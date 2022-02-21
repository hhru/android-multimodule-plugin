package ru.hh.android.plugin.completions

import com.android.SdkConstants
import com.android.tools.idea.util.androidFacet
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class XmlDeprecatedCompletionContributor : CompletionContributor() {

    companion object {
        private const val DEPRECATED_ATTRIBUTE_COMPLETION = "deprecated=\"true\""

        private val ACCEPTED_TAGS_FOR_DEPRECATING = listOf(
            SdkConstants.TAG_DIMEN,
            SdkConstants.TAG_COLOR,
            SdkConstants.TAG_STRING,
            SdkConstants.TAG_STYLE,
            SdkConstants.TAG_PLURALS
        )
    }

    private val logger = Logger.getInstance(XmlDeprecatedCompletionContributor::class.java)

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val psiElement = parameters.position
        val androidFacet = psiElement.androidFacet
        if (androidFacet != null) {
            val parent = psiElement.parent
            val originalParent = parameters.originalPosition?.parent

            if (parent is XmlAttribute && originalParent is XmlTag) {
                if (originalParent.localName in ACCEPTED_TAGS_FOR_DEPRECATING) {
                    result.addElement(LookupElementBuilder.create(DEPRECATED_ATTRIBUTE_COMPLETION))
                }
            }
        }
    }
}
