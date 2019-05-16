package ru.hh.android.plugin.generator.steps

import com.android.tools.idea.lang.proguard.ProguardFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.hh.android.plugin.TemplatesFilesConstants.ANDROID_MANIFEST_OUTPUT
import ru.hh.android.plugin.TemplatesFilesConstants.ANDROID_MANIFEST_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.BUILD_GRADLE_OUTPUT
import ru.hh.android.plugin.TemplatesFilesConstants.BUILD_GRADLE_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.DI_MODULE_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.DI_MODULE_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.EMPTY_FRAGMENT_LAYOUT_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.GITIGNORE_OUTPUT
import ru.hh.android.plugin.TemplatesFilesConstants.GITIGNORE_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_FRAGMENT_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_FRAGMENT_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_INTERACTOR_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_INTERACTOR_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_PRESENTER_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_PRESENTER_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_REPOSITORY_IMPL_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_REPOSITORY_IMPL_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_REPOSITORY_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_REPOSITORY_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_VIEW_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.MODULE_VIEW_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.PROGUARD_OUTPUT
import ru.hh.android.plugin.TemplatesFilesConstants.PROGUARD_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.REMOTE_API_INTERFACE_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.REMOTE_API_INTERFACE_TEMPLATE
import ru.hh.android.plugin.TemplatesFilesConstants.REMOTE_API_PROVIDER_OUTPUT_SUFFIX
import ru.hh.android.plugin.TemplatesFilesConstants.REMOTE_API_PROVIDER_TEMPLATE
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
                    templateFileName = ANDROID_MANIFEST_TEMPLATE,
                    outputFileName = ANDROID_MANIFEST_OUTPUT,
                    outputFileType = XmlFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MAIN_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = GITIGNORE_TEMPLATE,
                    outputFileName = GITIGNORE_OUTPUT,
                    outputFileType = IgnoreFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = PROGUARD_TEMPLATE,
                    outputFileName = PROGUARD_OUTPUT,
                    outputFileType = ProguardFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = BUILD_GRADLE_TEMPLATE,
                    outputFileName = BUILD_GRADLE_OUTPUT,
                    outputFileType = GroovyFileType.GROOVY_FILE_TYPE,
                    outputFilePsiDirectory = params[KEY_MODULE_ROOT_FOLDER]
            )
            this += FileTemplateData(
                    templateFileName = DI_MODULE_TEMPLATE,
                    outputFileName = "${config.mainParams.formattedLibraryName}$DI_MODULE_OUTPUT_SUFFIX",
                    outputFileType = KotlinFileType.INSTANCE,
                    outputFilePsiDirectory = params[KEY_DI_MODULE_FOLDER]
            )

            if (config.mainParams.needCreateAPIInterface) {
                this += FileTemplateData(
                        templateFileName = REMOTE_API_PROVIDER_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$REMOTE_API_PROVIDER_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DI_PROVIDER_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = REMOTE_API_INTERFACE_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$REMOTE_API_INTERFACE_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_REMOTE_DATA_API_FOLDER]
                )
            }
            if (config.mainParams.needCreateRepositoryWithInteractor) {
                this += FileTemplateData(
                        templateFileName = MODULE_INTERACTOR_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_INTERACTOR_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DOMAIN_INTERACTOR_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = MODULE_REPOSITORY_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_REPOSITORY_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_DOMAIN_REPOSITORY_FOLDER]
                )
                if (config.mainParams.needCreateInterfaceForRepository) {
                    this += FileTemplateData(
                            templateFileName = MODULE_REPOSITORY_IMPL_TEMPLATE,
                            outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_REPOSITORY_IMPL_OUTPUT_SUFFIX",
                            outputFileType = KotlinFileType.INSTANCE,
                            outputFilePsiDirectory = params[KEY_DOMAIN_REPOSITORY_FOLDER]
                    )
                }
            }

            if (config.mainParams.needCreatePresentationLayer) {
                this += FileTemplateData(
                        templateFileName = MODULE_FRAGMENT_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_FRAGMENT_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_PRESENTATION_VIEW_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = MODULE_VIEW_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_VIEW_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_PRESENTATION_VIEW_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = MODULE_PRESENTER_TEMPLATE,
                        outputFileName = "${config.mainParams.formattedLibraryName}$MODULE_PRESENTER_OUTPUT_SUFFIX",
                        outputFileType = KotlinFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_PRESENTATION_PRESENTER_FOLDER]
                )
                this += FileTemplateData(
                        templateFileName = EMPTY_FRAGMENT_LAYOUT_TEMPLATE,
                        outputFileName = "fragment_${config.mainParams.layoutName}.xml",
                        outputFileType = XmlFileType.INSTANCE,
                        outputFilePsiDirectory = params[KEY_RESOURCES_LAYOUT_FOLDER]
                )
            }
        }


        val properties = createModuleConfigConverter.convert(config)
        templates.forEach { templateData ->
            val psiFile = moduleFilesFactory.createFromTemplate(templateData, properties.toFreeMarkerDataModel())
            templateData.outputFilePsiDirectory?.add(psiFile)
        }
    }

}