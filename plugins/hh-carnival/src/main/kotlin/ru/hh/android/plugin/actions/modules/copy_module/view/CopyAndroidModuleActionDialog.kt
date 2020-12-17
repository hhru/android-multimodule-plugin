package ru.hh.android.plugin.actions.modules.copy_module.view

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.PluginConstants.DEFAULT_GH_MODULE_PREFIX
import ru.hh.android.plugin.core.framework_ui.view.ModuleNamePanel
import ru.hh.android.plugin.extensions.toPackageNameFromModuleName
import ru.hh.android.plugin.utils.showErrorMessage
import ru.hh.plugins.extensions.openapi.getAndroidApplicationsModules
import java.awt.BorderLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel


class CopyAndroidModuleActionDialog(
    private val project: Project,
    private val moduleName: String
) : DialogWrapper(project, true) {

    private val appModuleComboBoxModel = project.getAndroidApplicationsModules()
        .run { CollectionComboBoxModel(this) }

    private val moduleNamePanel = ModuleNamePanel(
        moduleNameSectionLabel = "New module name",
        packageNameSectionLabel = "New module package name",
        defaultModuleName = "$DEFAULT_GH_MODULE_PREFIX$moduleName",
        defaultPackageName = "$DEFAULT_GH_MODULE_PREFIX$moduleName".toPackageNameFromModuleName(),
        onErrorAction = { hasError ->
            this.isOKActionEnabled = hasError.not()
        }
    )

    private var appModuleComboBox: JComboBox<Module>? = null


    init {
        init()
        title = "Copy module"
    }


    override fun createCenterPanel(): JComponent? = JPanel(BorderLayout())

    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                    text = "Copy selected module \"${moduleName}\" into...",
                    bold = true
                )
            }

            moduleNamePanel.create(this)

            titledRow("Application module") {
                row {
                    cell {
                        val cellBuilder = comboBox(
                            model = appModuleComboBoxModel,
                            getter = { appModuleComboBoxModel.selected },
                            setter = { /* do nothing */ },
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
                showErrorMessage(project, "Application module not selected!", comboBox)
            }
        }
    }


    fun getModuleName(): String = moduleNamePanel.getModuleName()

    fun getPackageName(): String = moduleNamePanel.getPackageName()

    fun getSelectedModule(): Module = appModuleComboBoxModel.selected!!

}