package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.FormFactor
import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateFormFactor


/**
 * Mapping from [ru.hh.plugins.geminio.model.GeminioTemplateFormFactor] into
 * Android Studio's [com.android.tools.idea.wizard.template.FormFactor].
 */
fun GeminioTemplateFormFactor.toAndroidStudioTemplateFormFactor(): FormFactor {
    return when (this) {
        GeminioTemplateFormFactor.MOBILE -> FormFactor.Mobile
        GeminioTemplateFormFactor.WEAR -> FormFactor.Wear
        GeminioTemplateFormFactor.TV -> FormFactor.Tv
        GeminioTemplateFormFactor.AUTOMOTIVE -> FormFactor.Automotive
        GeminioTemplateFormFactor.THINGS -> FormFactor.Things
        GeminioTemplateFormFactor.GENERIC -> FormFactor.Generic
    }
}