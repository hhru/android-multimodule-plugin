package ru.hh.plugins.geminio.services.android

import com.android.tools.idea.npw.project.getModuleTemplates
import com.android.tools.idea.npw.project.getPackageForPath
import com.android.tools.idea.projectsystem.AndroidModulePaths
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.geminio.common.extensions.toSlashedFilePath
import ru.hh.plugins.geminio.logger.HHLogger
import java.io.File

internal data class GeminioNamedModuleTemplateContext(
    val namedModuleTemplate: NamedModuleTemplate,
    val initialPackageSuggestion: String,
)

/**
 * Builds an Android-specific module template context for the selected target directory.
 *
 * Geminio still relies on Android Studio project-model APIs to resolve module paths and initial
 * package suggestions, but this adapter is now separate from the custom UI and custom execution
 * runtime.
 */
internal fun AndroidFacet.createGeminioNamedModuleTemplateContext(
    targetDirectory: VirtualFile,
): GeminioNamedModuleTemplateContext {
    val originalModuleTemplates = getModuleTemplates(targetDirectory)
    check(originalModuleTemplates.isNotEmpty()) {
        "Cannot resolve Android module templates for target directory '${targetDirectory.path}'"
    }

    val moduleTemplate = originalModuleTemplates.first()

    /**
     * Sometimes AndroidFacet returns module paths without AIDL or SRC directories.
     *
     * We keep the fallback wrapper here so path resolution is stable for Geminio's own runtime.
     */
    val shouldReplaceAidlDirectory = moduleTemplate.paths.getAidlDirectory("stub.package") == null
    val shouldReplaceSrcDirectory = moduleTemplate.paths.getSrcDirectory("stub.package") == null

    HHLogger.d("Should replace AIDL directory: $shouldReplaceAidlDirectory")
    HHLogger.d("Should replace SRC directory: $shouldReplaceSrcDirectory")

    val safeTemplate = moduleTemplate.copy(
        paths = GeminioSafeAndroidModulePaths(
            original = moduleTemplate.paths,
            baseModuleName = moduleTemplate.name,
            shouldReplaceAidlDirectory = shouldReplaceAidlDirectory,
            shouldReplaceSrcDirectory = shouldReplaceSrcDirectory,
        ),
    )

    return GeminioNamedModuleTemplateContext(
        namedModuleTemplate = safeTemplate,
        initialPackageSuggestion = getPackageForPath(listOf(safeTemplate), targetDirectory).orEmpty(),
    )
}

private class GeminioSafeAndroidModulePaths(
    private val original: AndroidModulePaths,
    private val baseModuleName: String,
    private val shouldReplaceAidlDirectory: Boolean,
    private val shouldReplaceSrcDirectory: Boolean,
) : AndroidModulePaths {
    override val manifestDirectory: File?
        get() = original.manifestDirectory
    override val moduleRoot: File?
        get() = original.moduleRoot
    override val resDirectories: List<File>
        get() = original.resDirectories

    override fun getAidlDirectory(packageName: String?): File? {
        return if (shouldReplaceAidlDirectory) {
            original.moduleRoot
                ?.resolve("src/$baseModuleName/aidl" + packageName?.toSlashedFilePath().orEmpty())
        } else {
            original.getAidlDirectory(packageName)
        }
    }

    override fun getSrcDirectory(packageName: String?): File? {
        return if (shouldReplaceSrcDirectory) {
            original.moduleRoot
                ?.resolve("src/$baseModuleName/java" + packageName?.toSlashedFilePath().orEmpty())
        } else {
            original.getSrcDirectory(packageName)
        }
    }

    override fun getTestDirectory(packageName: String?): File? {
        return original.getTestDirectory(packageName)
    }

    override fun getUnitTestDirectory(packageName: String?): File? {
        return original.getUnitTestDirectory(packageName)
    }
}
