package ru.hh.plugins.geminio.sdk.form

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBooleanParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter

/**
 * Propagates the current pure form state back into legacy Android Studio template parameters.
 *
 * This keeps the old execution runtime working while Geminio still relies on Android Studio's
 * template/executor infrastructure under the hood.
 */
internal fun GeminioTemplateData.applyFormValues(
    form: GeminioForm,
    session: GeminioFormSession,
) {
    form.fields.forEach { field ->
        when (field) {
            is GeminioFormField.StringField -> {
                val parameter =
                    existingParametersMap[field.id] as? AndroidStudioTemplateStringParameter ?: return@forEach
                parameter.value = session.stringValue(field.id) ?: String.EMPTY
            }

            is GeminioFormField.BooleanField -> {
                val parameter =
                    existingParametersMap[field.id] as? AndroidStudioTemplateBooleanParameter ?: return@forEach
                parameter.value = session.booleanValue(field.id)
            }
        }
    }
}
