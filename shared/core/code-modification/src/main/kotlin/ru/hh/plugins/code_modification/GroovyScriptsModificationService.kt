package ru.hh.plugins.code_modification

import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.psi_utils.groovy.createBuildGradleDependencyElement
import ru.hh.plugins.psi_utils.groovy.createGradlePluginElement
import ru.hh.plugins.psi_utils.groovy.createNewLine
import ru.hh.plugins.psi_utils.groovy.getOrCreateGradleDependenciesBlock
import ru.hh.plugins.psi_utils.groovy.getOrCreateGradlePluginsBlock
import ru.hh.plugins.psi_utils.reformatWithCodeStyle

class GroovyScriptsModificationService {

    fun addGradleDependencies(groovyFileImpl: GroovyFileImpl, gradleDependencies: List<BuildGradleDependency>) {
        val dependenciesClosableBlock = groovyFileImpl.getOrCreateGradleDependenciesBlock()

        val existingDependencies = dependenciesClosableBlock.children.filterIsInstance<GrApplicationStatement>()
            .mapTo(mutableSetOf()) { dependency ->
                dependency.argumentList.text
                    .removePrefix("project(")
                    .removeSuffix(")")
                    .removeSurrounding("'")
                    .removeSurrounding("\"")
            }

        val factory = GroovyPsiElementFactory.getInstance(groovyFileImpl.project)
        gradleDependencies.forEach { dependency ->
            if (existingDependencies.contains(dependency.value).not()) {
                val element = factory.createBuildGradleDependencyElement(dependency)
                dependenciesClosableBlock.addBefore(factory.createNewLine(), dependenciesClosableBlock.rBrace)
                dependenciesClosableBlock.addBefore(element, dependenciesClosableBlock.rBrace)
            }
        }

        groovyFileImpl.reformatWithCodeStyle()
    }

    fun addGradlePlugins(groovyFileImpl: GroovyFileImpl, pluginsIds: List<String>) {
        val gradlePluginsBlock = groovyFileImpl.getOrCreateGradlePluginsBlock()

        val existingPlugins = gradlePluginsBlock.children.filterIsInstance<GrApplicationStatement>()
            .mapTo(mutableSetOf()) { plugin ->
                plugin.argumentList.text
                    .removePrefix("id(")
                    .removeSuffix(")")
                    .removeSurrounding("'")
                    .removeSurrounding("\"")
            }

        val factory = GroovyPsiElementFactory.getInstance(groovyFileImpl.project)
        pluginsIds.forEach { pluginId ->
            if (existingPlugins.contains(pluginId).not()) {
                val element = factory.createGradlePluginElement(pluginId)
                gradlePluginsBlock.addBefore(factory.createNewLine(), gradlePluginsBlock.rBrace)
                gradlePluginsBlock.addBefore(element, gradlePluginsBlock.rBrace)
            }
        }

        groovyFileImpl.reformatWithCodeStyle()
    }

}
