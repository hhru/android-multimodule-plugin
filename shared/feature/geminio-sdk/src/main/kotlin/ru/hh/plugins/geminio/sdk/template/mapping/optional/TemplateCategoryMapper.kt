package ru.hh.plugins.geminio.sdk.template.mapping.optional

import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateCategory
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateCategory

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateCategory]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateCategory].
 */
internal fun TemplateCategory.toAndroidStudioTemplateCategory(): AndroidStudioTemplateCategory {
    return when (this) {
        TemplateCategory.ACTIVITY -> AndroidStudioTemplateCategory.Activity
        TemplateCategory.FRAGMENT -> AndroidStudioTemplateCategory.Fragment
        TemplateCategory.APPLICATION -> AndroidStudioTemplateCategory.Application
        TemplateCategory.FOLDER -> AndroidStudioTemplateCategory.Folder
        TemplateCategory.UI_COMPONENT -> AndroidStudioTemplateCategory.UiComponent
        TemplateCategory.CAR -> AndroidStudioTemplateCategory.Car
        TemplateCategory.XML -> AndroidStudioTemplateCategory.XML
        TemplateCategory.WEAR -> AndroidStudioTemplateCategory.Wear
        TemplateCategory.AIDL -> AndroidStudioTemplateCategory.AIDL
        TemplateCategory.WIDGET -> AndroidStudioTemplateCategory.Widget
        TemplateCategory.GOOGLE -> AndroidStudioTemplateCategory.Google
        TemplateCategory.COMPOSE -> AndroidStudioTemplateCategory.Compose
        TemplateCategory.OTHER -> AndroidStudioTemplateCategory.Other
        TemplateCategory.TV -> AndroidStudioTemplateCategory.TV
        TemplateCategory.SERVICE -> AndroidStudioTemplateCategory.Service
        TemplateCategory.TEST -> AndroidStudioTemplateCategory.Test
    }
}
