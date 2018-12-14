package ru.hh.android.plugin.feature_module.extensions


val String.Companion.EMPTY: String get() = ""


fun String.replaceLineBreaks(): String {
    return this.replace("\n", "\n<br />", true)
}