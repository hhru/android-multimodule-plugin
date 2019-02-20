package ru.hh.android.plugin.feature_module._test.steps

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType.*

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