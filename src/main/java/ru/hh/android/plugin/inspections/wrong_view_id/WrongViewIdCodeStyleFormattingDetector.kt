@file:Suppress("UnstableApiUsage")

package ru.hh.android.plugin.inspections.wrong_view_id

import com.android.SdkConstants
import com.android.tools.lint.detector.api.*
import org.jetbrains.android.facet.LayoutViewClassUtils
import org.w3c.dom.Attr
import ru.hh.android.plugin.core.model.enums.extensions.findClosestViewClassDeclaration
import ru.hh.android.plugin.extensions.lint.androidFacet
import ru.hh.android.plugin.extensions.lint.fileNameWithoutExtension


/**
 * Detector for checking Views id's attribute for code style compliance.
 */
class WrongViewIdCodeStyleFormattingDetector : LayoutDetector() {

    companion object {

        const val ISSUE_ID = "WrongViewIdCodeStyleFormatting"

        val ISSUE: Issue
            get() {
                return Issue.create(
                    id = ISSUE_ID,
                    briefDescription = """
                    Brief description
                    """,
                    explanation = """
                    Full explanation of this issue
                    """,
                    category = Category.LINT,
                    enabledByDefault = true,
                    severity = Severity.WARNING,
                    priority = 5,
                    implementation = Implementation(
                        WrongViewIdCodeStyleFormattingDetector::class.java,
                        Scope.RESOURCE_FILE_SCOPE
                    )
                )
            }

    }


    override fun getApplicableAttributes(): Collection<String>? = listOf(SdkConstants.ATTR_ID)

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val androidFacet = context.androidFacet ?: return
        val tagPsiClass = LayoutViewClassUtils.findClassByTagName(
            androidFacet,
            attribute.ownerElement.localName,
            SdkConstants.CLASS_VIEW
        ) ?: return

        val closestViewClassDeclaration = tagPsiClass.findClosestViewClassDeclaration()

        val expectedIdPrefix = "${context.fileNameWithoutExtension}_${closestViewClassDeclaration.idPrefix}"


        if (stripIdPrefix(attribute.value).startsWith(expectedIdPrefix).not()) {
            // TODO add quick fix
            context.report(
                issue = ISSUE,
                scope = attribute,
                location = context.getLocation(attribute),
                message = "Wrong id declaration! Should have prefix `$expectedIdPrefix`"
            )
        }
    }

}