package ru.hh.android.plugins.garcon.model.page_object

import com.intellij.psi.xml.XmlFile


sealed class PageObjectInitData

data class ScreenPageObjectInitData(
    val xmlFile: XmlFile,
    val className: String,
    val packageName: String
) : PageObjectInitData()

data class RecyclerViewItemPageObjectInitData(
    val xmlFile: XmlFile,
    val className: String
) : PageObjectInitData()