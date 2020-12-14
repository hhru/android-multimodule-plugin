package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.findDescendantOfType
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.extensions.psi.reformatWithCodeStyle
import ru.hh.plugins.geminio.sdk.extensions.psi.groovy.createBuildGradleDependencyElement
import ru.hh.plugins.geminio.sdk.recipe.models.commands.BuildGradleDependency
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData


internal fun RecipeExecutor.execute(
    command: RecipeCommand.AddDependencies,
    executorData: GeminioRecipeExecutorData
) {
    println("AddDependencies command [$command], isDryRun: ${executorData.isDryRun}")
    if (executorData.isDryRun) {
        println("\tExecute only when isDryRun == true")

        val rootDir = executorData.moduleTemplateData.rootDir.toPsiDirectory(executorData.project)

        modifyGroovyBuildGradleIfAcceptable(rootDir, command.dependencies)
        // TODO add modification for Kotlin build.gradle.kts
    }
}


private fun modifyGroovyBuildGradleIfAcceptable(rootDir: PsiDirectory?, dependencies: List<BuildGradleDependency>) {
    val buildGradleFile = rootDir?.findFile("build.gradle") as? GroovyFileImpl
        ?: return

    val dependenciesClosableBlock = buildGradleFile.findChildrenByClass(GrMethodCall::class.java)
        .firstOrNull { it.text.startsWith("dependencies") }
        ?.findDescendantOfType<GrClosableBlock>()
        ?: return

    val existingDependencies = dependenciesClosableBlock.children.filterIsInstance<GrApplicationStatement>()
        .mapTo(mutableSetOf()) { dependency ->
            dependency.argumentList.text
                .removePrefix("project(")
                .removeSuffix(")")
                .removeSurrounding("'")
                .removeSurrounding("\"")
        }

    val factory = GroovyPsiElementFactory.getInstance(rootDir.project)
    dependencies.forEach { dependency ->
        if (existingDependencies.contains(dependency.value).not()) {
            val element = factory.createBuildGradleDependencyElement(dependency)
            dependenciesClosableBlock.addBefore(element, dependenciesClosableBlock.rBrace)
        }
    }

    buildGradleFile.reformatWithCodeStyle()
}