package ru.hh.plugins.extensions.psi.kotlin

import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.formatter.commitAndUnblockDocument
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.extensions.psi.reformatWithCodeStyle


fun KtFile.shortReferencesAndReformatWithCodeStyle() {
    this.commitAndUnblockDocument()
    ShortenReferences.DEFAULT.process(this)
    this.reformatWithCodeStyle()
}