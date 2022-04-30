package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.KtScriptsModificationService
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File

internal fun RecipeExecutor.execute(
    command: RecipeCommand.AddKoinModule,
    executorData: GeminioRecipeExecutorData
) {
    with(executorData) {
        println("AddKoinModule command [$command], isDryRun: ${executorData.isDryRun}")
        if (executorData.isDryRun) {
            val featureName = resolvedParamsMap["featureName"] as? String
            val packageName = resolvedParamsMap["packageName"] as? String
            val featurePackageName = resolvedParamsMap["featurePackageName"] as? String

            val koinModuleName = "${featureName}Module"
            val koinModulePath = "${packageName}.${featurePackageName}.${koinModuleName}".replace(" ", "")

            val fileName = command.fileNameExpression.evaluateString(moduleTemplateData, existingParametersMap)
            val filePath = "${executorData.project.basePath}/$fileName"
            val file = File(filePath).toPsiFile(executorData.project)
            file?.let {
                KtScriptsModificationService().addKoinModule(it as KtFile, koinModuleName, koinModulePath)
            }
        }
    }
}
