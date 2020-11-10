package ru.hh.android.plugins.garcon.actions.collect_kakao_views.dialog

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.kakaoScreensClassFilter
import ru.hh.android.plugins.garcon.extensions.layout.createKotlinClassChooserComboBox
import ru.hh.android.plugins.garcon.extensions.showErrorMessage
import ru.hh.android.plugins.garcon.utils.GarconBundle
import ru.hh.android.plugins.garcon.views.KotlinFileComboBoxWrapper
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class CollectKakaoViewsIntoPageObjectDialog(
    private val xmlFile: XmlFile
) : DialogWrapper(xmlFile.project, true) {

    companion object {
        private const val TAG = "CollectKakaoViewsIntoPageObjectDialog"

        private const val KEY_OPEN_IN_EDITOR = "$TAG.OpenInEditor"
        private const val KEY_RECENT_TARGET_CLASS = "$TAG.RecentTargetClass"
    }

    private val project: Project get() = xmlFile.project

    private val eventLogger: GarconEventLogger by lazy {
        xmlFile.project.getComponent(GarconEventLogger::class.java)
    }

    private lateinit var openInEditorCheckBox: JCheckBox
    private lateinit var targetClassChooser: KotlinFileComboBoxWrapper

    private var targetClass: PsiElement? = null


    init {
        init()
        title = GarconBundle.message("garcon.forms.collect_kakao_views_into_page_object.title")
    }


    override fun createCenterPanel(): JComponent? = JPanel(BorderLayout())


    @Suppress("UnstableApiUsage")
    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                    text = GarconBundle.message(
                        "garcon.forms.collect_kakao_views_into_page_object.label.0",
                        xmlFile.name
                    ),
                    bold = true
                )
            }

            row(GarconBundle.message("garcon.common.forms.destination_class")) {
                targetClassChooser = initTargetClassChooser()
                targetClassChooser(CCFlags.growX)
            }

            row {
                openInEditorCheckBox = checkBox(
                    text = GarconBundle.message("garcon.common.forms.open_in_editor"),
                    isSelected = PropertiesComponent.getInstance().getBoolean(KEY_OPEN_IN_EDITOR, true)
                )
                openInEditorCheckBox(CCFlags.pushX)
            }
        }
    }

    override fun doOKAction() {
        if (checkTargetClassIsValid()) {
            saveOpenInEditorFlag(getOpenInEditor())
            super.doOKAction()
        }

        return
    }


    fun getOpenInEditor(): Boolean {
        return openInEditorCheckBox.isSelected
    }

    fun getTargetClass(): PsiElement? {
        return targetClass
    }


    private fun initTargetClassChooser(): KotlinFileComboBoxWrapper {
        return createKotlinClassChooserComboBox(
            project = project,
            chooserDialogTitle = GarconBundle.message("garcon.dialogs.choose_destination_class"),
            initialText = null,
            recentKey = KEY_RECENT_TARGET_CLASS,
            classFilter = kakaoScreensClassFilter(),
            onSelectTargetClassAction = { aClass, needChangeText ->
                if (aClass is KtLightClassForSourceDeclaration) {
                    targetClass = aClass.kotlinOrigin
                    if (needChangeText) {
                        targetClassChooser.text = aClass.qualifiedName ?: String.EMPTY
                    }
                } else {
                    targetClass = aClass
                }
            }
        )
    }

    private fun checkTargetClassIsValid(): Boolean {
        return if (targetClass == null) {
            showTargetClassError(GarconBundle.message("garcon.errors.no_target_class"))
            false
        } else {
            true
        }
    }

    private fun showTargetClassError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(
            project,
            errorMessage,
            targetClassChooser
        )
    }

    private fun saveOpenInEditorFlag(currentValue: Boolean) {
        PropertiesComponent.getInstance().setValue(KEY_OPEN_IN_EDITOR, currentValue.toString())
    }

}