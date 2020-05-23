package ru.hh.android.plugin.annotators

import com.android.SdkConstants
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import org.jetbrains.android.dom.wrappers.LazyValueResourceElementWrapper
import kotlin.system.measureTimeMillis


/**
 * Annotator which can mark using of Android resource with deprecate highlighting,
 * if it has attribute `deprecated="true"` in its declaration.
 */
class DeprecatedAndroidResourceAnnotator : Annotator {

    companion object {
        private const val DEPRECATED_ATTRIBUTE_NAME = "deprecated"
        private const val DEPRECATED_TRUE_VALUE = "true"

        private const val INSPECTION_MESSAGE = "This resource is marked as deprecated. Are you sure you need to use it?"

        private val RESOURCES_PREFIXES = listOf(
            SdkConstants.COLOR_RESOURCE_PREFIX,
            SdkConstants.STRING_PREFIX,
            SdkConstants.STYLE_RESOURCE_PREFIX,
            SdkConstants.DIMEN_PREFIX
        )
    }

    private val logger = Logger.getInstance(DeprecatedAndroidResourceAnnotator::class.java)


    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is XmlAttribute) {
            val attrValueElement = element.valueElement ?: return

            if (attrValueElement.isAcceptableResourceRef() && attrValueElement.hasDeprecatedMark()) {
                logger.debug("Resource `${attrValueElement.value}` is deprecated -> mark it")
                markValueRefAsDeprecated(attrValueElement, holder)
            }
        }
    }


    private fun markValueRefAsDeprecated(valueElement: XmlAttributeValue, holder: AnnotationHolder) {
        val annotation = holder.createWarningAnnotation(
            TextRange(valueElement.textRange.startOffset + 1, valueElement.textRange.endOffset - 1),
            INSPECTION_MESSAGE
        )
        annotation.highlightType = ProblemHighlightType.LIKE_DEPRECATED
    }

    private fun XmlAttributeValue.isAcceptableResourceRef(): Boolean {
        return RESOURCES_PREFIXES.any { this.value.startsWith(it) }
    }

    private fun XmlAttributeValue.hasDeprecatedMark(): Boolean {
        var result = false
        val computationTime = measureTimeMillis {
            result = ((this.reference?.resolve() as? LazyValueResourceElementWrapper)
                ?.resourceInfo
                ?.computeXmlElement()
                ?.parent
                ?.parent as? XmlTag)
                ?.getAttributeValue(DEPRECATED_ATTRIBUTE_NAME) == DEPRECATED_TRUE_VALUE
        }
        logger.debug("Checked if XML Android resource `${this.value}` is deprecated (result: $result) in $computationTime ms")
        return result
    }

}