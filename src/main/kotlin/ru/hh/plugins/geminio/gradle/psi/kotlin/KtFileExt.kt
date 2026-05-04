package ru.hh.plugins.geminio.gradle.psi.kotlin

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import ru.hh.plugins.geminio.common.PluginsConstants.BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME
import ru.hh.plugins.geminio.common.PluginsConstants.BUILD_GRADLE_PLUGINS_BLOCK_NAME

/**
 * Invoke in write command only.
 */
fun KtFile.getOrCreateBuildGradleDependenciesBlock(): KtBlockExpression {
    require(isScript()) {
        """
        You can create "$BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME" block only inside kts scripts.
            file name: ${this.name}
            file path: ${this.virtualFilePath}
        """
    }

    return findBlockExpressionByName(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME)
        ?: createScriptBlock(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME)
}

/**
 * Invoke in write command only.
 */
fun KtFile.getOrCreateGradlePluginsBlock(): KtBlockExpression {
    require(isScript()) {
        """
        You can create "$BUILD_GRADLE_PLUGINS_BLOCK_NAME" block only inside kts scripts.
            file name: ${this.name}
            file path: ${this.virtualFilePath}
        """.trimIndent()
    }

    return findBlockExpressionByName(BUILD_GRADLE_PLUGINS_BLOCK_NAME)
        ?: createScriptBlock(BUILD_GRADLE_PLUGINS_BLOCK_NAME)
}

private fun KtFile.findBlockExpressionByName(blockName: String): KtBlockExpression? {
    return script
        ?.declarations
        ?.firstOrNull { it.text.startsWith(blockName) }
        ?.findDescendantOfType()
}

private fun KtFile.createScriptBlock(blockName: String): KtBlockExpression {
    val ktPsiFactory = KtPsiFactory(project)
    val newPluginsBlock = ktPsiFactory.createExpression(
        """
        $blockName {
        }
        """
    )

    this.add(ktPsiFactory.createNewLine())
    val addedPluginsBlock = this.add(newPluginsBlock)

    return requireNotNull(addedPluginsBlock.findDescendantOfType()) {
        "Error with creating new $blockName block | kotlin"
    }
}
