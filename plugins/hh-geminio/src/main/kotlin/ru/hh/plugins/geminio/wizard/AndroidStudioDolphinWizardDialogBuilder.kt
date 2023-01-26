package ru.hh.plugins.geminio.wizard

import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardDialog
import com.android.tools.idea.wizard.ui.StudioWizardDialogBuilder
import com.intellij.openapi.project.Project
import ru.hh.plugins.logger.HHLogger

/**
 * [ModelWizardDialog] builder for Android Studio Dolphin (version = 221.6008.13).
 *
 * In Dolphin [ModelWizardDialog] creates through [com.android.tools.idea.wizard.ui.StudioWizardDialogBuilder] --
 * package name was changed in comparison with Chipmunk
 * (was [com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder])
 */
internal class AndroidStudioDolphinWizardDialogBuilder(
    override val modelWizard: ModelWizard,
    override val title: String,
) : WizardDialogBuilder {

    override fun create(project: Project): ModelWizardDialog {
        HHLogger.d("Try to create wizard dialog as in Android Studio Dolphin...")
        return StudioWizardDialogBuilder(modelWizard, title)
            .setProject(project)
            .build()
    }
}
