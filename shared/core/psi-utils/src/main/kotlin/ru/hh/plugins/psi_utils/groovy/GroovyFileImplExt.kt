package ru.hh.plugins.psi_utils.groovy

import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.PluginsConstants.BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME
import ru.hh.plugins.PluginsConstants.BUILD_GRADLE_PLUGINS_BLOCK_NAME

/**
 * Invoke in write command only.
 */
fun GroovyFileImpl.getOrCreateGradleDependenciesBlock(): GrClosableBlock {
    return findBlockExpressionByName(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME)
        ?: createScriptBlock(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME)
}

/**
 * Invoke in write command only.
 */
fun GroovyFileImpl.getOrCreateGradlePluginsBlock(): GrClosableBlock {
    return findBlockExpressionByName(BUILD_GRADLE_PLUGINS_BLOCK_NAME)
        ?: createScriptBlock(BUILD_GRADLE_PLUGINS_BLOCK_NAME)
}

private fun GroovyFileImpl.findBlockExpressionByName(blockName: String): GrClosableBlock? {
    return findChildrenByClass(GrMethodCall::class.java)
        .firstOrNull { it.text.startsWith(blockName) }
        ?.findDescendantOfType<GrClosableBlock>()
}

private fun GroovyFileImpl.createScriptBlock(blockName: String): GrClosableBlock {
    val factory = GroovyPsiElementFactory.getInstance(project)

    val newBlockExpression = factory.createExpressionFromText(
        """
        $blockName {
        }    
        """
    )

    this.add(factory.createNewLine())
    val addedBlock = this.add(newBlockExpression)

    return requireNotNull(addedBlock.findDescendantOfType()) {
        "Error with creating new $blockName block | Groovy"
    }
}
