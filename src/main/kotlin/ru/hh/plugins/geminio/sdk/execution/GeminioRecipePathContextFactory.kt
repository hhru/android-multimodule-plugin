package ru.hh.plugins.geminio.sdk.execution

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.geminio.models.GeminioAndroidModulePaths
import ru.hh.plugins.geminio.models.GeminioSourceSetConfig
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext
import ru.hh.plugins.geminio.services.android.createGeminioNamedModuleTemplateContext

/**
 * Builds path aliases used by Geminio expressions for both existing-module and new-module flows.
 */
internal object GeminioRecipePathContextFactory {

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
                "Cannot resolve src directory for package '$targetPackageName' in module '${templateContext.namedModuleTemplate.name}'"
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

    fun createForNewModule(
        currentDirPath: String,
        newModuleRootDirectoryPath: String,
        moduleName: String,
        packageName: String,
        sourceSet: String,
        sourceCodeFolderName: String,
    ): GeminioFormPathContext {
        val modulePaths = GeminioAndroidModulePaths(
            basePath = newModuleRootDirectoryPath,
            moduleName = moduleName,
            sourceSetConfig = GeminioSourceSetConfig(
                sourceSet = sourceSet,
                sourceCodeFolderName = sourceCodeFolderName,
            ),
        )

        return GeminioFormPathContext(
            srcOut = modulePaths.getSrcDirectory(packageName).absolutePath,
            resOut = modulePaths.resDirectories.firstOrNull()?.absolutePath,
            manifestOut = modulePaths.manifestDirectory.absolutePath,
            rootOut = modulePaths.moduleRoot.absolutePath,
            currentDirOut = currentDirPath,
        )
    }

    private fun VirtualFile.toCurrentDirPath(): String {
        return if (isDirectory) path else parent.path
    }
}
