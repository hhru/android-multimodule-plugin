package ru.hh.android.plugin.extensions.psi.kotlin

import org.jetbrains.kotlin.psi.KtPsiFactory


private const val KT_TOKEN_LINE_BREAK = "\n"


fun KtPsiFactory.getBreakLineElement() = createWhiteSpace(KT_TOKEN_LINE_BREAK)