package ru.hh.plugins.geminio.sdk.recipe.models.extensions

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe

internal fun GeminioRecipe.toIndentString(): String {
    val notFancy = toString()
    return buildString(notFancy.length) {
        var indent = 0
        fun StringBuilder.line() {
            appendLine()
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
