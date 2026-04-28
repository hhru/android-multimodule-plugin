package ru.hh.plugins.psi_utils.xml

import android.databinding.tool.ext.toCamelCase
import com.intellij.psi.xml.XmlFile
import ru.hh.plugins.PluginsConstants

fun XmlFile.extractClassNameFromFileName(): String {
    return name.removeSuffix(PluginsConstants.XML_FILE_EXTENSION).toCamelCase().capitalize()
}
