package ru.hh.android.plugin.actions.boilerplate.fragment_view_model

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty

class GenerateFragmentViewModelPsiElements(
    val singleEventClassPsiFile: KtFile,
    val uiStateClassPsiFile: KtFile,
    val uiStateClassConverterPsiFile: KtFile,
    val viewModelClassPsiFile: KtFile,
    val viewModelKtProperty: KtProperty,
    val handleEventKtFunction: KtFunction,
    val renderStateKtFunction: KtFunction,
)
