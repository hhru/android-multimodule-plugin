package ru.hh.android.plugin.feature_module._test

import com.android.tools.idea.lang.proguard.ProguardFileType
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.hh.android.plugin.feature_module._test.gitignore.IgnoreFileType
import ru.hh.android.plugin.feature_module.component.templates_factory.TemplatesFactory
import ru.hh.android.plugin.feature_module.extensions.getRootModulePath
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType.*
import java.io.StringWriter


class FeatureModuleDirsStructureStep {

    fun createDirsStructure(project: Project, config: CreateModuleConfig): Map<String, PsiDirectory?> {
        val projectPsiDirectory = project.baseDir.toPsiDirectory(project) ?: return mapOf()

        val moduleRootPsiFolder = when (config.mainParams.moduleType) {
            STANDALONE -> projectPsiDirectory
            FEATURE_MODULE -> projectPsiDirectory.findSubdirectory("features")
            CORE_MODULE -> projectPsiDirectory.findSubdirectory("core")
        }

        val confirmedModuleRootPsiFolder = moduleRootPsiFolder ?: when (config.mainParams.moduleType) {
            STANDALONE -> {
                projectPsiDirectory
            }

            FEATURE_MODULE -> {
                projectPsiDirectory.createSubdirectory("features")
            }
            CORE_MODULE -> {
                projectPsiDirectory.createSubdirectory("core")
            }
        }
        val modulePsiFolder = confirmedModuleRootPsiFolder.createSubdirectory(config.mainParams.moduleName)

        return mutableMapOf<String, PsiDirectory?>().apply {
            this["root"] = modulePsiFolder

            this["src"] = modulePsiFolder.createSubdirectory("src")
            this["main"] = this["src"]?.createSubdirectory("main")
            this["java"] = this["main"]?.createSubdirectory("java")
            this["res"] = this["main"]?.createSubdirectory("res")

            // Создание папок для package-а
            val packageParts = config.mainParams.packageName.split(".")
            var prevPsiDirectory = this["java"]
            var lastPsiDirectory: PsiDirectory? = null
            for (part in packageParts) {
                lastPsiDirectory = prevPsiDirectory?.createSubdirectory(part)
                prevPsiDirectory = lastPsiDirectory
            }
            this["content"] = lastPsiDirectory

            // создание папок для нашей структуры. [new]
            // Хотелось бы, конечно, иметь какую-то нормальную конфигурацию, из файлика, но пока и так норм.

            // data
            this["content__data"] = this["content"]?.createSubdirectory("data")
            this["content__data__local"] = this["content__data"]?.createSubdirectory("local")
            this["content__data__remote"] = this["content__data"]?.createSubdirectory("remote")
            this["content__data__remote__api"] = this["content__data__remote"]?.createSubdirectory("api")
            this["content__data__remote__model"] = this["content__data__remote"]?.createSubdirectory("model")

            // di
            this["content__di"] = this["content"]?.createSubdirectory("di")
            this["content__di__module"] = this["content__di"]?.createSubdirectory("module")
            this["content__di__provider"] = this["content__di"]?.createSubdirectory("provider")
            this["content__di__outer"] = this["content__di"]?.createSubdirectory("outer")

            // domain
            this["content__domain"] = this["content"]?.createSubdirectory("domain")
            this["content__domain__model"] = this["content__domain"]?.createSubdirectory("model")
            this["content__domain__interactor"] = this["content__domain"]?.createSubdirectory("interactor")
            this["content__domain__repository"] = this["content__domain"]?.createSubdirectory("repository")

            // presentation
            this["content__presentation"] = this["content"]?.createSubdirectory("presentation")
            this["content__presentation__view"] = this["content__presentation"]?.createSubdirectory("view")
            this["content__presentation__presenter"] = this["content__presentation"]?.createSubdirectory("presenter")
            this["content__presentation__model"] = this["content__presentation"]?.createSubdirectory("model")
            this["content__presentation__custom_view"] = this["content__presentation"]?.createSubdirectory("custom_view")
        }
    }
}

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
                        targetFileName = "${config.mainParams.formattedLibraryName}DI.kt",
                        fileType = KotlinFileType.INSTANCE,
                        targetPsiDirectory = dirsMap["content__di"]
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


class TestComponent(
        private val project: Project,
        private val newTemplateFactory: NewTemplateFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    fun create(config: CreateModuleConfig) {
        val featureModuleDirsStructureStep = FeatureModuleDirsStructureStep()
        val dirsMap = featureModuleDirsStructureStep.createDirsStructure(project, config)

        val generateFeatureModuleFilesStep = GenerateFeatureModuleFilesStep(newTemplateFactory, createModuleConfigConverter)
        generateFeatureModuleFilesStep.generate(config, dirsMap)





    }

}


class NewTemplateFactory(
        private val project: Project
) : ProjectComponent {


    companion object {
        private const val TEMPLATES_DIR_PATH = "/templates"
    }


    private val config by lazy {
        Configuration(Configuration.VERSION_2_3_28).apply {
            setClassForTemplateLoading(TemplatesFactory::class.java, TEMPLATES_DIR_PATH)

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }


    fun createFromTemplate(templateData: NewTemplateData, data: Map<String, Any>): PsiFile {
        val template = config.getTemplate(templateData.templateName)

        val text = StringWriter().use { writer ->
            template.process(data, writer)

            writer.buffer.toString()
        }

        return PsiFileFactory.getInstance(project).createFileFromText(templateData.targetFileName, templateData.fileType, text)
    }

}

data class NewTemplateData(
        val templateName: String,
        val targetFileName: String,
        val fileType: FileType,
        val targetPsiDirectory: PsiDirectory?
)

