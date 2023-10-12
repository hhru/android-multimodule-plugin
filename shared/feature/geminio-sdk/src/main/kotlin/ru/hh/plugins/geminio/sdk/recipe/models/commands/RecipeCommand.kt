package ru.hh.plugins.geminio.sdk.recipe.models.commands

import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.models.gradle.BuildGradleDependency

/**
 * Recipe's commands for execution.
 */
sealed class RecipeCommand {

    /**
     * Instantiate file [from] path into [to] destination.
     */
    data class Instantiate(
        val from: RecipeExpression,
        val to: RecipeExpression
    ) : RecipeCommand()

    /**
     * Try to open file from [file] path.
     */
    data class Open(
        val file: RecipeExpression
    ) : RecipeCommand()

    /**
     * Combination of [Instantiate] and [Open] command.
     *
     * Instantiate file [from] path into [to] destination + try to open created file.
     */
    data class InstantiateAndOpen(
        val from: RecipeExpression,
        val to: RecipeExpression
    ) : RecipeCommand()

    data class AddDaggerModule(
        val daggerAppModulePath: RecipeExpression,
        val appComponentName: RecipeExpression,
        val featureModuleName: RecipeExpression
    ) : RecipeCommand()

    /**
     * Command which will be executed only if [validIf] predicate returns true.
     * If [validIf] returns false, try to execute [elseCommands].
     */
    data class Predicate(
        val validIf: RecipeExpression,
        val commands: List<RecipeCommand>,
        val elseCommands: List<RecipeCommand>
    ) : RecipeCommand()

    /**
     * Command which will add dependencies into build.gradle
     */
    data class AddDependencies(
        val dependencies: List<BuildGradleDependency>
    ) : RecipeCommand()

    /**
     * Command for creating directories structure.
     */
    data class MkDirs(
        val dirs: List<MkDirItem>
    ) : RecipeCommand()

    /**
     * Command for adding gradle plugins into `plugins { }` block in build.gradle .
     */
    data class AddGradlePlugins(
        val pluginsIds: List<String>
    ) : RecipeCommand()
}
