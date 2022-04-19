package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File

internal fun RecipeExecutor.execute(
    command: RecipeCommand.AddNavigation,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    if (executorData.isDryRun.not()) {
        val featureName = resolvedParamsMap["featureName"] as? String
        val packageName = resolvedParamsMap["packageName"] as? String
        val featurePackageName = resolvedParamsMap["featurePackageName"] as? String

        val filePath = "${executorData.project.basePath}/${command.fileName}"
        val fileText = readFileAsText(filePath)

        val stringBuilder = StringBuilder(fileText)

        val navigationText = navigationText(featureName ?: "", packageName ?: "", featurePackageName ?: "")
        val insertPosition = stringBuilder.indexOf("</navigation>")

        stringBuilder.insert(insertPosition, navigationText)

        File(filePath).writeText(stringBuilder.toString())

        open(File(filePath))
    }
}

private fun readFileAsText(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

private fun navigationText(featureName: String, basePackagePath: String, featurePackageName: String) = """
    <fragment
        android:id="@+id/${featureName.replaceFirstChar { it.lowercase() }}"
        android:name="${basePackagePath}.${featurePackageName}.presentation.view.${featureName}ComposeFragment"
        android:label="$featureName">

        <argument
            android:name="${featureName}Arguments"
            app:argType="${basePackagePath}.${featurePackageName}.presentation.store.${featureName}Arguments" />
    </fragment>
"""