package ru.hh.plugins.garcon.actions.create_screen_page_object

import com.intellij.psi.xml.XmlFile
import com.intellij.refactoring.MoveDestination


data class CreateScreenPageObjectDialogResult(
    val xmlFile: XmlFile,
    val className: String,
    val packageName: String,
    val targetMoveDestination: MoveDestination,
    val openInEditor: Boolean
)