package ru.hh.plugins.garcon.actions.create_recycler_item_page_object

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
import ru.hh.plugins.extensions.isValidIdentifier
import ru.hh.plugins.garcon.GarconConstants
import ru.hh.plugins.garcon.extensions.showErrorDialog
import ru.hh.plugins.garcon.services.ClassFiltersFactory
import ru.hh.plugins.layout.KotlinFileComboBoxWrapper
import ru.hh.plugins.psi_utils.xml.extractClassNameFromFileName
import ru.hh.plugins.views.layouts.createKotlinClassChooserComboBox
import javax.swing.JCheckBox
import javax.swing.JComponent

class CreateRecyclerItemPageObjectDialog(
    private val xmlFile: XmlFile
) : DialogWrapper(xmlFile.project) {

    private val project: Project get() = xmlFile.project

    private lateinit var openInEditorCheckBox: JCheckBox
    private lateinit var targetClassChooser: KotlinFileComboBoxWrapper

    @Suppress("MemberVisibilityCanBePrivate")
    var className = "${xmlFile.extractClassNameFromFileName()}RecyclerItem"
    private var targetClass: PsiElement? = null

    init {
        init()
        title = "Create <RecyclerItem> Page Object"
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

    override fun createCenterPanel(): JComponent {
        return panel {
            titledRow(title = "Enter page object class name:") {
                row {
                    textField(this@CreateRecyclerItemPageObjectDialog::className)
                }
            }
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

    fun getDialogResult(): CreateRecyclerItemPageObjectDialogResult {
        return CreateRecyclerItemPageObjectDialogResult(
            xmlFile = xmlFile,
            className = getCurrentClassName(),
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

    private fun getCurrentClassName(): String {
        return className
    }

    private fun isFormValid(): Boolean {
        return isClassNameValid() && isTargetClassValid()
    }

    private fun isClassNameValid(): Boolean {
        val className = getCurrentClassName()

        return when {
            className.isEmpty() -> {
                project.showErrorDialog("Class name is blank")
                false
            }

            className.isValidIdentifier(project).not() -> {
                project.showErrorDialog("''$className'' is not a legal java identifier")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun isTargetClassValid(): Boolean {
        return when (targetClass) {
            null -> {
                project.showErrorDialog("No target class specified")
                false
            }
            else -> {
                true
            }
        }
    }
}
