package ru.hh.plugins.geminio.wizard

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import javax.swing.JDialog
import javax.swing.JProgressBar

/**
 * Lightweight modeless loader shown while the legacy template execution blocks the EDT.
 *
 * The execution path still relies on Android Studio template runtime internals, so for now we
 * prefer a visible loading state over pretending the action is fully asynchronous.
 */
internal class GeminioLoadingDialog(
    project: Project,
    title: String,
    description: String,
) : JDialog(
    WindowManager.getInstance().getFrame(project),
    title,
    ModalityType.MODELESS,
) {

    private companion object {
        const val DIALOG_PADDING = 16
    }

    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        isResizable = false

        contentPane = panel {
            row {
                label(description)
            }
            row {
                cell(JProgressBar().apply {
                    isIndeterminate = true
                })
                    .align(Align.FILL)
                    .resizableColumn()
            }
        }.apply {
            border = JBUI.Borders.empty(DIALOG_PADDING)
        }

        pack()
        setLocationRelativeTo(owner)
    }
}
