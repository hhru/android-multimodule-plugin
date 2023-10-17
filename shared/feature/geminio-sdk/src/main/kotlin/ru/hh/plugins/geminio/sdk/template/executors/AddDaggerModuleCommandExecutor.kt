package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.kotlin.findValueArgument
import com.android.tools.idea.lint.common.isAnnotationTarget
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.logger.HHLogger
import java.io.File

internal fun RecipeExecutor.execute(
    targetDirectory: VirtualFile,
    command: RecipeCommand.AddDaggerModule,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    HHLogger.d("AddDaggerModule command [$command]")

    val daggerAppModulePath = command.daggerAppModulePath.evaluateString(
        targetDirectory,
        moduleTemplateData,
        existingParametersMap
    ) ?: return@with

    val appComponentName = command.appComponentName.evaluateString(
        targetDirectory,
        moduleTemplateData,
        existingParametersMap
    ) ?: return@with

    val featureModuleName = command.featureModuleName.evaluateString(
        targetDirectory,
        moduleTemplateData,
        existingParametersMap
    ) ?: return@with

    val path = "${executorData.project.basePath}/${daggerAppModulePath}"

    val appComponentPath = File(path).toPsiDirectory(executorData.project) ?: return

    val appComponentPsi = appComponentPath.findFile(appComponentName) as KtFile

    val annotationTargetPsi = appComponentPsi.children.find { it.isAnnotationTarget() } as KtClass
    val componentAnnotation =
        annotationTargetPsi.annotationEntries.find { it.text.contains("@Component") } ?: return@with

    val argument =
        componentAnnotation.findValueArgument("modules")?.getArgumentExpression() ?: return@with
    val modulePsiFactory = KtPsiFactory(argument.project)

    argument.addBefore(modulePsiFactory.createComma(), argument.lastChild)
    argument.addBefore(modulePsiFactory.createNewLine(), argument.lastChild)
    argument.addBefore(
        modulePsiFactory.createArgument("${featureModuleName}::class\n"),
        argument.lastChild
    )
    argument.addBefore(modulePsiFactory.createNewLine(), argument.lastChild)

    CodeStyleManager.getInstance(project).reformat(appComponentPsi)
}
