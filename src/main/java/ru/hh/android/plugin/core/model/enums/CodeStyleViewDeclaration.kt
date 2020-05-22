package ru.hh.android.plugin.core.model.enums

import com.android.SdkConstants


private const val ANDROID_WIDGET_PKG = "android.widget"
private const val ANDROIDX_APPCOMPAT_WIDGET_PKG = "${SdkConstants.ANDROIDX_APPCOMPAT_PKG}.widget"

/**
 * Enum for holding hh.ru code style for XML views identifiers.
 */
enum class CodeStyleViewDeclaration(
    val idPrefix: String,
    val androidWidgetsClasses: List<String>
) {

    CONTAINER(
        idPrefix = "container",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_FRAME_LAYOUT,
            SdkConstants.FQCN_LINEAR_LAYOUT,
            SdkConstants.FQCN_RELATIVE_LAYOUT,
            SdkConstants.FQCN_SCROLL_VIEW,
            "${ANDROID_WIDGET_PKG}.${SdkConstants.HORIZONTAL_SCROLL_VIEW}",
            SdkConstants.CLASS_CONSTRAINT_LAYOUT.newName(),
            SdkConstants.CLASS_COORDINATOR_LAYOUT.newName(),
            SdkConstants.CLASS_NESTED_SCROLL_VIEW.newName()
        )
    ),

    BUTTON(
        idPrefix = "button",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_BUTTON,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatButton"
        )
    ),

    CHECKBOX(
        idPrefix = "checkbox",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_CHECK_BOX,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatCheckBox"
        )
    ),

    TEXT_VIEW(
        idPrefix = "text_view",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_TEXT_VIEW,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatTextView"
        )
    ),

    RECYCLER_VIEW(
        idPrefix = "recycler",
        androidWidgetsClasses = listOf(
            SdkConstants.RECYCLER_VIEW.newName()
        )
    ),

    PROGRESS_BAR(
        idPrefix = "progress",
        androidWidgetsClasses = listOf(
            "${ANDROID_WIDGET_PKG}.${SdkConstants.PROGRESS_BAR}"
        )
    ),

    IMAGE_VIEW(
        idPrefix = "image",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_IMAGE_VIEW,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatImageView"
        )
    ),

    EDIT_TEXT(
        idPrefix = "edit_text",
        androidWidgetsClasses = listOf(
            SdkConstants.FQCN_EDIT_TEXT,
            "$ANDROIDX_APPCOMPAT_WIDGET_PKG.AppCompatEditText"
        )
    ),

    SWIPE_REFRESH_LAYOUT(
        idPrefix = "swipe_refresh",
        androidWidgetsClasses = listOf(
            "androidx.swiperefreshlayout.widget.SwipeRefreshLayout"
        )
    ),

    VIEW(
        idPrefix = "view",
        androidWidgetsClasses = listOf(
            SdkConstants.CLASS_VIEW
        )
    )

}