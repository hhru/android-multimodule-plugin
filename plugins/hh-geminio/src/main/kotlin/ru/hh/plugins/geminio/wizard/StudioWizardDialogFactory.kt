package ru.hh.plugins.geminio.wizard

import com.android.tools.idea.wizard.model.ModelWizard
import ru.hh.plugins.logger.HHLogger

internal object StudioWizardDialogFactory {

    private const val DOLPHIN_STUDIO_WIZARD_FQN = "com.android.tools.idea.wizard.ui.StudioWizardDialogBuilder"
    private const val CHIPMUNK_STUDIO_WIZARD_FQN = "com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder"


    fun getWizardBuilder(wizard: ModelWizard, title: String): WizardDialogBuilder {
        HHLogger.d("Try to find studio dialogs...")
        return when {
            isClassPresent(DOLPHIN_STUDIO_WIZARD_FQN) -> {
                HHLogger.i("Find Android Studio Dolphin's version of Studio wizard (`$DOLPHIN_STUDIO_WIZARD_FQN`)")
                AndroidStudioDolphinWizardDialogBuilder(wizard, title)
            }

            isClassPresent(CHIPMUNK_STUDIO_WIZARD_FQN) -> {
                // Compatibility with Android Studio Chipmunk
                HHLogger.i("Find Android Studio Chipmunk's version of Studio wizard (`$CHIPMUNK_STUDIO_WIZARD_FQN`)")
                AndroidStudioChipmunkWizardDialogBuilder(wizard, title)
            }

            else -> {
                throw IllegalStateException("There is no wizard builders classes")
            }
        }
    }


    private fun isClassPresent(fullyQualifiedClasName: String): Boolean {
        return try {
            StudioWizardDialogFactory::class.java.classLoader.loadClass(fullyQualifiedClasName)
            true
        } catch (ex: ClassNotFoundException) {
            false
        }
    }

}
