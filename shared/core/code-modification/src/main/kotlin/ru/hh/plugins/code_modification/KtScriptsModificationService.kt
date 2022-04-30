package ru.hh.plugins.code_modification

import com.android.tools.build.jetifier.core.utils.Log
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.hh.plugins.extensions.kotlin.getProperty
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.psi_utils.kotlin.createBuildGradleDependencyElement
import ru.hh.plugins.psi_utils.kotlin.createGradlePluginElement
import ru.hh.plugins.psi_utils.kotlin.createImport
import ru.hh.plugins.psi_utils.kotlin.createModuleField
import ru.hh.plugins.psi_utils.kotlin.getOrCreateBuildGradleDependenciesBlock
import ru.hh.plugins.psi_utils.kotlin.getOrCreateGradlePluginsBlock
import ru.hh.plugins.psi_utils.reformatWithCodeStyle

class KtScriptsModificationService {

    fun addGradlePlugins(ktFile: KtFile, pluginsIds: List<String>) {
        val pluginsBlock = ktFile.getOrCreateGradlePluginsBlock()

        val existingPlugins = pluginsBlock
            .children
            .filterIsInstance<KtCallExpression>()
            .map { it.text.removePrefix("id(").removeSuffix(")") }
            .toSet()

        val ktPsiFactory = KtPsiFactory(ktFile.project)
        pluginsIds.forEach { pluginId ->
            if (existingPlugins.contains(pluginId).not()) {
                val element = ktPsiFactory.createGradlePluginElement(pluginId)
                pluginsBlock.addBefore(ktPsiFactory.createNewLine(), pluginsBlock.rBrace)
                pluginsBlock.addBefore(element, pluginsBlock.rBrace)
            }
        }

        ktFile.reformatWithCodeStyle()
    }

    fun addKoinModule(ktFile: KtFile, koinModule: String, koinModuleImport: String) {
        val ktPsiFactory = KtPsiFactory(ktFile.project)

        ktFile.importList?.add(ktPsiFactory.createNewLine())
        ktFile.importList?.add(ktPsiFactory.createImport(koinModuleImport))

        val property = ktFile.getProperty()

        val callExpression = property?.children?.find { it is KtCallExpression } as? KtCallExpression
        val argumentList = callExpression?.valueArgumentList
        val lastArgumentElement = argumentList?.lastChild

        lastArgumentElement?.let {
            argumentList.addBefore(ktPsiFactory.createComma(), it)
            argumentList.addBefore(ktPsiFactory.createNewLine(), it)
            argumentList.addBefore(ktPsiFactory.createModuleField(koinModule), it)
        }

        ktFile.reformatWithCodeStyle()
    }

    fun addGradleDependencies(ktFile: KtFile, gradleDependencies: List<BuildGradleDependency>) {
        val dependenciesBodyBlock = ktFile.getOrCreateBuildGradleDependenciesBlock()

        val existingDependencies = dependenciesBodyBlock
            .children
            .filterIsInstance<KtCallExpression>()
            .map { ktCallExpression ->
                ktCallExpression.text
                    .removePrefix("${ktCallExpression.calleeExpression?.text}(")
                    .removeSuffix(")")
                    .removePrefix("project(\"")
                    .removeSuffix("\")")
            }
            .toSet()

        val ktPsiFactory = KtPsiFactory(ktFile.project)
        gradleDependencies.forEach { dependency ->
            if (existingDependencies.contains(dependency.value).not()) {
                val element = ktPsiFactory.createBuildGradleDependencyElement(dependency)
                dependenciesBodyBlock.addBefore(ktPsiFactory.createNewLine(), dependenciesBodyBlock.rBrace)
                dependenciesBodyBlock.addBefore(element, dependenciesBodyBlock.rBrace)
            }
        }

        ktFile.reformatWithCodeStyle()
    }
}
