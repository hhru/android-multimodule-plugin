package ru.hh.android.plugin.core.framework_ui.view

import com.intellij.ui.layout.LayoutBuilder
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.framework_ui.UiConstants
import ru.hh.android.plugin.extensions.isCorrectPackageName
import ru.hh.android.plugin.extensions.toPackageNameFromModuleName
import ru.hh.plugins.extensions.layout.onTextChange
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.border.Border

/**
 * Wrapper for two text fields:
 * - new module name
 * - new package name
 *
 * Hide logic with module name and package name changing, showing errors
 */
class ModuleNamePanel(
    private val moduleNameSectionLabel: String = "Module name",
    private val packageNameSectionLabel: String = "Package name",
    private val defaultModuleName: String = PluginConstants.DEFAULT_MODULE_NAME,
    private val defaultPackageName: String = PluginConstants.DEFAULT_PACKAGE_NAME,
    private val onErrorAction: (Boolean) -> Unit = {}
) {

    private val errorColor = Color.decode(UiConstants.DARK_THEME_ERROR_COLOR)

    private lateinit var moduleNameJTextField: JTextField

    private lateinit var packageNameJTextField: JTextField
    private lateinit var editPackageNameButton: JButton
    private lateinit var packageNameErrorLabel: JLabel

    private lateinit var normalTextFieldBorder: Border


    private var isPackageNameTextFieldEnabled: Boolean = false
        set(value) {
            field = value
            packageNameJTextField.isEnabled = value
        }

    private var isPackageNameTextFieldHasError: Boolean = false
        set(value) {
            field = value

            if (value) {
                packageNameJTextField.border = BorderFactory.createLineBorder(errorColor, 1, true)
                if (isPackageNameTextFieldEnabled) {
                    editPackageNameButton.isEnabled = false
                }
                packageNameErrorLabel.isVisible = true
            } else {
                packageNameJTextField.border = normalTextFieldBorder
                if (isPackageNameTextFieldEnabled) {
                    editPackageNameButton.isEnabled = true
                }
                packageNameErrorLabel.isVisible = false
            }
        }


    private var isPackageNameInEditMode = false
    private var isPackageNameWasChangedByUser = false
    private var wantTriggerPackageNameChanged = false


    fun create(layoutBuilder: LayoutBuilder) {
        with(layoutBuilder) {
            createModuleNameSection()
            createPackageNameSection()
        }
    }

    fun getModuleName(): String = moduleNameJTextField.text

    fun getPackageName(): String = packageNameJTextField.text


    private fun onModuleNameChanged(newModuleName: String) {
        if (isPackageNameWasChangedByUser) {
            return
        }

        wantTriggerPackageNameChanged = true
        packageNameJTextField.text = newModuleName.toPackageNameFromModuleName()
    }

    private fun onPackageNameChanged(newPackageName: String) {
        if (newPackageName.isNotBlank()) {
            if (wantTriggerPackageNameChanged) {
                wantTriggerPackageNameChanged = false
            } else {
                isPackageNameWasChangedByUser = true
            }

            isPackageNameTextFieldHasError = newPackageName.isCorrectPackageName().not()
            onErrorAction.invoke(isPackageNameTextFieldHasError)
        }
    }

    private fun onEditPackageNameButtonClicked() {
        editPackageNameButton.text = if (isPackageNameInEditMode) {
            "Edit"
        } else {
            "Done"
        }

        isPackageNameTextFieldEnabled = isPackageNameInEditMode.not()
        isPackageNameInEditMode = !isPackageNameInEditMode
    }


    @Suppress("UnstableApiUsage")
    private fun LayoutBuilder.createModuleNameSection() {
        titledRow(moduleNameSectionLabel) {
            row {
                cell {
                    moduleNameJTextField = JTextField(defaultModuleName).apply {
                        onTextChange { onModuleNameChanged(moduleNameJTextField.text) }
                    }
                    moduleNameJTextField(growX)

                    moduleNameJTextField()
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun LayoutBuilder.createPackageNameSection() {
        titledRow(packageNameSectionLabel) {
            row {
                cell {
                    packageNameJTextField = JTextField(defaultPackageName).apply {
                        onTextChange { onPackageNameChanged(packageNameJTextField.text) }
                        isEnabled = false
                    }
                    packageNameJTextField(growX)

                    normalTextFieldBorder = packageNameJTextField.border
                    packageNameJTextField()
                }

                cell {
                    editPackageNameButton = JButton("Edit").apply {
                        addActionListener { onEditPackageNameButtonClicked() }
                    }

                    editPackageNameButton()
                }
            }
            row {
                packageNameErrorLabel = JLabel("Invalid target package name specified").apply {
                    foreground = errorColor
                    isVisible = false
                }
                packageNameErrorLabel()
            }
        }
    }

}