package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.WizardUiContext
import ru.hh.plugins.geminio.model.enums.GeminioTemplateScreen


/**
 * Mapping from [ru.hh.plugins.geminio.model.GeminioTemplateScreen] into
 * Android Studio's [com.android.tools.idea.wizard.template.WizardUiContext].
 */
fun GeminioTemplateScreen.toAndroidStudioTemplateWizardUiContext(): WizardUiContext {
    return when (this) {
        GeminioTemplateScreen.NEW_PROJECT -> WizardUiContext.NewProject
        GeminioTemplateScreen.NEW_MODULE -> WizardUiContext.NewModule
        GeminioTemplateScreen.MENU_ENTRY -> WizardUiContext.MenuEntry
        GeminioTemplateScreen.ACTIVITY_GALLERY -> WizardUiContext.ActivityGallery
        GeminioTemplateScreen.FRAGMENT_GALLERY -> WizardUiContext.FragmentGallery
    }
}