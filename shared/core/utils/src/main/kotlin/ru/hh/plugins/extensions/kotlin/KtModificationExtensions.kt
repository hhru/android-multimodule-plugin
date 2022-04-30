package ru.hh.plugins.extensions.kotlin

import org.jetbrains.kotlin.psi.KtFile

private const val OBJECT_DECLARATION = "OBJECT_DECLARATION"
private const val CLASS_BODY = "CLASS_BODY"
private const val PROPERTY = "PROPERTY"

fun KtFile.getObjectDeclaration() = children.find {
    it.toString() == OBJECT_DECLARATION
}

fun KtFile.getClassBody() = getObjectDeclaration()?.children?.find {
    it.toString() == CLASS_BODY
}

fun KtFile.getProperty() = getClassBody()?.children?.find {
    it.toString() == PROPERTY
}