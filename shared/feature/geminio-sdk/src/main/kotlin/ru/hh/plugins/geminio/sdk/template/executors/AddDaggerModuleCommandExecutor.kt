package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
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
    HHLogger.d("AddDaggerModule command [$command], isDryRun: ${executorData.isDryRun}")
    if (executorData.isDryRun) {
        HHLogger.d("\tExecute only when isDryRun == true")

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

        featureModuleName.let {
            val appComponentPath =
                File(daggerAppModulePath).toPsiDirectory(executorData.project) ?: return@let
            val appComponentPsi = appComponentPath.findFile(appComponentName)
            HHLogger.e(appComponentPsi?.children.toString())
            HHLogger.e(appComponentPsi?.children?.map { it.text }.toString())
            appComponentPsi?.children?.find { it.text == "CLASS" }
        }
    }
}