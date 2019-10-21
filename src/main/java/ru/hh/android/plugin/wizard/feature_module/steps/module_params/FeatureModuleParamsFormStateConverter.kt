package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import ru.hh.android.plugin.core.model.ModelConverter
import ru.hh.android.plugin.extensions.SLASH
import ru.hh.android.plugin.extensions.replaceMultipleSplashes
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.FeatureModuleType


class FeatureModuleParamsFormStateConverter(
        private val project: Project
) : ModelConverter<FeatureModuleParamsFormState, MainParametersHolder> {

    override fun convert(item: FeatureModuleParamsFormState): MainParametersHolder {
        val moduleName = item.moduleName
        val moduleType = item.moduleType

        return MainParametersHolder(
                moduleName = moduleName,
                packageName = item.packageName,
                moduleType = moduleType,
                settingsGradleModulePath = getModulePathForSettingsGradle(item),
                rootModuleDirectory = getRootModuleDirectory(item),
                enabledSettings = item.enabledFeatures
        )
    }


    private fun getModulePathForSettingsGradle(formState: FeatureModuleParamsFormState): String {
        val typeRootFolder = when (formState.moduleType) {
            FeatureModuleType.CUSTOM_PATH -> formState.customModuleTypePath
            else -> "/${formState.moduleType.folderPrefix}"
        }

        return "./${typeRootFolder}/${formState.moduleName}".replaceMultipleSplashes()
    }

    private fun getRootModuleDirectory(formState: FeatureModuleParamsFormState): PsiDirectory? {
        val projectPsiDirectory = project.guessProjectDir()?.toPsiDirectory(project)

        return projectPsiDirectory?.let { rootPsiDirectory ->
            when (formState.moduleType) {
                FeatureModuleType.STANDALONE -> rootPsiDirectory
                FeatureModuleType.CUSTOM_PATH -> descendantSubdirectorySearch(rootPsiDirectory, formState)
                else -> rootPsiDirectory.findSubdirectory(formState.moduleType.folderPrefix)
            }
        }
    }

    private fun descendantSubdirectorySearch(
            rootPsiDirectory: PsiDirectory,
            formState: FeatureModuleParamsFormState
    ): PsiDirectory? {
        var nextPsiDirectory: PsiDirectory? = rootPsiDirectory
        val splittedFolderNames = formState.customModuleTypePath.split(Char.SLASH)
        for (item in splittedFolderNames) {
            nextPsiDirectory = nextPsiDirectory?.findSubdirectory(item)
        }
        return nextPsiDirectory
    }

}