package ru.hh.android.plugin.feature_module._test.steps

import com.android.tools.idea.lang.proguard.ProguardFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateData
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateFactory
import ru.hh.android.plugin.feature_module._test.templates.gitignore.IgnoreFileType
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter

class GenerateFeatureModuleFilesStep(
        private val newTemplateFactory: NewTemplateFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) {

    fun generate(config: CreateModuleConfig, dirsMap: Map<String, PsiDirectory?>) {
        val templates = mutableListOf<NewTemplateData>().apply {
            this += NewTemplateData(
                    templateName = "AndroidManifest.xml.ftl",
                    targetFileName = "AndroidManifest.xml",
                    fileType = XmlFileType.INSTANCE,
                    targetPsiDirectory = dirsMap["main"]
            )
            this += NewTemplateData(
                    templateName = "gitignore.ftl",
                    targetFileName = ".gitignore",
                    fileType = IgnoreFileType.INSTANCE,
                    targetPsiDirectory = dirsMap["root"]
            )
            this += NewTemplateData(
                    templateName = "proguard-rules.pro.ftl",
                    targetFileName = "proguard-rules.pro",
                    fileType = ProguardFileType.INSTANCE,
                    targetPsiDirectory = dirsMap["root"]
            )
            this += NewTemplateData(
                    templateName = "build.gradle.ftl",
                    targetFileName = "build.gradle",
                    fileType = GroovyFileType.GROOVY_FILE_TYPE,
                    targetPsiDirectory = dirsMap["root"]
            )
            this += NewTemplateData(
                    templateName = "DIModule.kt.ftl",
                    targetFileName = "${config.mainParams.formattedLibraryName}Module.kt",
                    fileType = KotlinFileType.INSTANCE,
                    targetPsiDirectory = dirsMap["content__di__module"]
            )

            if (config.mainParams.needCreateAPIInterface) {
                this += NewTemplateData(
                        templateName = "ApiProvider.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}ApiProvider.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__di__provider"]
                )
                this += NewTemplateData(
                        templateName = "ModuleApi.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Api.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__data__remote__api"]
                )
            }
            if (config.mainParams.needCreateRepositoryWithInteractor) {
                this += NewTemplateData(
                        templateName = "ModuleInteractor.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Interactor.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__domain__interactor"]
                )
                this += NewTemplateData(
                        templateName = "ModuleRepository.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Repository.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__domain__repository"]
                )
                if (config.mainParams.needCreateInterfaceForRepository) {
                    this += NewTemplateData(
                            templateName = "ModuleRepositoryImpl.kt.ftl",
                            targetFileName = "${config.mainParams.formattedLibraryName}RepositoryImpl.kt",
                            fileType = KotlinFileType.INSTANCE,
                            targetPsiDirectory = dirsMap["content__domain__repository"]
                    )
                }

                if (config.mainParams.needCreatePresentationLayer) {
                    this += NewTemplateData(
                            templateName = "ModuleFragment.kt.ftl",
                            targetFileName = "${config.mainParams.formattedLibraryName}Fragment.kt",
                            fileType = KotlinFileType.INSTANCE,
                            targetPsiDirectory = dirsMap["content__presentation__view"]
                    )
                    this += NewTemplateData(
                            templateName = "ModuleView.kt.ftl",
                            targetFileName = "${config.mainParams.formattedLibraryName}View.kt",
                            fileType = KotlinFileType.INSTANCE,
                            targetPsiDirectory = dirsMap["content__presentation__view"]
                    )
                    this += NewTemplateData(
                            templateName = "ModulePresenter.kt.ftl",
                            targetFileName = "${config.mainParams.formattedLibraryName}Presenter.kt",
                            fileType = KotlinFileType.INSTANCE,
                            targetPsiDirectory = dirsMap["content__presentation__view"]
                    )
                    this += NewTemplateData(
                            templateName = "fragment_module.xml.ftl",
                            targetFileName = "fragment_${config.mainParams.layoutName}.xml",
                            fileType = XmlFileType.INSTANCE,
                            targetPsiDirectory = dirsMap["layout"]
                    )
                }
            }
        }


        val properties = createModuleConfigConverter.convert(config)
        templates.forEach { templateData ->
            val psiFile = newTemplateFactory.createFromTemplate(templateData, properties.toFreeMarkerDataModel())
            templateData.targetPsiDirectory?.add(psiFile)
        }
    }

}