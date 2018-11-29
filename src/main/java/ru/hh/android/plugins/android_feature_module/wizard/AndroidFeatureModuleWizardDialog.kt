package ru.hh.android.plugins.android_feature_module.wizard

import com.intellij.ui.wizard.WizardDialog
import com.intellij.util.ui.JBUI
import java.awt.Dimension


class AndroidFeatureModuleWizardDialog(
        private val listener: OnFinishButtonClickListener
) : WizardDialog<AndroidFeatureModuleWizardModel>(
        true,
        true,
        AndroidFeatureModuleWizardModel(null)
) {

    companion object {
        private const val PREFERRED_DIALOG_WIDTH = 800
        private const val PREFERRED_DIALOG_HEIGHT = 450
    }


    override fun onWizardGoalAchieved() {
        super.onWizardGoalAchieved()
        listener.onFinishButtonClicked(myModel)
    }

    override fun getPreferredSize(): Dimension {
        return JBUI.size(PREFERRED_DIALOG_WIDTH, PREFERRED_DIALOG_HEIGHT)
    }


    interface OnFinishButtonClickListener {

        fun onFinishButtonClicked(model: AndroidFeatureModuleWizardModel)

    }

}