package ru.hh.plugins.psi_utils.kotlin

import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.formatter.commitAndUnblockDocument
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import ru.hh.plugins.PluginsConstants.BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME
import ru.hh.plugins.psi_utils.reformatWithCodeStyle


fun KtFile.shortReferencesAndReformatWithCodeStyle() {
    this.commitAndUnblockDocument()
    ShortenReferences.DEFAULT.process(this)
    this.reformatWithCodeStyle()
}

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

    val existingDependenciesBlock = getGradleDependenciesBlock()
    if (existingDependenciesBlock != null) {
        return existingDependenciesBlock
    }

    val ktPsiFactory = KtPsiFactory(project)
    val newDependenciesExpression = ktPsiFactory.createExpression("""
        dependencies {
        }
        """
    )

    this.add(ktPsiFactory.createNewLine())
    val addedDescriptionBlock = this.add(newDependenciesExpression)

    return requireNotNull(addedDescriptionBlock.findDescendantOfType()) {
        "Error with creating new $BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME block | kotlin"
    }
}

private fun KtFile.getGradleDependenciesBlock(): KtBlockExpression? {
    return script
        ?.declarations
        ?.firstOrNull { it.text.startsWith(BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME) }
        ?.findDescendantOfType()
}