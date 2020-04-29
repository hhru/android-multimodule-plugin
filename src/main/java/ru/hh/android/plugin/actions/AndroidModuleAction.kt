package ru.hh.android.plugin.actions

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.idea.util.module
import ru.hh.android.plugin.extensions.getSelectedPsiElement


/**
 * Wrapper for actions based on [org.jetbrains.android.facet.AndroidFacet].
 */
abstract class AndroidModuleAction : AnAction() {

    /**
     * Make action disabled if there is no information about [org.jetbrains.android.facet.AndroidFacet]
     * from current action event.
     */
    override fun update(e: AnActionEvent) {
        super.update(e)

        val selectedPsiElement = e.getSelectedPsiElement()
        val module = selectedPsiElement?.module
        val androidFacet = module?.androidFacet

        e.presentation.isEnabled = androidFacet != null
    }

}