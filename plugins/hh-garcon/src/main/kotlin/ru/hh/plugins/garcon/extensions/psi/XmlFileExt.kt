package ru.hh.plugins.garcon.extensions.psi

import com.android.SdkConstants
import com.intellij.psi.XmlRecursiveElementVisitor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.plugins.psi_utils.androidManifestPackageName

val XmlFile.rFilePackageName: String
    get() {
        val packageName = androidManifestPackageName
        return if (packageName.isBlank()) SdkConstants.R_CLASS else "$packageName.${SdkConstants.R_CLASS}"
    }

fun XmlFile.collectAndroidViewsTagsInfo(): List<AndroidViewTagInfo> {
    val result = mutableListOf<AndroidViewTagInfo>()

    this.accept(object : XmlRecursiveElementVisitor() {
        override fun visitXmlTag(tag: XmlTag) {
            tag.toLayoutTagInfo()?.let { result += it }
            super.visitXmlTag(tag)
        }
    })

    return result
}

private fun XmlTag.toLayoutTagInfo(): AndroidViewTagInfo? {
    val idAttr = getAttribute("${SdkConstants.ANDROID_NS_NAME_PREFIX}${SdkConstants.ATTR_ID}")
    val idValue = idAttr?.value?.removePrefix(SdkConstants.NEW_ID_PREFIX) ?: String.EMPTY

    return when {
        idValue.isBlank() -> {
            null
        }

        else -> {
            toPsiClass()?.let { psiClass ->
                AndroidViewTagInfo(id = idValue, xmlFile = containingFile as XmlFile, tagPsiClass = psiClass)
            }
        }
    }
}
