package ru.hh.android.plugin.actions.modules.copy_module.view

import com.intellij.ide.util.propComponentProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.extensions.EMPTY
import ru.hh.android.plugin.extensions.layout.onTextChange
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class CopyAndroidModuleActionDialog(
        private val project: Project,
        private val moduleName: String
) : DialogWrapper(project, true) {

    private var moduleNameTextField: JTextField? = null
    private var packageNameTextField: JTextField? = null

    init {
        init()
        title = "Copy module"
    }

    override fun createCenterPanel(): JComponent? = JPanel(BorderLayout())

    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                        text = "Copy selected module '$moduleName' into...",
                        bold = true
                )
            }

            row("New module name:") {
                textField(Model::newModuleName).apply {
                    moduleNameTextField = component
                    with(component) {
                        onTextChange { Model.newModuleName = moduleNameTextField?.text ?: String.EMPTY }
                    }
                }
            }

            row("New package name:") {
                textField(Model::newPackageName).apply {
                    packageNameTextField = component
                    with(component) {
                        onTextChange { Model.newPackageName = packageNameTextField?.text ?: String.EMPTY }
                    }
                }
            }
        }
    }


    object Model {
        var newModuleName by propComponentProperty(defaultValue = String.EMPTY)
        var newPackageName by propComponentProperty(defaultValue = String.EMPTY)
    }

}