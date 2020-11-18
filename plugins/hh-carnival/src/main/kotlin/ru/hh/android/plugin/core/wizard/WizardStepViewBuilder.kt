package ru.hh.android.plugin.core.wizard

import javax.swing.JComponent

/**
 * Interface for building view
 */
interface WizardStepViewBuilder {

    fun build(): JComponent

    fun collectFormState(): WizardStepFormState

}