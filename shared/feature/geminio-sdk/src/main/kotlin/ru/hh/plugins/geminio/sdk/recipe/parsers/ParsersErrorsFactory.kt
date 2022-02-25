package ru.hh.plugins.geminio.sdk.recipe.parsers

import ru.hh.plugins.extensions.EMPTY

/**
 * Simple factory for validation errors messages.
 */
internal object ParsersErrorsFactory {

    fun rootSectionErrorMessage(sectionName: String): String {
        return "Recipe parsing: not found '$sectionName' section in recipe!"
    }

    fun sectionErrorMessage(
        sectionName: String,
        message: String
    ): String {
        return "'$sectionName' section: $message"
    }

    fun sectionRequiredParameterErrorMessage(
        sectionName: String,
        key: String,
        additionalInfo: String = String.EMPTY
    ): String {
        val info = additionalInfo.takeIf { it.isNotBlank() }?.let { " [$it]" } ?: String.EMPTY
        return sectionErrorMessage(sectionName, "Not found required '$key' parameter $info.")
    }

    fun sectionUnknownEnumKeyErrorMessage(
        sectionName: String,
        key: String,
        acceptableValues: String,
        additionalInfo: String = String.EMPTY
    ): String {
        val info = additionalInfo.takeIf { it.isNotBlank() }?.let { " [$it]" } ?: String.EMPTY
        return sectionErrorMessage(
            sectionName = sectionName,
            message = "Unknown parsing key [key: $key, acceptable values: $acceptableValues] $info"
        )
    }
}
