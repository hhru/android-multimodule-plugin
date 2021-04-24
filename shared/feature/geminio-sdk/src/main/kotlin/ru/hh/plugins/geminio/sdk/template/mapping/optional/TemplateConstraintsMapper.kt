package ru.hh.plugins.geminio.sdk.template.mapping.optional

import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateConstraint
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateConstraint


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateConstraint]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateConstraint].
 */
internal fun TemplateConstraint.toAndroidStudioTemplateConstraint(): AndroidStudioTemplateConstraint {
    return when (this) {
        TemplateConstraint.ANDROIDX -> AndroidStudioTemplateConstraint.AndroidX
        TemplateConstraint.KOTLIN -> AndroidStudioTemplateConstraint.Kotlin
    }
}