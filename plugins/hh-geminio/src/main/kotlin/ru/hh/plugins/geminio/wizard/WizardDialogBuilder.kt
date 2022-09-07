package ru.hh.plugins.geminio.wizard

import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardDialog
import com.intellij.openapi.project.Project

internal interface WizardDialogBuilder {

    val modelWizard: ModelWizard
    val title: String

    fun create(project: Project): ModelWizardDialog
}
