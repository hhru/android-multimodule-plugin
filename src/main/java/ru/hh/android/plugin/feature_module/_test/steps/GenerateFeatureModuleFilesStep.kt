package ru.hh.android.plugin.feature_module._test.steps

import com.android.tools.idea.lang.proguard.ProguardFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.hh.android.plugin.feature_module._test.templates.gitignore.IgnoreFileType
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateData
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateFactory
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter

class GenerateFeatureModuleFilesStep(
        private val newTemplateFactory: NewTemplateFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) {

    fun generate(config: CreateModuleConfig, dirsMap: Map<String, PsiDirectory?>) {
        val templates = listOf(
                NewTemplateData(
                        templateName = "AndroidManifest.xml.ftl",
                        targetFileName = "AndroidManifest.xml",
                        fileType = XmlFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["main"]
                ),
                NewTemplateData(
                        templateName = "gitignore.ftl",
                        targetFileName = ".gitignore",
                        fileType = IgnoreFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["root"]
                ),
                NewTemplateData(
                        templateName = "proguard-rules.pro.ftl",
                        targetFileName = "proguard-rules.pro",
                        fileType = ProguardFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["root"]
                ),
                NewTemplateData(
                        templateName = "build.gradle.ftl",
                        targetFileName = "build.gradle",
                        fileType = GroovyFileType.GROOVY_FILE_TYPE,
                        targetPsiDirectory = dirsMap["root"]
                ),
                NewTemplateData(
                        templateName = "DIModule.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Module.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__di__module"]
                ),
                NewTemplateData(
                        templateName = "ApiProvider.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}ApiProvider.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__di__provider"]
                ),
                NewTemplateData(
                        templateName = "ModuleApi.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Api.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__data__remote__api"]
                ),
                NewTemplateData(
                        templateName = "ModuleInteractor.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Interactor.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__domain__interactor"]
                ),
                NewTemplateData(
                        templateName = "ModuleRepository.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}Repository.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__domain__repository"]
                ),
                NewTemplateData(
                        templateName = "ModuleRepositoryImpl.kt.ftl",
                        targetFileName = "${config.mainParams.formattedLibraryName}RepositoryImpl.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__domain__repository"]
                )
        )


        val properties = createModuleConfigConverter.convert(config)
        templates.forEach { templateData ->
            val psiFile = newTemplateFactory.createFromTemplate(templateData, properties.toFreeMarkerDataModel())
            templateData.targetPsiDirectory?.add(psiFile)
        }
    }

}