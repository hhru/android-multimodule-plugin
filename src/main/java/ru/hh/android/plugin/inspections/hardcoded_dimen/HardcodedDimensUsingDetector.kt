package ru.hh.android.plugin.inspections.hardcoded_dimen

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr


@Suppress("UnstableApiUsage")
class HardcodedDimensUsingDetector : LayoutDetector() {

    companion object {

        const val ISSUE_ID = "HardcodedDimensUsing"

        val ISSUE: Issue
            get() {
                return Issue.create(
                    id = ISSUE_ID,
                    briefDescription = """
                    Lint rule for checking using of hardcoded dimens.
                    """,
                    explanation = """
                    Lint rule for checking using of hardcoded dimens.
                    """,
                    category = Category.LINT,
                    enabledByDefault = true,
                    severity = Severity.WARNING,
                    priority = 5,
                    implementation = Implementation(
                        HardcodedDimensUsingDetector::class.java,
                        Scope.RESOURCE_FILE_SCOPE
                    )
                )
            }

    }


    override fun getApplicableAttributes(): Collection<String>? = listOf(
        SdkConstants.ATTR_LAYOUT_HEIGHT,
        SdkConstants.ATTR_LAYOUT_WIDTH,

        SdkConstants.ATTR_LAYOUT_MARGIN,
        SdkConstants.ATTR_LAYOUT_MARGIN_BOTTOM,
        SdkConstants.ATTR_LAYOUT_MARGIN_TOP,
        SdkConstants.ATTR_LAYOUT_MARGIN_RIGHT,
        SdkConstants.ATTR_LAYOUT_MARGIN_END,
        SdkConstants.ATTR_LAYOUT_MARGIN_LEFT,
        SdkConstants.ATTR_LAYOUT_MARGIN_START,

        SdkConstants.ATTR_LAYOUT_MARGIN_HORIZONTAL,
        SdkConstants.ATTR_LAYOUT_MARGIN_VERTICAL,

        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_BOTTOM,
        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_TOP,
        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_RIGHT,
        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_END,
        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_START,
        SdkConstants.ATTR_LAYOUT_GONE_MARGIN_LEFT,

        SdkConstants.ATTR_PADDING,
        SdkConstants.ATTR_PADDING_BOTTOM,
        SdkConstants.ATTR_PADDING_TOP,
        SdkConstants.ATTR_PADDING_RIGHT,
        SdkConstants.ATTR_PADDING_END,
        SdkConstants.ATTR_PADDING_LEFT,
        SdkConstants.ATTR_PADDING_START,
        SdkConstants.ATTR_DRAWABLE_PADDING
    )


    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (attribute.value.startsWith(SdkConstants.DIMEN_PREFIX).not()) {
            context.report(
                issue = ISSUE,
                scope = attribute,
                location = context.getLocation(attribute),
                message = "Don't use hardcoded dimens!"
            )
        }
    }

}