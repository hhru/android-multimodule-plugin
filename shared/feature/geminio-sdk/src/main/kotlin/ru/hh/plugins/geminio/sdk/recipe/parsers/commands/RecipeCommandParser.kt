@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage

private const val KEY_COMMAND_INSTANTIATE = "instantiate"
private const val KEY_COMMAND_OPEN = "open"
private const val KEY_COMMAND_INSTANTIATE_AND_OPEN = "instantiateAndOpen"
private const val KEY_COMMAND_PREDICATE = "predicate"
private const val KEY_COMMAND_ADD_DEPENDENCIES = "addDependencies"
private const val KEY_MK_DIRS = "mkDirs"
private const val KEY_COMMAND_ADD_GRADLE_PLUGINS = "addGradlePlugins"
private const val KEY_COMMAND_ADD_DAGGER_MODULE = "addDaggerModule"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand]
 */
internal fun Map<String, Any>.toRecipeCommand(sectionName: String): RecipeCommand {
    val instantiateCommandMap = this[KEY_COMMAND_INSTANTIATE] as? Map<String, Any>
    val openCommandMap = this[KEY_COMMAND_OPEN] as? Map<String, Any>
    val instantiateAndOpenCommandMap = this[KEY_COMMAND_INSTANTIATE_AND_OPEN] as? Map<String, Any>
    val predicateCommandMap = this[KEY_COMMAND_PREDICATE] as? Map<String, Any>
    val addDependenciesCommandList = this[KEY_COMMAND_ADD_DEPENDENCIES] as? List<Map<String, Any>>
    val mkDirsCommandList = this[KEY_MK_DIRS] as? List<Any>
    val addGradlePluginsCommandList = this[KEY_COMMAND_ADD_GRADLE_PLUGINS] as? List<String>
    val addDaggerModule = this[KEY_COMMAND_ADD_DAGGER_MODULE] as? Map<String, Any>

    return when {
        instantiateCommandMap != null -> {
            instantiateCommandMap.toInstantiateCommand("$sectionName:$KEY_COMMAND_INSTANTIATE")
        }

        openCommandMap != null -> {
            openCommandMap.toOpenCommand("$sectionName:$KEY_COMMAND_INSTANTIATE")
        }

        instantiateAndOpenCommandMap != null -> {
            instantiateAndOpenCommandMap.toInstantiateAndOpenCommand("$sectionName:$KEY_COMMAND_INSTANTIATE")
        }

        predicateCommandMap != null -> {
            predicateCommandMap.toPredicateCommand("$sectionName:$KEY_COMMAND_INSTANTIATE")
        }

        addDependenciesCommandList != null -> {
            addDependenciesCommandList.toAddDependenciesCommand("$sectionName:$KEY_COMMAND_INSTANTIATE")
        }

        mkDirsCommandList != null -> {
            mkDirsCommandList.toMkDirsCommand("$sectionName:$KEY_MK_DIRS")
        }

        addGradlePluginsCommandList != null -> {
            addGradlePluginsCommandList.toAddGradlePluginsCommand("$sectionName:$KEY_COMMAND_ADD_GRADLE_PLUGINS")
        }
        addDaggerModule != null -> {
            addDaggerModule.toAddDaggerCommand("$sectionName:$KEY_COMMAND_ADD_DAGGER_MODULE")
        }
        else -> {
            throw IllegalArgumentException(
                sectionUnknownEnumKeyErrorMessage(
                    sectionName = sectionName,
                    key = this.keys.toString(),
                    acceptableValues = listOf(
                        KEY_COMMAND_INSTANTIATE,
                        KEY_COMMAND_OPEN,
                        KEY_COMMAND_INSTANTIATE_AND_OPEN,
                        KEY_COMMAND_PREDICATE,
                        KEY_COMMAND_ADD_DEPENDENCIES,
                        KEY_MK_DIRS,
                        KEY_COMMAND_ADD_GRADLE_PLUGINS,
                        KEY_COMMAND_ADD_DAGGER_MODULE
                    ).joinToString { "'$it'" }
                )
            )
        }
    }
}
