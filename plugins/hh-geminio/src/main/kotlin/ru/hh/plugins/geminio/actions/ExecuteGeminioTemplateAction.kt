package ru.hh.plugins.geminio.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.plugins.geminio.model.yaml.GeminioRecipeReader
import java.io.File


/**
 * Base action for executing templates from YAML config.
 *
 * This action not registered in plugin.xml, because we create it in runtime.
 */
@Suppress("ComponentNotRegistered")
class ExecuteGeminioTemplateAction(
    private val actionText: String,
    private val actionDescription: String,
    private val templateDirPath: String,
    private val geminioRecipePath: String
) : AnAction() {

    companion object {
        const val BASE_ID = "ru.hh.plugins.geminio.actions."
    }

    init {
        with(templatePresentation) {
            text = actionText
            description = actionDescription
            isEnabledAndVisible = true
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        println("Start executing template [$actionText]")

        println("Search for recipe file: $geminioRecipePath")
        val recipeFile = File(geminioRecipePath)
        if (recipeFile.exists().not()) {
            println("Recipe file doesn't exists [look into $geminioRecipePath]")
            return
        }

        println("Recipe file exists -> need to parse, execute, etc")

        val geminioRecipe = GeminioRecipeReader().parse(geminioRecipePath)

        println("geminio recipe to String:\n ${geminioRecipe}")
        println("==========")
        println("geminio recipe:\n ${geminioRecipe.toIndentString()}")
    }



}

fun Any?.toIndentString(): String {
    val notFancy = toString()
    return buildString(notFancy.length) {
        var indent = 0
        fun StringBuilder.line() {
            appendln()
            repeat(2 * indent) { append(' ') }
        }

        for (char in notFancy) {
            if (char == ' ') continue

            when (char) {
                ')', ']' -> {
                    indent--
                    line()
                }
            }

            if (char == '=') append(' ')
            append(char)
            if (char == '=') append(' ')

            when (char) {
                '(', '[', ',' -> {
                    if (char != ',') indent++
                    line()
                }
            }
        }
    }
}