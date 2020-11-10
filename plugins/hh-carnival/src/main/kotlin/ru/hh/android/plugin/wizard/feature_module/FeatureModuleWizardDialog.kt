package ru.hh.android.plugin.wizard.feature_module

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardDialog
import com.intellij.util.ui.JBUI
import java.awt.Dimension

class FeatureModuleWizardDialog(
        project: Project,
        private val onGoalAchieved: (FeatureModuleWizardModel) -> Unit
) : WizardDialog<FeatureModuleWizardModel>(project, true, FeatureModuleWizardModel(project)) {

    companion object {
        private const val PREFERRED_DIALOG_WIDTH = 800
        private const val PREFERRED_DIALOG_HEIGHT = 450
    }


    override fun onWizardGoalAchieved() {
        super.onWizardGoalAchieved()
        onGoalAchieved.invoke(myModel)
    }

    override fun getPreferredSize(): Dimension {
        return JBUI.size(PREFERRED_DIALOG_WIDTH, PREFERRED_DIALOG_HEIGHT)
    }

}