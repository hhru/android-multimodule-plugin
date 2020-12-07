package ru.hh.plugins.geminio.sdk.model.recipe


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

    /**
     * Command which will be executed only if [validIf] predicate returns true.
     */
    data class Predicate(
        val validIf: RecipeExpression,
        val commands: List<RecipeCommand>
    ) : RecipeCommand()


    /**
     * Command which will add dependencies into build.gradle
     */
    data class AddDependencies(
        val dependencies: List<BuildGradleDependency>
    ) : RecipeCommand()
}