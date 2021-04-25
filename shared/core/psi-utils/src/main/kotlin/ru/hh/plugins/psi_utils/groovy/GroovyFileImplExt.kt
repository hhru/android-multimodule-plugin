package ru.hh.plugins.psi_utils.groovy

import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.PluginsConstants.BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME


/**
 * Invoke in write command only.
 */
fun GroovyFileImpl.getOrCreateGradleDependenciesBlock(): GrClosableBlock {
    val existingDependenciesBlock = findChildrenByClass(GrMethodCall::class.java)
        .firstOrNull { it.text.startsWith(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME) }
        ?.findDescendantOfType<GrClosableBlock>()

    if (existingDependenciesBlock != null) {
        return existingDependenciesBlock
    }

    val factory = GroovyPsiElementFactory.getInstance(project)

    val newDependenciesExpression = factory.createExpressionFromText("""
        dependencies {
        }    
        """
    )

    this.add(factory.createNewLine())
    val addedDescriptionBlock = this.add(newDependenciesExpression)

    return requireNotNull(addedDescriptionBlock.findDescendantOfType()) {
        "Error with creating new $BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME block | Groovy"
    }
}