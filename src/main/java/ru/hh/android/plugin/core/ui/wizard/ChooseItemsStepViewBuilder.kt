package ru.hh.android.plugin.core.ui.wizard

import com.intellij.ui.layout.panel
import ru.hh.android.plugin.core.ui.custom_view.CheckBoxListView
import ru.hh.android.plugin.core.ui.model.CheckBoxListViewItem
import ru.hh.android.plugin.core.wizard.EmptyFormState
import ru.hh.android.plugin.core.wizard.WizardStepFormState
import ru.hh.android.plugin.core.wizard.WizardStepViewBuilder
import ru.hh.android.plugin.extensions.layout.boldLabel
import ru.hh.android.plugin.extensions.layout.onTextChange
import javax.swing.JComponent
import javax.swing.JTextField


class ChooseItemsStepViewBuilder<T : CheckBoxListViewItem>(
        private val textBundle: ChooseItemsStepViewTextBundle,
        private val onFilterTextChanged: (String) -> Unit,
        private val onModuleSelectionChanged: (T) -> Unit,
        private val onModuleItemChecked: (T) -> Unit,
        private val onEnableAllButtonClicked: () -> Unit,
        private val onDisableAllButtonClicked: () -> Unit
) : WizardStepViewBuilder {

    private lateinit var filterModulesJTextField: JTextField
    private lateinit var modulesJList: CheckBoxListView<T>


    override fun build(): JComponent {
        return panel {
            row { boldLabel(textBundle.descriptionMessage) }

            titledRow(textBundle.filterTextFieldMessage) {
                row {
                    filterModulesJTextField = JTextField().apply {
                        onTextChange { onFilterTextChanged.invoke(filterModulesJTextField.text) }
                    }
                    filterModulesJTextField()
                }
            }

            titledRow(textBundle.listDescriptionMessage) {
                row {
                    modulesJList = CheckBoxListView(
                            onItemSelectedListener = { onModuleSelectionChanged.invoke(it) },
                            onItemToggleChangedListener = { onModuleItemChecked.invoke(it) }
                    )

                    scrollPane(modulesJList)
                }
            }

            row {
                cell {
                    button("Enable all") { onEnableAllButtonClicked.invoke() }
                    button("Disable all") { onDisableAllButtonClicked.invoke() }
                }
            }
        }
    }

    override fun collectFormState(): WizardStepFormState = EmptyFormState


    fun showItems(items: List<T>) {
        modulesJList.setItems(items)
    }

}