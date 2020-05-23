@file:Suppress("UnstableApiUsage")

package ru.hh.android.plugin.inspections.wrong_view_id

import com.android.SdkConstants
import com.android.tools.idea.psi.TagToClassMapper
import com.android.tools.lint.detector.api.*
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
                    Lint rule for checking View's identifier that should be created in accordance with hh.ru code style.
                    """,
                    explanation = """
                    Lint rule for checking View's identifier that should be created in accordance with hh.ru code style.
                    
                    ViewGroup -> _container
                    ImageView -> _image
                    TextView -> text_view
                    EditText -> edit_text
                    View -> _view
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
        val tagPsiClass = TagToClassMapper.getInstance(androidFacet.module)
            .getClassMap(SdkConstants.CLASS_VIEW)[attribute.ownerElement.tagName]
            ?: return

        val closestViewClassDeclaration = tagPsiClass.findClosestViewClassDeclaration()

        val expectedIdPrefix = "${context.fileNameWithoutExtension}_${closestViewClassDeclaration.idPrefix}"

        if (attribute.hasCorrectViewIdFormatting(expectedIdPrefix).not()) {
            context.report(
                issue = ISSUE,
                scope = attribute,
                location = context.getLocation(attribute),
                message = "Wrong id declaration! Should have prefix `$expectedIdPrefix`",
                quickfixData = LintFix.create()
                    .replace()
                    .text(attribute.value)
                    .with("${attribute.getIdPrefix()}${expectedIdPrefix}")
                    .build()
            )
        }
    }


    private fun Attr.hasCorrectViewIdFormatting(expectedViewIdPrefix: String): Boolean {
        return stripIdPrefix(value).startsWith(expectedViewIdPrefix)
    }

    private fun Attr.getIdPrefix(): String {
        return when {
            value.startsWith(SdkConstants.NEW_ID_PREFIX) -> SdkConstants.NEW_ID_PREFIX
            else -> SdkConstants.ID_PREFIX
        }
    }

}