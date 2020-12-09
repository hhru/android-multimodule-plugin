package ru.hh.plugins.geminio.sdk.recipe.parsers

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.parsers.commands.toRecipeCommandsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.globals.toGlobalsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.optional.toOptionalParams
import ru.hh.plugins.geminio.sdk.recipe.parsers.required.toRequiredParams
import ru.hh.plugins.geminio.sdk.recipe.parsers.widgets.toWidgetsSection
import ru.hh.plugins.utils.yaml.YamlUtils
import java.io.File


/**
 * Parser from YAML file to [ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe].
 */
internal fun String.parseGeminioRecipeFromYamlFile(): GeminioRecipe {
    val configMap = YamlUtils.loadFromConfigFile(this) { throwable ->
        throwable.printStackTrace()
    } ?: throw IllegalArgumentException(
        "Unknown error with parsing $this -- cannot get map of objects"
    )

    return GeminioRecipe(
        freemarkerTemplatesRootDirPath = File(this).parent,
        requiredParams = configMap.toRequiredParams(),
        optionalParams = configMap.toOptionalParams(),
        globalsSection = configMap.toGlobalsSection(),
        widgetsSection = configMap.toWidgetsSection(),
        recipeCommands = configMap.toRecipeCommandsSection()
    )
}