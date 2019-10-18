package ru.hh.android.plugin.core.ui.wizard

import javax.swing.JComponent


interface StepView {

    fun build(): JComponent

}