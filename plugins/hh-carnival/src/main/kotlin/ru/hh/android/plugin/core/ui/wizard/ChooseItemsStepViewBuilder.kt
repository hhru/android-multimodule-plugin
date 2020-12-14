package ru.hh.android.plugin.core.ui.wizard

import com.intellij.ui.layout.panel
import ru.hh.android.plugin.core.wizard.EmptyFormState
import ru.hh.android.plugin.core.wizard.WizardStepFormState
import ru.hh.android.plugin.core.wizard.WizardStepViewBuilder
import ru.hh.android.plugin.extensions.layout.boldLabel
import ru.hh.android.plugin.extensions.layout.onTextChange
import ru.hh.plugins.models.CheckBoxListViewItem
import ru.hh.plugins.views.CheckBoxListView
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder


class ChooseItemsStepViewBuilder<T : CheckBoxListViewItem>(
    private val textBundle: ChooseItemsStepViewTextBundle,
    private val onFilterTextChanged: (String) -> Unit,
    private val onModuleSelectionChanged: (T) -> Unit = {},
    private val onModuleItemChecked: (T) -> Unit,
    private val onEnableAllButtonClicked: () -> Unit,
    private val onDisableAllButtonClicked: () -> Unit,
    private val isReadmeBlockAvailable: Boolean = false
) : WizardStepViewBuilder {

    companion object {
        private const val TEXT_AREA_PADDING = 10
    }

    private lateinit var filterModulesJTextField: JTextField
    private lateinit var modulesJList: CheckBoxListView<T>
    private lateinit var readmeBlockTextArea: JEditorPane


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

            if (isReadmeBlockAvailable) {
                titledRow("Readme of selected module") {
                    row {
                        readmeBlockTextArea = JEditorPane().apply {
                            contentType = "text/html"
                            border =
                                EmptyBorder(TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING)
                        }
                        scrollPane(readmeBlockTextArea)
                    }
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

    fun changeReadmeText(text: String) {
        readmeBlockTextArea.text = text
    }

}