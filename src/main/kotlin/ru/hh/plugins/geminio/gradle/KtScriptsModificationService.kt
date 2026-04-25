package ru.hh.plugins.geminio.gradle

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.hh.plugins.geminio.gradle.model.BuildGradleDependency
import ru.hh.plugins.geminio.gradle.psi.kotlin.createBuildGradleDependencyElement
import ru.hh.plugins.geminio.gradle.psi.kotlin.createGradlePluginElement
import ru.hh.plugins.geminio.gradle.psi.kotlin.getOrCreateBuildGradleDependenciesBlock
import ru.hh.plugins.geminio.gradle.psi.kotlin.getOrCreateGradlePluginsBlock
import ru.hh.plugins.geminio.gradle.psi.reformatWithCodeStyle

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
