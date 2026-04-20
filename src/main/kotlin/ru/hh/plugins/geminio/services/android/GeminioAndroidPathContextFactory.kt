package ru.hh.plugins.geminio.services.android

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext

/**
 * Builds Geminio path aliases from Android-specific module metadata.
 */
internal object GeminioAndroidPathContextFactory {

    internal data class NewModulePathRequest(
        val currentDirPath: String,
        val newModuleRootDirectoryPath: String,
        val moduleName: String,
        val packageName: String,
        val sourceSet: String,
        val sourceCodeFolderName: String,
    )

    fun createForExistingModule(
        facet: AndroidFacet,
        targetDirectory: VirtualFile,
        targetPackageName: String,
    ): GeminioFormPathContext {
        val templateContext = facet.createGeminioNamedModuleTemplateContext(targetDirectory)
        val modulePaths = templateContext.namedModuleTemplate.paths
        val moduleRoot = requireNotNull(modulePaths.moduleRoot) {
            "Cannot resolve module root for Android module '${templateContext.namedModuleTemplate.name}'"
        }

        return GeminioFormPathContext(
            srcOut = requireNotNull(modulePaths.getSrcDirectory(targetPackageName)) {
                "Cannot resolve src directory for package '$targetPackageName' " +
                    "in module '${templateContext.namedModuleTemplate.name}'"
            }.absolutePath,
            resOut = modulePaths.resDirectories.firstOrNull()
                ?.absolutePath
                ?: moduleRoot.resolve("src/main/res").absolutePath,
            manifestOut = modulePaths.manifestDirectory?.absolutePath
                ?: moduleRoot.resolve("src/main").absolutePath,
            rootOut = moduleRoot.absolutePath,
            currentDirOut = targetDirectory.toCurrentDirPath(),
        )
    }

    fun createForNewModule(request: NewModulePathRequest): GeminioFormPathContext {
        val modulePaths = GeminioAndroidModulePaths(
            basePath = request.newModuleRootDirectoryPath,
            moduleName = request.moduleName,
            sourceSetConfig = GeminioSourceSetConfig(
                sourceSet = request.sourceSet,
                sourceCodeFolderName = request.sourceCodeFolderName,
            ),
        )

        return GeminioFormPathContext(
            srcOut = modulePaths.getSrcDirectory(request.packageName).absolutePath,
            resOut = modulePaths.resDirectories.firstOrNull()?.absolutePath,
            manifestOut = modulePaths.manifestDirectory.absolutePath,
            rootOut = modulePaths.moduleRoot.absolutePath,
            currentDirOut = request.currentDirPath,
        )
    }

    private fun VirtualFile.toCurrentDirPath(): String {
        return if (isDirectory) path else parent.path
    }
}
