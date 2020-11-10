package ru.hh.android.plugins.garcon.extensions.psi

import com.android.tools.idea.util.androidFacet
import com.intellij.psi.xml.XmlDocument
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.facet.LayoutViewClassUtils
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.model.AndroidViewTagInfo


private const val VIEW_ID_ATTRIBUTE_NAME = "id"
private const val VIEW_ID_VALUE_PREFIX = "@+id/"
private const val ANDROID_VIEW_BASE_CLASS_FQN = "android.view.View"


fun XmlFile.collectAndroidViewsTagsInfo(): List<AndroidViewTagInfo> {
    return androidFacet?.let { androidFacet ->
        document?.collectXmlTags()?.mapNotNull { it.toLayoutTagInfo(androidFacet, this.name) }
    } ?: emptyList()
}

private fun XmlDocument.collectXmlTags(): List<XmlTag> {
    val tags = mutableListOf<XmlTag>()

    rootTag?.let { tags += it }
    rootTag?.collectRecursively(tags)

    return tags
}


private fun XmlTag.collectRecursively(tags: MutableList<XmlTag>) {
    tags += this
    this.children.filterIsInstance<XmlTag>().forEach { it.collectRecursively(tags) }
}

private fun XmlTag.toLayoutTagInfo(androidFacet: AndroidFacet, xmlFileName: String): AndroidViewTagInfo? {
    val idAttr = attributes.find { it.localName == VIEW_ID_ATTRIBUTE_NAME }
    val idValue = idAttr?.value?.removePrefix(VIEW_ID_VALUE_PREFIX) ?: String.EMPTY

    return when {
        idValue.isEmpty() -> {
            null
        }

        else -> {
            val psiClass = LayoutViewClassUtils.findClassByTagName(
                androidFacet,
                localName,
                ANDROID_VIEW_BASE_CLASS_FQN
            )

            psiClass?.let { AndroidViewTagInfo(id = idValue, xmlFieName = xmlFileName, tagPsiClass = it) }
        }
    }
}