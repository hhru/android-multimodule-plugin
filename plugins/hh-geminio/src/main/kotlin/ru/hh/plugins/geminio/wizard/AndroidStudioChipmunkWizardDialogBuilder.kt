package ru.hh.plugins.geminio.wizard

import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.android.tools.idea.wizard.model.ModelWizardDialog
import com.intellij.openapi.project.Project
import ru.hh.plugins.logger.HHLogger

/**
 * [ModelWizardDialog] builder for Android Studio Chipmunk (version = 212.5712.43).
 *
 * IMPORTANT NOTE!!!
 *
 * Here is tricky part: we use our stub class [com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder] from
 * `:shared:core:android-studio-stubs` only for plugin COMPILATION. But not for usage in RUNTIME.
 */
internal class AndroidStudioChipmunkWizardDialogBuilder(
    override val modelWizard: ModelWizard,
    override val title: String,
) : WizardDialogBuilder {

    override fun create(project: Project): ModelWizardDialog {
        HHLogger.d("Try to create wizard dialog as in Android Studio Chipmunk...")
        return StudioWizardDialogBuilder(modelWizard, title)
            .setProject(project)
            .build()
    }

}