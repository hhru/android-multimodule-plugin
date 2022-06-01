package ru.hh.plugins.geminio.util

import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardDialog
import com.android.tools.idea.wizard.ui.StudioWizardDialogBuilder
import com.intellij.openapi.project.Project

class Studio2022Point3WizardDialogFactory(
    private val wizard: ModelWizard,
    private val title: String,
) : StudioWizardDialogFactory {
    override fun create(project: Project): ModelWizardDialog {
        return StudioWizardDialogBuilder(wizard, title)
            .setProject(project)
            .build()
    }
}