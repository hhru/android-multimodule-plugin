package ru.hh.android.plugin.generator.steps

import com.android.tools.idea.lang.proguard.ProguardFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_DI_MODULE_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_DI_PROVIDER_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_DOMAIN_INTERACTOR_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_DOMAIN_REPOSITORY_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_MAIN_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_MODULE_ROOT_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_PRESENTATION_PRESENTER_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_PRESENTATION_VIEW_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_REMOTE_DATA_API_FOLDER
import ru.hh.android.plugin.generator.steps.FeatureModuleDirsStructureStep.Companion.KEY_RESOURCES_LAYOUT_FOLDER
import ru.hh.android.plugin.generator.templates.FileTemplateData
import ru.hh.android.plugin.generator.templates.ModuleFilesFactory
import ru.hh.android.plugin.generator.templates.gitignore.IgnoreFileType
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.converter.CreateModuleConfigConverter


class GenerateFeatureModuleFilesStep(
        private val createModuleConfigConverter: CreateModuleConfigConverter,
        private val moduleFilesFactory: ModuleFilesFactory
) {

    fun execute(config: CreateModuleConfig, params: Map<String, PsiDirectory?>) {
        val templates = mutableListOf<FileTemplateData>().apply {
            this += FileTemplateData(
                    templateFileName = "AndroidManifest.xml.ftl",
                    outputFileName = "AndroidManifest.xml",
                    outputFileType = XmlFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MAIN_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = "gitignore.ftl",
                    outputFileName = ".gitignore",
                    outputFileType = IgnoreFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = "proguard-rules.pro.ftl",
                    outputFileName = "proguard-rules.pro",
                    outputFileType = ProguardFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = "build.gradle.ftl",
                    outputFileName = "build.gradle",
                    outputFileType = GroovyFileType.GROOVY_FILE_TYPE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = "DIModule.kt.ftl",
                    outputFileName = "${config.mainParams.formattedLibraryName}Module.kt",
                    outputFileType = KotlinFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_DI_MODULE_FOLDER]
            )

            if (config.mainParams.needCreateAPIInterface) {
                this += FileTemplateData(
                        templateFileName = "ApiProvider.kt.ftl",
                        outputFileName = "${config.mainParams.formattedLibraryName}ApiProvider.kt",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DI_PROVIDER_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = "ModuleApi.kt.ftl",
                        outputFileName = "${config.mainParams.formattedLibraryName}Api.kt",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_REMOTE_DATA_API_FOLDER]
                )
            }
            if (config.mainParams.needCreateRepositoryWithInteractor) {
                this += FileTemplateData(
                        templateFileName = "ModuleInteractor.kt.ftl",
                        outputFileName = "${config.mainParams.formattedLibraryName}Interactor.kt",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DOMAIN_INTERACTOR_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = "ModuleRepository.kt.ftl",
                        outputFileName = "${config.mainParams.formattedLibraryName}Repository.kt",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DOMAIN_REPOSITORY_FOLDER]
                )
                if (config.mainParams.needCreateInterfaceForRepository) {
                    this += FileTemplateData(
                            templateFileName = "ModuleRepositoryImpl.kt.ftl",
                            outputFileName = "${config.mainParams.formattedLibraryName}RepositoryImpl.kt",
                            outputFileType = KotlinFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_DOMAIN_REPOSITORY_FOLDER]
                    )
                }

                if (config.mainParams.needCreatePresentationLayer) {
                    this += FileTemplateData(
                            templateFileName = "ModuleFragment.kt.ftl",
                            outputFileName = "${config.mainParams.formattedLibraryName}Fragment.kt",
                            outputFileType = KotlinFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_PRESENTATION_VIEW_FOLDER]
                    )
                    this += FileTemplateData(
                            templateFileName = "ModuleView.kt.ftl",
                            outputFileName = "${config.mainParams.formattedLibraryName}View.kt",
                            outputFileType = KotlinFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_PRESENTATION_VIEW_FOLDER]
                    )
                    this += FileTemplateData(
                            templateFileName = "ModulePresenter.kt.ftl",
                            outputFileName = "${config.mainParams.formattedLibraryName}Presenter.kt",
                            outputFileType = KotlinFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_PRESENTATION_PRESENTER_FOLDER]
                    )
                    this += FileTemplateData(
                            templateFileName = "fragment_module.xml.ftl",
                            outputFileName = "fragment_${config.mainParams.layoutName}.xml",
                            outputFileType = XmlFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_RESOURCES_LAYOUT_FOLDER]
                    )
                }
            }
        }


        val properties = createModuleConfigConverter.convert(config)
        templates.forEach { templateData ->
            val psiFile = moduleFilesFactory.createFromTemplate(templateData, properties.toFreeMarkerDataModel())
            templateData.outputFilePsiDirectory?.add(psiFile)
        }
    }

}