package ru.hh.plugins.geminio.sdk.template.mapping.optional

import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateScreen
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateScreen

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateScreen]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateScreen].
 */
internal fun TemplateScreen.toAndroidStudioTemplateScreen(): AndroidStudioTemplateScreen {
    return when (this) {
        TemplateScreen.NEW_PROJECT -> AndroidStudioTemplateScreen.NewProject
        TemplateScreen.NEW_MODULE -> AndroidStudioTemplateScreen.NewModule
        TemplateScreen.MENU_ENTRY -> AndroidStudioTemplateScreen.MenuEntry
        TemplateScreen.ACTIVITY_GALLERY -> AndroidStudioTemplateScreen.ActivityGallery
        TemplateScreen.FRAGMENT_GALLERY -> AndroidStudioTemplateScreen.FragmentGallery
    }
}
