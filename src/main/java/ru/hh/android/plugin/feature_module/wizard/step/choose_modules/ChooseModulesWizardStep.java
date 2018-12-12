package ru.hh.android.plugin.feature_module.wizard.step.choose_modules;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import org.jetbrains.annotations.NotNull;
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel;

import javax.swing.*;


public class ChooseModulesWizardStep extends WizardStep<PluginWizardModel> {

    @NotNull
    private final ChooseModulesPresenter controller;

    public ChooseModulesWizardStep(@NotNull ChooseModulesPresenter controller) {
        this.controller = controller;
    }

    @Override
    public JComponent prepare(WizardNavigationState state) {
        return null;
    }

}