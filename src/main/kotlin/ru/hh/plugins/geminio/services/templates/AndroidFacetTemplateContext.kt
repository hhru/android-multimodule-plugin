package ru.hh.plugins.geminio.services.templates

import com.android.tools.idea.npw.project.getModuleTemplates
import com.android.tools.idea.npw.project.getPackageForPath
import com.android.tools.idea.projectsystem.AndroidModulePaths
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.extensions.toSlashedFilePath
import ru.hh.plugins.logger.HHLogger
import java.io.File

internal data class GeminioNamedModuleTemplateContext(
    val namedModuleTemplate: NamedModuleTemplate,
    val initialPackageSuggestion: String,
)

/**
 * Resolves a safe Android module template description for the selected target directory.
 *
 * The result is shared between the old Android Studio wizard flow and the new custom Geminio UI,
 * so both paths keep using identical module paths and initial package suggestion logic.
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
     * `ModuleTemplateDataBuilder`/`RenderTemplateModel` expect those paths to exist, otherwise the
     * old Android template runtime crashes or silently skips file generation. We keep the same
     * fallback wrapper here so both the legacy wizard flow and the custom Geminio UI use identical
     * module path semantics.
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
