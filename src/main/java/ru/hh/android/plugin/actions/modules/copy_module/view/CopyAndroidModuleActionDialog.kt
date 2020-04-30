package ru.hh.android.plugin.actions.modules.copy_module.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.core.framework_ui.view.ModuleNamePanel
import ru.hh.android.plugin.utils.PluginBundle.message
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


class CopyAndroidModuleActionDialog(
        private val project: Project,
        private val moduleName: String
) : DialogWrapper(project, true) {

    private val moduleNamePanel = ModuleNamePanel(
            moduleNameSectionLabel = message("geminio.common.forms.new_module_name"),
            packageNameSectionLabel = message("geminio.common.forms.new_module_package_name"),
            onErrorAction = { hasError ->
                this.isOKActionEnabled = hasError.not()
            }
    )


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
        }
    }


    fun getModuleName(): String = moduleNamePanel.getModuleName()

    fun getPackageName(): String = moduleNamePanel.getPackageName()

}