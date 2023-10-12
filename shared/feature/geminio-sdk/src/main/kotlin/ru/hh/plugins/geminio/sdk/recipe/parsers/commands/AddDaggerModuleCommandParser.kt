package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression

private const val KEY_COMMAND_DAGGER_APP_MODULE_PATH = "daggerAppModulePath"
private const val KEY_COMMAND_APP_COMPONENT_NAME = "appComponentName"
private const val KEY_COMMAND_FEATURE_MODULE_NAME = "featureModuleName"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.Instantiate] command.
 */
internal fun Map<String, Any>.toAddDaggerCommand(sectionName: String): RecipeCommand.AddDaggerModule {
    val daggerAppModulePath = requireNotNull(this[KEY_COMMAND_DAGGER_APP_MODULE_PATH] as? String) {
        ParsersErrorsFactory.sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_DAGGER_APP_MODULE_PATH
        )
    }
    val appComponentName = requireNotNull(this[KEY_COMMAND_APP_COMPONENT_NAME] as? String) {
        ParsersErrorsFactory.sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_APP_COMPONENT_NAME
        )
    }
    val featureModuleName = requireNotNull(this[KEY_COMMAND_FEATURE_MODULE_NAME] as? String) {
        ParsersErrorsFactory.sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_FEATURE_MODULE_NAME
        )
    }

    return RecipeCommand.AddDaggerModule(
        daggerAppModulePath = daggerAppModulePath.toRecipeExpression(sectionName),
        appComponentName = appComponentName.toRecipeExpression(sectionName),
        featureModuleName = featureModuleName.toRecipeExpression(sectionName),
    )
}