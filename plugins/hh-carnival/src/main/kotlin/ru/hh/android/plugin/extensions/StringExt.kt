package ru.hh.android.plugin.extensions

import ru.hh.android.plugin.PluginConstants
import ru.hh.plugins.extensions.HYPHEN
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE


private val REGEX_PACKAGE_NAME = Regex("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)*[A-Za-z][A-Za-z\\d_]*\$")


fun String.replaceLineBreaks(): String {
    return this.replace("\n", "\n<br />", true)
}


fun String.toPackageNameFromModuleName(): String {
    val formattedModuleName = this
        .replace(Char.SPACE, Char.UNDERSCORE)
        .replace(Char.HYPHEN, Char.UNDERSCORE)
    return "${PluginConstants.DEFAULT_PACKAGE_NAME_PREFIX}.$formattedModuleName"
}

fun String.isCorrectPackageName(): Boolean {
    return REGEX_PACKAGE_NAME.matches(this)
}