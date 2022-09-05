package com.android.tools.idea.ui.wizard

import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardDialog
import com.intellij.openapi.project.Project

/**
 * Special stub for Geminio's compilation usage.
 */
@Suppress("UNUSED_PARAMETER")
class StudioWizardDialogBuilder(
    wizard: ModelWizard,
    title: String,
) {

    private companion object {
        const val ERROR =
            "This is stub for com.android.tools.idea.wizard.ui.StudioWizardDialogBuilder from Android Studio <= Chipmunk"
    }

    init {
        throw NotImplementedError(ERROR)
    }

    fun setProject(project: Project): StudioWizardDialogBuilder {
        throw NotImplementedError(ERROR)
    }

    fun build(): ModelWizardDialog {
        throw NotImplementedError(ERROR)
    }

}