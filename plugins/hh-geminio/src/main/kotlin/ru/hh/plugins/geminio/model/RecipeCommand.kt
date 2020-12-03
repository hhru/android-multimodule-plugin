package ru.hh.plugins.geminio.model

import ru.hh.plugins.model.BuildGradleDependency


/**
 * Recipe's commands for execution.
 */
sealed class RecipeCommand {

    /**
     * Instantiate file [from] path into [to] destination.
     */
    data class Instantiate(
        val from: GeminioRecipe.RecipeExpression,
        val to: GeminioRecipe.RecipeExpression
    ) : RecipeCommand()

    /**
     * Try to open file from [file] path.
     */
    data class Open(
        val file: GeminioRecipe.RecipeExpression
    ) : RecipeCommand()

    /**
     * Combination of [Instantiate] and [Open] command.
     *
     * Instantiate file [from] path into [to] destination + try to open created file.
     */
    data class InstantiateAndOpen(
        val from: GeminioRecipe.RecipeExpression,
        val to: GeminioRecipe.RecipeExpression
    ) : RecipeCommand()

    /**
     * Command which will be executed only if [validIf] predicate returns true.
     */
    data class Predicate(
        val validIf: GeminioRecipe.RecipeExpression,
        val commands: List<RecipeCommand>
    ) : RecipeCommand()


    /**
     * Command which will add dependencies into build.gradle
     */
    data class AddDependencies(
        val dependencies: List<BuildGradleDependency>
    ) : RecipeCommand()
}