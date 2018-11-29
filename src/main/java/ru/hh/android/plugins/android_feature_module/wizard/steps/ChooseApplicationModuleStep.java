package ru.hh.android.plugins.android_feature_module.wizard.steps;

import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import com.intellij.util.ui.UIUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import ru.hh.android.plugins.android_feature_module.models.ModuleListItem;
import ru.hh.android.plugins.android_feature_module.wizard.AndroidFeatureModuleWizardModel;
import ru.hh.android.plugins.android_feature_module.wizard.uikit.CheckboxesListView;

import javax.swing.*;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unchecked")
public class ChooseApplicationModuleStep extends WizardStep<AndroidFeatureModuleWizardModel> {

    @NotNull
    private static final String EMPTY_SELECTION_DESCRIPTION_TEXT = "Please select application to see its description";

    private JPanel contentPanel;
    private JList applicationsList;
    private JTextPane applicationDescriptionArea;
    private JButton enableAllButton;
    private JButton disableAllButton;

    @NotNull
    private final AndroidFeatureModuleWizardModel model;
    @NotNull
    private final List<ModuleListItem> applicationsModules;


    public ChooseApplicationModuleStep(
            @NotNull AndroidFeatureModuleWizardModel model,
            @NotNull List<ModuleListItem> applicationsModules
    ) {
        this.model = model;
        this.applicationsModules = applicationsModules;

        enableAllButton.addActionListener(e -> setAllModulesEnabled(true));
        disableAllButton.addActionListener(e -> setAllModulesEnabled(false));
    }


    @Override
    public JComponent prepare(WizardNavigationState state) {
        contentPanel.revalidate();
        applicationsList.requestFocusInWindow();
        return contentPanel;
    }


    public void initListView() {
        ((CheckboxesListView) applicationsList).setItems(applicationsModules, Collections.emptyList());
    }


    private void setAllModulesEnabled(boolean value) {
        for (ModuleListItem moduleListItem : applicationsModules) {
            model.setApplicationModuleEnabled(moduleListItem, value);
        }
        applicationsList.repaint();
    }

    private void createUIComponents() {
        applicationsList = new CheckboxesListView((Function1<ModuleListItem, Unit>) moduleListItem -> {
            if (moduleListItem == null) {
                applicationDescriptionArea.setText(UIUtil.toHtml(EMPTY_SELECTION_DESCRIPTION_TEXT));
            } else {
                String description = moduleListItem.getReadmeText();
                applicationDescriptionArea.setText(description);
                applicationDescriptionArea.moveCaretPosition(0);
            }

            return null;
        }, (Function1<ModuleListItem, Unit>) item -> {
            model.setApplicationModuleEnabled(item, item.isEnabled());
            return null;
        });
    }


}