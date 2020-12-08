package ru.hh.plugins.geminio.sdk.template.mapping

import com.android.tools.idea.wizard.template.TemplateConstraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateConstraint


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateConstraint] into
 * Android Studio's [com.android.tools.idea.wizard.template.TemplateConstraint].
 */
fun GeminioTemplateConstraint.toAndroidStudioTemplateConstraint(): TemplateConstraint {
    return when (this) {
        GeminioTemplateConstraint.ANDROIDX -> TemplateConstraint.AndroidX
        GeminioTemplateConstraint.KOTLIN -> TemplateConstraint.Kotlin
    }
}