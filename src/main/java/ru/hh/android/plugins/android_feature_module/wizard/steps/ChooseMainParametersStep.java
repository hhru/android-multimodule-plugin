package ru.hh.android.plugins.android_feature_module.wizard.steps;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import ru.hh.android.plugins.android_feature_module.models.AndroidFeatureModuleType;
import ru.hh.android.plugins.android_feature_module.models.MainNewModuleParameters;
import ru.hh.android.plugins.android_feature_module.wizard.AndroidFeatureModuleWizardModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Objects;


public class ChooseMainParametersStep extends WizardStep<AndroidFeatureModuleWizardModel> {


    private JPanel contentPanel;
    private JPanel titlePanel;
    private JPanel configurationPanel;
    private JPanel libraryNamePanel;
    private JTextField libraryNameTextField;
    private JPanel moduleNamePanel;
    private JTextField moduleNameTextField;
    private JPanel packageNamePanel;
    private JTextField packageNameTextField;
    private JButton editPackageNameButton;
    private JLabel packageNameLabel;
    private JComboBox moduleTypeComboBox;
    private JPanel moduleTypePanel;
    private JPanel predefinedSettingsPanel;
    private JCheckBox enableMoxyCheckBox;
    private JCheckBox addUIModulesDependenciesCheckBox;
    private JCheckBox createAPIInterfaceCheckBox;
    private JCheckBox createRepositoryWithInteractorCheckBox;


    private boolean isPackageNameInEditMode = false;


    @Override
    public WizardStep onNext(final AndroidFeatureModuleWizardModel model) {
        final WizardStep next = super.onNext(model);

        MainNewModuleParameters mainNewModuleParameters = collectMainParameters();
        model.setMainParameters(mainNewModuleParameters);

        if (next instanceof ChooseAddingModulesStep) {
            ((ChooseAddingModulesStep) next).initListView();
        }

        return next;
    }

    @Override
    public WizardStep onPrevious(final AndroidFeatureModuleWizardModel model) {
        final WizardStep prev = super.onPrevious(model);

        model.setMainParams(null);

        return prev;
    }

    @Override
    public JComponent prepare(WizardNavigationState state) {
        return contentPanel;
    }

    private void createUIComponents() {
        initModuleTypeComboBox();

        setupLibraryNameChangedListener();
        setupModuleNameChangedListener();
        setupPackageNameChangedListener();
        setupEditPackageNameButtonListener();
    }


    private void setupModuleNameChangedListener() {
        moduleNameTextField = new JTextField();
    }

    private void setupLibraryNameChangedListener() {
        libraryNameTextField = new JTextField();
        libraryNameTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                onLibraryNameChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                onLibraryNameChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                onLibraryNameChanged();
            }

        });
    }

    private void setupPackageNameChangedListener() {
        packageNameLabel = new JLabel();
        packageNameTextField = new JTextField();
        packageNameTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                onPackageNameChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                onPackageNameChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                onPackageNameChanged();
            }

        });
    }

    private void setupEditPackageNameButtonListener() {
        editPackageNameButton = new JButton();
        editPackageNameButton.addActionListener(e -> {
            isPackageNameInEditMode = !isPackageNameInEditMode;

            editPackageNameButton.setText(isPackageNameInEditMode ? "Done" : "Edit");
            packageNameLabel.setVisible(!isPackageNameInEditMode);
            packageNameTextField.setVisible(isPackageNameInEditMode);
        });
    }


    @SuppressWarnings("unchecked")
    private void initModuleTypeComboBox() {
        moduleTypeComboBox = new ComboBox();
        moduleTypeComboBox.setModel(new EnumComboBoxModel(AndroidFeatureModuleType.class));
    }


    private void onLibraryNameChanged() {
        String newLibraryName = libraryNameTextField.getText();
        String m1 = StringsKt.replace(newLibraryName, " ", "", true);
        String m2 = StringsKt.replace(m1, '-', '_', true);
        String m3 = m2.toLowerCase();


        moduleNameTextField.setText(m3);
    }

    private void onPackageNameChanged() {
        String currentPackageName = packageNameTextField.getText();
        packageNameLabel.setText(currentPackageName);
    }


    @NotNull
    private MainNewModuleParameters collectMainParameters() {
        return new MainNewModuleParameters()
                .libraryName(libraryNameTextField.getText())
                .moduleName(moduleNameTextField.getText())
                .packageName(packageNameTextField.getText())
                .moduleType((AndroidFeatureModuleType) Objects.requireNonNull(moduleTypeComboBox.getSelectedItem()))
                .enableMoxy(enableMoxyCheckBox.isSelected())
                .addUIModuleDependencies(addUIModulesDependenciesCheckBox.isSelected())
                .needCreateAPIInterface(createAPIInterfaceCheckBox.isSelected())
                .needCreateRepositoryWithInteractor(createRepositoryWithInteractorCheckBox.isSelected());
    }

}