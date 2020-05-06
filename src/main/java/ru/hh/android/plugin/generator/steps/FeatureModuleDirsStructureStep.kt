package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import ru.hh.android.plugin.extensions.SLASH
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.enums.FeatureModuleType.CUSTOM_PATH
import ru.hh.android.plugin.model.enums.FeatureModuleType.STANDALONE


/**
 * Step for creating full feature module directories structure.
 *
 * Current scheme:
 *
 * -- module-name
 *    -- src
 *       -- main
 *          -- java
 *             -- package_name
 *                -- data
 *                   -- local
 *                      --  model
 *                   -- remote
 *                      -- api
 *                      -- model
 *                -- di
 *                   -- module
 *                   -- outer
 *                   -- provider
 *                -- domain
 *                   -- interactor
 *                   -- model
 *                   -- repository
 *                -- presentation
 *                   -- custom_view
 *                   -- model
 *                   -- presenter
 *                   -- view
 *          -- res
 *             -- layout
 */
class FeatureModuleDirsStructureStep(
        private val project: Project
) {

    companion object {
        const val KEY_MODULE_ROOT_FOLDER = "root"

        const val KEY_SRC_FOLDER = "src"
        const val KEY_MAIN_FOLDER = "main"
        const val KEY_JAVA_FOLDER = "java"


        const val KEY_RESOURCES_FOLDER = "res"
        const val KEY_RESOURCES_LAYOUT_FOLDER = "layout"

        const val KEY_PACKAGE_FOLDER = "content"

        const val KEY_DATA_FOLDER = "data"
        const val KEY_LOCAL_DATA_FOLDER = "content__data__local"
        const val KEY_LOCAL_DATA_MODEL_FOLDER = "content__data__local__model"

        const val KEY_REMOTE_DATA_FOLDER = "content__data__remote"
        const val KEY_REMOTE_DATA_API_FOLDER = "content__data__remote__api"
        const val KEY_REMOTE_DATA_MODEL_FOLDER = "content__data__remote__model"

        const val KEY_DI_FOLDER = "content__di"
        const val KEY_DI_MODULE_FOLDER = "content__di__module"
        const val KEY_DI_OUTER_FOLDER = "content__di__outer"
        const val KEY_DI_PROVIDER_FOLDER = "content__di__provider"

        const val KEY_DOMAIN_FOLDER = "content__domain"
        const val KEY_DOMAIN_INTERACTOR_FOLDER = "content__domain__interactor"
        const val KEY_DOMAIN_MODEL_FOLDER = "content__domain__model"
        const val KEY_DOMAIN_REPOSITORY_FOLDER = "content__domain__repository"

        const val KEY_PRESENTATION_FOLDER = "content__presentation"
        const val KEY_PRESENTATION_CUSTOM_VIEW_FOLDER = "content__presentation__custom_view"
        const val KEY_PRESENTATION_MODEL_FOLDER = "content__presentation__model"
        const val KEY_PRESENTATION_PRESENTER_FOLDER = "content__presentation__presenter"
        const val KEY_PRESENTATION_VIEW_FOLDER = "content__presentation__view"


        private const val PACKAGE_PARTS_DELIMITER = "."
    }


    fun execute(config: CreateModuleConfig): Map<String, PsiDirectory?> {
        val projectPsiDirectory = project.guessProjectDir()?.toPsiDirectory(project) ?: return mapOf()
        val typeRootPsiDirectory = when (config.params.moduleType) {
            STANDALONE -> projectPsiDirectory
            CUSTOM_PATH -> descendantSubdirectorySearch(projectPsiDirectory, config.params.customModuleTypePath)
            else -> descendantSubdirectorySearch(projectPsiDirectory, config.params.moduleType.folderPrefix)
        } ?: return mapOf()

        val modulePsiFolder = when (config.params.moduleType) {
            CUSTOM_PATH -> typeRootPsiDirectory
            else -> typeRootPsiDirectory.createSubdirectory(config.params.moduleName)
        }

        return mutableMapOf<String, PsiDirectory?>().apply {
            this[KEY_MODULE_ROOT_FOLDER] = modulePsiFolder

            this[KEY_SRC_FOLDER] = modulePsiFolder.createSubdirectory("src")
            this[KEY_MAIN_FOLDER] = this[KEY_SRC_FOLDER]?.createSubdirectory("main")
            this[KEY_JAVA_FOLDER] = this[KEY_MAIN_FOLDER]?.createSubdirectory("java")
            this[KEY_RESOURCES_FOLDER] = this[KEY_MAIN_FOLDER]?.createSubdirectory("res")
            this[KEY_RESOURCES_LAYOUT_FOLDER] = this[KEY_RESOURCES_FOLDER]?.createSubdirectory("layout")

            createPackageNameFolder(config)

            // data
            this[KEY_DATA_FOLDER] = this[KEY_PACKAGE_FOLDER]?.createSubdirectory("data")
            this[KEY_LOCAL_DATA_FOLDER] = this[KEY_DATA_FOLDER]?.createSubdirectory("local")
            this[KEY_LOCAL_DATA_MODEL_FOLDER] = this[KEY_LOCAL_DATA_FOLDER]?.createSubdirectory("model")
            this[KEY_REMOTE_DATA_FOLDER] = this[KEY_DATA_FOLDER]?.createSubdirectory("remote")
            this[KEY_REMOTE_DATA_API_FOLDER] = this[KEY_REMOTE_DATA_FOLDER]?.createSubdirectory("api")
            this[KEY_REMOTE_DATA_MODEL_FOLDER] = this[KEY_REMOTE_DATA_FOLDER]?.createSubdirectory("model")

            // di
            this[KEY_DI_FOLDER] = this[KEY_PACKAGE_FOLDER]?.createSubdirectory("di")
            this[KEY_DI_MODULE_FOLDER] = this[KEY_DI_FOLDER]?.createSubdirectory("module")
            this[KEY_DI_OUTER_FOLDER] = this[KEY_DI_FOLDER]?.createSubdirectory("outer")
            this[KEY_DI_PROVIDER_FOLDER] = this[KEY_DI_FOLDER]?.createSubdirectory("provider")

            // domain
            this[KEY_DOMAIN_FOLDER] = this[KEY_PACKAGE_FOLDER]?.createSubdirectory("domain")
            this[KEY_DOMAIN_INTERACTOR_FOLDER] = this[KEY_DOMAIN_FOLDER]?.createSubdirectory("interactor")
            this[KEY_DOMAIN_MODEL_FOLDER] = this[KEY_DOMAIN_FOLDER]?.createSubdirectory("model")
            this[KEY_DOMAIN_REPOSITORY_FOLDER] = this[KEY_DOMAIN_FOLDER]?.createSubdirectory("repository")

            // presentation
            this[KEY_PRESENTATION_FOLDER] = this[KEY_PACKAGE_FOLDER]?.createSubdirectory("presentation")
            this[KEY_PRESENTATION_VIEW_FOLDER] = this[KEY_PRESENTATION_FOLDER]?.createSubdirectory("view")
            this[KEY_PRESENTATION_PRESENTER_FOLDER] =
                    this[KEY_PRESENTATION_FOLDER]?.createSubdirectory("presenter")
            this[KEY_PRESENTATION_MODEL_FOLDER] = this[KEY_PRESENTATION_FOLDER]?.createSubdirectory("model")
            this[KEY_PRESENTATION_CUSTOM_VIEW_FOLDER] =
                    this[KEY_PRESENTATION_FOLDER]?.createSubdirectory("custom_view")
        }
    }


    private fun descendantSubdirectorySearch(
            rootPsiDirectory: PsiDirectory,
            startPath: String
    ): PsiDirectory? {
        var nextPsiDirectory: PsiDirectory? = rootPsiDirectory
        val splittedFolderNames = startPath.split(Char.SLASH).filter { it.isNotBlank() }
        for (item in splittedFolderNames) {
            nextPsiDirectory = nextPsiDirectory?.findSubdirectory(item) ?: nextPsiDirectory?.createSubdirectory(item)
        }
        return nextPsiDirectory
    }

    private fun MutableMap<String, PsiDirectory?>.createPackageNameFolder(config: CreateModuleConfig) {
        val packageParts = config.params.packageName.split(PACKAGE_PARTS_DELIMITER)

        var prevPsiDirectory = this[KEY_JAVA_FOLDER]
        var lastPsiDirectory: PsiDirectory? = null
        for (part in packageParts) {
            lastPsiDirectory = prevPsiDirectory?.createSubdirectory(part)
            prevPsiDirectory = lastPsiDirectory
        }

        this[KEY_PACKAGE_FOLDER] = lastPsiDirectory
    }

}