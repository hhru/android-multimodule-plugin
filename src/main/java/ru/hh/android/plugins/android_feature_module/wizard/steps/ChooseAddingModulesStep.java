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
import java.util.List;

@SuppressWarnings("unchecked")
public class ChooseAddingModulesStep extends WizardStep<AndroidFeatureModuleWizardModel> {

    @NotNull
    private static final String EMPTY_SELECTION_DESCRIPTION_TEXT = "Select a library to see its description";


    private JPanel contentPanel;
    private JList librariesList;
    private JTextPane libraryDescriptionArea;
    private JButton enableAllButton;
    private JButton disableAllButton;


    @NotNull
    private final AndroidFeatureModuleWizardModel model;
    @NotNull
    private final List<ModuleListItem> librariesModulesItems;


    public ChooseAddingModulesStep(
            @NotNull AndroidFeatureModuleWizardModel model,
            @NotNull List<ModuleListItem> librariesModulesItems
    ) {
        this.model = model;
        this.librariesModulesItems = librariesModulesItems;

        setupButtons();
    }


    @Override
    public WizardStep onNext(AndroidFeatureModuleWizardModel model) {
        WizardStep next = super.onNext(model);

        if (next instanceof ChooseApplicationModuleStep) {
            ((ChooseApplicationModuleStep) next).initListView();
        }

        return next;
    }

    @Override
    public JComponent prepare(WizardNavigationState state) {
        contentPanel.revalidate();
        librariesList.requestFocusInWindow();
        return contentPanel;
    }


    public void initListView() {
        ((CheckboxesListView) librariesList).setItems(librariesModulesItems, model.getForceEnabledModules());
    }


    private void setupButtons() {
        enableAllButton.addActionListener(e -> setAllModulesEnabled(true));
        disableAllButton.addActionListener(e -> setAllModulesEnabled(false));
    }

    private void setAllModulesEnabled(boolean value) {
        for (ModuleListItem moduleListItem : librariesModulesItems) {
            model.setModuleEnabled(moduleListItem, value);
        }
        librariesList.repaint();
    }

    private void createUIComponents() {
        librariesList = new CheckboxesListView((Function1<ModuleListItem, Unit>) moduleListItem -> {
            if (moduleListItem == null) {
                libraryDescriptionArea.setText(UIUtil.toHtml(EMPTY_SELECTION_DESCRIPTION_TEXT));
            } else {
                String description = moduleListItem.getReadmeText();
                libraryDescriptionArea.setText(description);
                libraryDescriptionArea.moveCaretPosition(0);
            }

            return null;
        }, (Function1<ModuleListItem, Unit>) item -> {
            model.setModuleEnabled(item, item.isEnabled());
            return null;
        });
    }

}