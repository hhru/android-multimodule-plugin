package ru.hh.android.plugin.core.framework_ui.view

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.operation
import com.intellij.openapi.observable.util.transform
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.bindText
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.framework_ui.UiConstants
import ru.hh.android.plugin.extensions.isCorrectPackageName
import ru.hh.android.plugin.extensions.toPackageNameFromModuleName
import ru.hh.plugins.extensions.layout.enabledIfCompat
import ru.hh.plugins.extensions.layout.visibleIfCompat
import java.awt.Color
import javax.swing.AbstractButton
import javax.swing.BorderFactory

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
    defaultModuleName: String = PluginConstants.DEFAULT_MODULE_NAME,
    defaultPackageName: String = PluginConstants.DEFAULT_PACKAGE_NAME,
    private val dialogDisposable: Disposable? = null,
    private val onErrorAction: (Boolean) -> Unit = {}
) {
    private val propertyGraph = PropertyGraph()
    private val moduleNameProperty = propertyGraph.property(defaultModuleName)
    private val packageNameProperty = propertyGraph.property(defaultPackageName)
    private val isPackageNameInEditModeProperty = propertyGraph.property(false)
    private val isPackageNameFieldHasErrorProperty = propertyGraph.property(false)

    val moduleName: String by moduleNameProperty
    val packageName: String by packageNameProperty

    fun create(panel: Panel) {
        with(panel) {
            createModuleNameSection()
            createPackageNameSection()
        }
        isPackageNameFieldHasErrorProperty.afterChange(dialogDisposable, onErrorAction)
        packageNameProperty.dependsOn(moduleNameProperty, ::suggestPackageName)
        packageNameProperty.afterChange(dialogDisposable, ::onPackageNameChanged)
    }

    private fun Panel.createModuleNameSection() {
        row {
            textField()
                .bindText(moduleNameProperty)
                .label(moduleNameSectionLabel, LabelPosition.TOP)
                .resizableColumn()
                .align(Align.FILL)
        }
    }

    private fun Panel.createPackageNameSection() {
        row {
            packageNameTextField(
                packageNameProperty = packageNameProperty,
                packageNameSectionLabel = packageNameSectionLabel,
                isInEditModeObservable = isPackageNameInEditModeProperty,
                hasErrorObservable = isPackageNameFieldHasErrorProperty,
            )
            editButton(
                isInEditModeProperty = isPackageNameInEditModeProperty,
                hasErrorObservable = isPackageNameFieldHasErrorProperty,
            )
        }
        row {
            label("Invalid target package name specified")
                .visibleIfCompat(isPackageNameFieldHasErrorProperty)
                .applyToComponent {
                    foreground = errorColor
                }
        }
    }

    private fun onPackageNameChanged(newPackageName: String) {
        if (newPackageName.isNotBlank()) {
            isPackageNameFieldHasErrorProperty.set(newPackageName.isCorrectPackageName().not())
        }
    }

    private fun suggestPackageName(): String = moduleNameProperty.get().toPackageNameFromModuleName()

    private companion object {
        private val errorColor = Color.decode(UiConstants.DARK_THEME_ERROR_COLOR)

        fun Row.packageNameTextField(
            packageNameProperty: ObservableMutableProperty<String>,
            packageNameSectionLabel: String,
            isInEditModeObservable: ObservableProperty<Boolean>,
            hasErrorObservable: ObservableProperty<Boolean>,
        ): Cell<JBTextField> {
            val cell = textField()
                .bindText(packageNameProperty)
                .label(packageNameSectionLabel, LabelPosition.TOP)
                .resizableColumn()
                .align(Align.FILL)
                .enabledIfCompat(isInEditModeObservable)

            val normalBorder = cell.component.border
            val errorBorder = BorderFactory.createLineBorder(errorColor, 1, true)

            val observableBorder = hasErrorObservable.transform {
                if (it) errorBorder else normalBorder
            }

            cell.component.border = observableBorder.get()
            observableBorder.afterChange { hasError ->
                cell.component.border = hasError
            }

            return cell
        }

        fun Row.editButton(
            isInEditModeProperty: ObservableMutableProperty<Boolean>,
            hasErrorObservable: ObservableProperty<Boolean>,
        ): Cell<AbstractButton> {
            val observableTitle = isInEditModeProperty.transform {
                if (it) "Done" else "Edit"
            }
            val enabledObservable = operation(isInEditModeProperty, hasErrorObservable) { isInEditMode, hasErrors ->
                if (isInEditMode) !hasErrors else true
            }

            val button = button(observableTitle.get()) {
                isInEditModeProperty.set(!isInEditModeProperty.get())
            }.enabledIfCompat(enabledObservable)

            observableTitle.afterChange { newTitle ->
                button.component.text = newTitle
            }

            return button
        }
    }
}
