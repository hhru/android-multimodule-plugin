package ru.hh.plugins.geminio.sdk.template.mapping.optional

import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateFormFactor
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateFormFactor


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateFormFactor]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateFormFactor].
 */
internal fun TemplateFormFactor.toAndroidStudioTemplateFormFactor(): AndroidStudioTemplateFormFactor {
    return when (this) {
        TemplateFormFactor.MOBILE -> AndroidStudioTemplateFormFactor.Mobile
        TemplateFormFactor.WEAR -> AndroidStudioTemplateFormFactor.Wear
        TemplateFormFactor.TV -> AndroidStudioTemplateFormFactor.Tv
        TemplateFormFactor.AUTOMOTIVE -> AndroidStudioTemplateFormFactor.Automotive
        TemplateFormFactor.THINGS -> AndroidStudioTemplateFormFactor.Things
        TemplateFormFactor.GENERIC -> AndroidStudioTemplateFormFactor.Generic
    }
}