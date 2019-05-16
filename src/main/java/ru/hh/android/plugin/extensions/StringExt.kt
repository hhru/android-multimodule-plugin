package ru.hh.android.plugin.extensions


val String.Companion.EMPTY: String get() = ""

val String.Companion.LINE_BREAK: String get() = "\n"


fun String.replaceLineBreaks(): String {
    return this.replace("\n", "\n<br />", true)
}

fun String.replaceWordsBreakers(): String {
    return this.replace('-', '_')
            .replace('_', ' ')
            .replace("  ", " ")
}

fun String.replaceMultipleSplashes(): String {
    return this.replace("//", "/")
}