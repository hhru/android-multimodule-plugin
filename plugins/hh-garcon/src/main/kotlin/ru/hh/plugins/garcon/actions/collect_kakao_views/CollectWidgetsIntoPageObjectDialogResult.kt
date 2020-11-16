package ru.hh.plugins.garcon.actions.collect_kakao_views

import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.psi.KtClass


data class CollectWidgetsIntoPageObjectDialogResult(
    val xmlFile: XmlFile,
    val targetClass: KtClass,
    val openInEditor: Boolean
)