@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommandsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.rootSectionErrorMessage


private const val KEY_RECIPE_SECTION = "recipe"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommandsSection].
 */
internal fun Map<String, Any>.toRecipeCommandsSection(): RecipeCommandsSection {
    val commandsList = requireNotNull(this[KEY_RECIPE_SECTION] as? List<Map<String, Map<String, Any>>>) {
        rootSectionErrorMessage(KEY_RECIPE_SECTION)
    }

    return RecipeCommandsSection(
        commands = commandsList.map { it.toRecipeCommand(KEY_RECIPE_SECTION) }
    )
}