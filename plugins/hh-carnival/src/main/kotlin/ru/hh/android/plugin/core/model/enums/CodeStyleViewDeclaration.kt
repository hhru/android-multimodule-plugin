package ru.hh.android.plugin.core.model.enums

import com.android.SdkConstants

private const val ANDROID_WIDGET_PKG = "android.widget"
private const val ANDROIDX_APPCOMPAT_WIDGET_PKG = "${SdkConstants.ANDROIDX_APPCOMPAT_PKG}.widget"

/**
 * Enum for holding hh.ru code style for XML views identifiers.
 *
 * ALARM: Enum values definition order is important because of inheritance.
 */
enum class CodeStyleViewDeclaration(
    val idPrefix: String,
    val androidWidgetsClasses: List<String>
) {

    COLLAPSING_TOOLBAR_LAYOUT(
        idPrefix = "collapsing_toolbar",
        androidWidgetsClasses = listOf(
            SdkConstants.COLLAPSING_TOOLBAR_LAYOUT.newName()
        )
    ),

    APP_BAR_LAYOUT(
        idPrefix = "app_bar",
        androidWidgetsClasses = listOf(
            SdkConstants.APP_BAR_LAYOUT.newName()
        )
    ),

    TOOLBAR(
        idPrefix = "toolbar",
        androidWidgetsClasses = listOf(
            SdkConstants.TOOLBAR_V7.newName()
        )
    ),

    RECYCLER_VIEW(
        idPrefix = "recycler",
        androidWidgetsClasses = listOf(
            SdkConstants.RECYCLER_VIEW.newName()
        )
    ),

    SWIPE_REFRESH_LAYOUT(
        idPrefix = "swipe_refresh",
        androidWidgetsClasses = listOf(
            "androidx.swiperefreshlayout.widget.SwipeRefreshLayout"
        )
    ),

    SWITCH(
        idPrefix = "switch",
        androidWidgetsClasses = listOf(
            "android.widget.Switch",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.SwitchCompat"
        )
    ),

    SEEK_BAR(
        idPrefix = "seek_bar",
        androidWidgetsClasses = listOf(
            "android.widget.SeekBar",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatSeekBar"
        )
    ),

    PROGRESS_BAR(
        idPrefix = "progress",
        androidWidgetsClasses = listOf(
            "$ANDROID_WIDGET_PKG.${SdkConstants.PROGRESS_BAR}"
        )
    ),

    IMAGE_VIEW(
        idPrefix = "image",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_IMAGE_VIEW,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatImageView"
        )
    ),

    CHECKBOX(
        idPrefix = "checkbox",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_CHECK_BOX,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatCheckBox"
        )
    ),

    BUTTON(
        idPrefix = "button",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_BUTTON,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatButton"
        )
    ),

    EDIT_TEXT(
        idPrefix = "edit_text",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_EDIT_TEXT,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatEditText"
        )
    ),

    TEXT_VIEW(
        idPrefix = "text_view",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_TEXT_VIEW,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatTextView"
        )
    ),

    VIEW_GROUP(
        idPrefix = "container",
        androidWidgetsClasses = listOf(
            "android.view.ViewGroup"
        )
    ),

    VIEW(
        idPrefix = "view",
        androidWidgetsClasses = listOf(
            SdkConstants.CLASS_VIEW
        )
    )
}
