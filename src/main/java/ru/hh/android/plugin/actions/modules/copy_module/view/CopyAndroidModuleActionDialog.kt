package ru.hh.android.plugin.actions.modules.copy_module.view

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.GrowPolicy
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.core.framework_ui.view.ModuleNamePanel
import ru.hh.android.plugin.services.modules.ModuleRepository
import ru.hh.android.plugin.utils.PluginBundle.message
import ru.hh.android.plugin.utils.showErrorMessage
import java.awt.BorderLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel


class CopyAndroidModuleActionDialog(
    private val project: Project,
    private val moduleName: String
) : DialogWrapper(project, true) {

    private val appModuleComboBoxModel = ModuleRepository.getInstance(project).fetchAppModules()
        .run { CollectionComboBoxModel(this) }

    private val moduleNamePanel = ModuleNamePanel(
        moduleNameSectionLabel = message("geminio.common.forms.new_module_name"),
        packageNameSectionLabel = message("geminio.common.forms.new_module_package_name"),
        onErrorAction = { hasError ->
            this.isOKActionEnabled = hasError.not()
        }
    )

    private var appModuleComboBox: JComboBox<Module>? = null


    init {
        init()
        title = message("geminio.forms.copy_module.title")
    }


    override fun createCenterPanel(): JComponent? = JPanel(BorderLayout())

    @Suppress("UnstableApiUsage")
    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                    text = message("geminio.forms.copy_module.label.0", moduleName),
                    bold = true
                )
            }

            moduleNamePanel.create(this)

            titledRow(message("geminio.forms.copy_module.application_module")) {
                row {
                    cell {
                        val cellBuilder = comboBox(
                            model = appModuleComboBoxModel,
                            getter = { appModuleComboBoxModel.selected },
                            setter = { /* do nothing */ },
                            growPolicy = GrowPolicy.SHORT_TEXT,
                            renderer = null
                        ).also { appModuleComboBox = it.component }
                        cellBuilder.component(CCFlags.growX)
                    }
                }
            }
        }
    }

    override fun doOKAction() {
        if (appModuleComboBoxModel.selected != null) {
            super.doOKAction()
        } else {
            appModuleComboBox?.let { comboBox ->
                showErrorMessage(project, message("geminio.errors.copy_module.app_module_not_selected"), comboBox)
            }
        }
    }


    fun getModuleName(): String = moduleNamePanel.getModuleName()

    fun getPackageName(): String = moduleNamePanel.getPackageName()

    fun getSelectedModule(): Module = appModuleComboBoxModel.selected!!

}