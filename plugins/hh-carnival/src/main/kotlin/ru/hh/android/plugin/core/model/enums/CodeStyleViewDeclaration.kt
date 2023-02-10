package ru.hh.android.plugin.core.model.enums


private const val ANDROID_WIDGET_PKG = "android.widget"
private const val ANDROIDX_APPCOMPAT_WIDGET_PKG = "androidx.appcompat.widget"

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
            "com.google.android.material.appbar.CollapsingToolbarLayout"
        )
    ),

    APP_BAR_LAYOUT(
        idPrefix = "app_bar",
        androidWidgetsClasses = listOf(
            "com.google.android.material.appbar.AppBarLayout"
        )
    ),

    TOOLBAR(
        idPrefix = "toolbar",
        androidWidgetsClasses = listOf(
            "androidx.appcompat.widget.Toolbar"
        )
    ),

    RECYCLER_VIEW(
        idPrefix = "recycler",
        androidWidgetsClasses = listOf(
            "androidx.recyclerview.widget.RecyclerView"
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
            "$ANDROID_WIDGET_PKG.ProgressBar"
        )
    ),

    IMAGE_VIEW(
        idPrefix = "image",
        androidWidgetsClasses = listOf(
            "android.widget.ImageView",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatImageView"
        )
    ),

    CHECKBOX(
        idPrefix = "checkbox",
        androidWidgetsClasses = listOf(
            "android.widget.CheckBox",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatCheckBox"
        )
    ),

    BUTTON(
        idPrefix = "button",
        androidWidgetsClasses = listOf(
            "android.widget.Button",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatButton"
        )
    ),

    EDIT_TEXT(
        idPrefix = "edit_text",
        androidWidgetsClasses = listOf(
            "android.widget.EditText",
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatEditText"
        )
    ),

    TEXT_VIEW(
        idPrefix = "text_view",
        androidWidgetsClasses = listOf(
            "android.widget.TextView",
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
            "android.view.View"
        )
    )
}
