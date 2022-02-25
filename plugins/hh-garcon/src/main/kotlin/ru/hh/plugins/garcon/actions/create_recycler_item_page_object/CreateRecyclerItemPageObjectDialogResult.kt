package ru.hh.plugins.garcon.actions.create_recycler_item_page_object

import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.psi.KtClass

data class CreateRecyclerItemPageObjectDialogResult(
    val xmlFile: XmlFile,
    val className: String,
    val targetClass: KtClass,
    val openInEditor: Boolean
)
