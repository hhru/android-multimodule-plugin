package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.Category
import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateCategory


/**
 * Mapping from [ru.hh.plugins.geminio.model.GeminioTemplateCategory] into
 * Android Studio's [com.android.tools.idea.wizard.template.Category].
 */
fun GeminioTemplateCategory.toAndroidStudioTemplateCategory(): Category {
    return when (this) {
        GeminioTemplateCategory.ACTIVITY -> Category.Activity
        GeminioTemplateCategory.FRAGMENT -> Category.Fragment
        GeminioTemplateCategory.APPLICATION -> Category.Application
        GeminioTemplateCategory.FOLDER -> Category.Folder
        GeminioTemplateCategory.UI_COMPONENT -> Category.UiComponent
        GeminioTemplateCategory.AUTOMOTIVE -> Category.Automotive
        GeminioTemplateCategory.XML -> Category.XML
        GeminioTemplateCategory.WEAR -> Category.Wear
        GeminioTemplateCategory.AIDL -> Category.AIDL
        GeminioTemplateCategory.WIDGET -> Category.Widget
        GeminioTemplateCategory.GOOGLE -> Category.Google
        GeminioTemplateCategory.COMPOSE -> Category.Compose
        GeminioTemplateCategory.OTHER -> Category.Other
    }
}