package ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import org.jetbrains.annotations.NotNull;
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel;

import javax.swing.*;


public class ChooseMainParametersWizardStep extends WizardStep<PluginWizardModel> {

    @NotNull
    private final ChooseMainParametersController controller;

    public ChooseMainParametersWizardStep(@NotNull ChooseMainParametersController controller) {
        this.controller = controller;
    }

    @Override
    public JComponent prepare(WizardNavigationState state) {
        return null;
    }

}