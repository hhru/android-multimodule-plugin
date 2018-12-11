package ru.hh.android.plugin.feature_module.wizard.step.choose_applications;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import org.jetbrains.annotations.NotNull;
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel;
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesController;

import javax.swing.*;

public class ChooseApplicationsWizardStep extends WizardStep<PluginWizardModel> {

    @NotNull
    private final ChooseApplicationsController controller;

    public ChooseApplicationsWizardStep(@NotNull ChooseApplicationsController controller) {
        this.controller = controller;
    }

    @Override
    public JComponent prepare(WizardNavigationState state) {
        return null;
    }

}
