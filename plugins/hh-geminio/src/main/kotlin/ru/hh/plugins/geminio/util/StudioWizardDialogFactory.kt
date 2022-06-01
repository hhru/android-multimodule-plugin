package ru.hh.plugins.geminio.util

import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper

interface StudioWizardDialogFactory {
    fun create(project: Project): DialogWrapper

    companion object {
        operator fun invoke(wizard: ModelWizard, title: String): StudioWizardDialogFactory {
            return try {
                Studio2022Point2AndLowerWizardDialogFactory(wizard, title)
            } catch (e: ClassNotFoundException) {
                Studio2022Point3WizardDialogFactory(wizard, title)
            }
        }
    }
}