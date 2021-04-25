package ru.hh.plugins.code_modification

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.code_modification.extensions.psi.createBuildGradleDependencyElement
import ru.hh.plugins.code_modification.extensions.psi.createNewLine
import ru.hh.plugins.code_modification.models.BuildGradleDependency
import ru.hh.plugins.extensions.openapi.findPsiFileByName
import ru.hh.plugins.extensions.psi.reformatWithCodeStyle


// TODO add modification for Kotlin build.gradle.kts

/**
 * Service for adding dependencies into build.gradle files.
 */
class BuildGradleModificationService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "BuildGradleModificationCommand"

        private const val BUILD_GRADLE_FILENAME = "build.gradle"
        private const val BUILD_GRADLE_KTS_FILENAME = "build.gradle.kts"
        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"

        fun getInstance(project: Project) = BuildGradleModificationService(project)
    }


    fun addDepsIntoModule(
        module: Module,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILENAME)
                ?: module.findPsiFileByName(BUILD_GRADLE_KTS_FILENAME)
                ?: throw IllegalStateException("""
                Can't find "$BUILD_GRADLE_FILENAME" / "$BUILD_GRADLE_KTS_FILENAME" in "${module.name}    
                """
                )

            buildGradlePsiFile.addGradleDependencies(gradleDependencies)
        }
    }

    fun addDepsIntoFile(
        psiFile: PsiFile,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            psiFile.addGradleDependencies(gradleDependencies)
        }
    }

    fun addDepsInModuleDirectory(
        rootDir: PsiDirectory?,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            val buildGradleFile = rootDir?.findFile(BUILD_GRADLE_FILENAME)
                ?: rootDir?.findFile(BUILD_GRADLE_KTS_FILENAME)
                ?: return@wrapInCommand

            buildGradleFile.addGradleDependencies(gradleDependencies)
        }
    }


    private fun PsiFile.addGradleDependencies(gradleDependencies: List<BuildGradleDependency>) {
        when (this) {
            is KtFile -> {
                this.addGradleDependencies(gradleDependencies)
            }

            is GroovyFileImpl -> {
                this.addGradleDependencies(gradleDependencies)
            }

            else -> {
                throw IllegalArgumentException("""
                Unknown $BUILD_GRADLE_FILENAME / $BUILD_GRADLE_KTS_FILENAME file type!    
                """
                )
            }
        }
    }

    private fun KtFile.addGradleDependencies(gradleDependencies: List<BuildGradleDependency>) {
        val dependenciesBodyBlock = getOrCreateBuildGradleDependenciesBlock()

        val existingDependencies = dependenciesBodyBlock
            .children
            .filterIsInstance<KtCallExpression>()
            .map { it.text.removePrefix("${it.calleeExpression?.text}(").removeSuffix(")") }
            .toSet()

        val ktPsiFactory = KtPsiFactory(project)
        gradleDependencies.forEach { dependency ->
            if (existingDependencies.contains(dependency.value).not()) {
                val element = ktPsiFactory.createBuildGradleDependencyElement(dependency)
                dependenciesBodyBlock.addBefore(element, dependenciesBodyBlock.rBrace)
            }
        }

        reformatWithCodeStyle()
    }

    private fun GroovyFileImpl.addGradleDependencies(gradleDependencies: List<BuildGradleDependency>) {
        val dependenciesClosableBlock = getOrCreateGradleDependenciesBlock()

        val existingDependencies = dependenciesClosableBlock.children.filterIsInstance<GrApplicationStatement>()
            .mapTo(mutableSetOf()) { dependency ->
                dependency.argumentList.text
                    .removePrefix("project(")
                    .removeSuffix(")")
                    .removeSurrounding("'")
                    .removeSurrounding("\"")
            }

        val factory = GroovyPsiElementFactory.getInstance(project)
        gradleDependencies.forEach { dependency ->
            if (existingDependencies.contains(dependency.value).not()) {
                val element = factory.createBuildGradleDependencyElement(dependency)
                dependenciesClosableBlock.addBefore(element, dependenciesClosableBlock.rBrace)
            }
        }

        reformatWithCodeStyle()
    }

    private fun GroovyFileImpl.getOrCreateGradleDependenciesBlock(): GrClosableBlock {
        val existingDependenciesBlock = findChildrenByClass(GrMethodCall::class.java)
            .firstOrNull { it.text.startsWith(DEPENDENCIES_BLOCK_NAME) }
            ?.findDescendantOfType<GrClosableBlock>()

        if (existingDependenciesBlock != null) {
            return existingDependenciesBlock
        }

        val factory = GroovyPsiElementFactory.getInstance(project)

        val newDependenciesExpression = factory.createExpressionFromText("""
        dependencies {
        }    
        """)

        this.add(factory.createNewLine())
        val addedDescriptionBlock = this.add(newDependenciesExpression)

        return requireNotNull(addedDescriptionBlock.findDescendantOfType()) {
            "Error with creating new $DEPENDENCIES_BLOCK_NAME block | Groovy"
        }
    }


    private inline fun wrapInCommand(isInWriteCommand: Boolean, crossinline action: () -> Unit) {
        if (isInWriteCommand) {
            action.invoke()
        } else {
            project.executeWriteCommand(COMMAND_NAME) {
                action.invoke()
            }
        }
    }

    private fun KtFile.getOrCreateBuildGradleDependenciesBlock(): KtBlockExpression {
        require(isScript()) {
            """
        You can create "$DEPENDENCIES_BLOCK_NAME" block only inside kts scripts.
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
            "Error with creating new $DEPENDENCIES_BLOCK_NAME block | kotlin"
        }
    }

    private fun KtFile.getGradleDependenciesBlock(): KtBlockExpression? {
        return script
            ?.declarations
            ?.firstOrNull { it.text.startsWith(DEPENDENCIES_BLOCK_NAME) }
            ?.findDescendantOfType()
    }

}