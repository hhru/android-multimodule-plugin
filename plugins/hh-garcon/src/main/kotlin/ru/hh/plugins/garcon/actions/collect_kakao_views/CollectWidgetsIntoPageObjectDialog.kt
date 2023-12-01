package ru.hh.plugins.garcon.actions.collect_kakao_views

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.RecentsManager
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.garcon.GarconConstants
import ru.hh.plugins.garcon.extensions.showErrorDialog
import ru.hh.plugins.garcon.services.ClassFiltersFactory
import ru.hh.plugins.layout.KotlinFileComboBoxWrapper
import ru.hh.plugins.views.layouts.createKotlinClassChooserComboBox
import javax.swing.JCheckBox
import javax.swing.JComponent

class CollectWidgetsIntoPageObjectDialog(
    private val xmlFile: XmlFile
) : DialogWrapper(xmlFile.project) {

    private val project: Project get() = xmlFile.project

    private lateinit var openInEditorCheckBox: JCheckBox
    private lateinit var targetClassChooser: KotlinFileComboBoxWrapper

    private var targetClass: PsiElement? = null

    init {
        init()
        title = "Collect Widgets into Page Object"
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            titledRow("Choose target <Screen> Page Object class") {
                row {
                    targetClassChooser = createKotlinClassChooserComboBox(
                        project = project,
                        chooserDialogTitle = "Choose target <Screen> Page Object class",
                        recentKey = GarconConstants.RecentsKeys.TARGET_SCREEN_CLASS,
                        initialText = null,
                        classFilter = ClassFiltersFactory.getInstance(project).createKakaoScreensClassFilter(),
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
                    targetClassChooser()
                }
            }
            row {
                openInEditorCheckBox = checkBox(
                    text = "Open in editor",
                    isSelected = PropertiesComponent.getInstance()
                        .getBoolean(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG, true)
                ).component
                openInEditorCheckBox(CCFlags.pushX)
            }
        }
    }

    override fun doOKAction() {
        if (isFormValid()) {
            targetClass?.let { aClass ->
                RecentsManager.getInstance(project).registerRecentEntry(
                    GarconConstants.RecentsKeys.TARGET_SCREEN_CLASS,
                    aClass.getKotlinFqName().toString()
                )
            }
            PropertiesComponent.getInstance()
                .setValue(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG, getOpenInEditor().toString())
            super.doOKAction()
        }
    }

    fun getDialogResult(): CollectWidgetsIntoPageObjectDialogResult {
        return CollectWidgetsIntoPageObjectDialogResult(
            xmlFile = xmlFile,
            targetClass = getTargetPsiElement() as KtClass,
            openInEditor = getOpenInEditor()
        )
    }

    private fun getOpenInEditor(): Boolean {
        return openInEditorCheckBox.isSelected
    }

    private fun getTargetPsiElement(): PsiElement? {
        return targetClass
    }

    private fun isFormValid(): Boolean {
        return isTargetClassValid()
    }

    private fun isTargetClassValid(): Boolean {
        return if (targetClass == null) {
            project.showErrorDialog("No target class specified")
            false
        } else {
            true
        }
    }
}
