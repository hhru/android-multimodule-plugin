package ru.hh.plugins.extensions.psi.xml

import com.intellij.psi.xml.XmlFile
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.extensions.toCamelCase


fun XmlFile.extractClassNameFromFileName(): String {
    return name.removeSuffix(PluginsConstants.XML_FILE_EXTENSION).toCamelCase().capitalize()
}