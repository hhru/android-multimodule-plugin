package ru.hh.android.plugins.garcon.actions.screen_page_object.dialog


import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JavaProjectRootsUtil
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.refactoring.MoveDestination
import com.intellij.refactoring.PackageWrapper
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox
import com.intellij.refactoring.util.RefactoringMessageUtil
import com.intellij.ui.EditorTextField
import com.intellij.ui.RecentsManager
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import com.intellij.util.IncorrectOperationException
import ru.hh.android.plugins.garcon.Constants
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.isQualifiedPackageName
import ru.hh.android.plugins.garcon.extensions.base_types.isValidIdentifier
import ru.hh.android.plugins.garcon.extensions.base_types.layoutFileNameToClassName
import ru.hh.android.plugins.garcon.extensions.layout.editorTextField
import ru.hh.android.plugins.garcon.extensions.layout.targetFolderComboBox
import ru.hh.android.plugins.garcon.extensions.layout.targetPackageComboBox
import ru.hh.android.plugins.garcon.extensions.psi.qualifiedPackageName
import ru.hh.android.plugins.garcon.extensions.showErrorMessage
import ru.hh.android.plugins.garcon.utils.GarconBundle
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class GeneratePageObjectDialog(
    private val xmlFile: XmlFile,
    private val defaultTargetDirectory: PsiDirectory?
) : DialogWrapper(xmlFile.project, true) {

    companion object {
        private const val TAG = "GeneratePageObjectDialog"

        private const val KEY_OPEN_IN_EDITOR = "$TAG.OpenInEditor"
    }


    private val project: Project
        get() = xmlFile.project

    private val eventLogger: GarconEventLogger by lazy {
        project.getComponent(GarconEventLogger::class.java)
    }


    private lateinit var screenPageObjectClassNameTextField: EditorTextField
    private lateinit var myOpenInEditorCb: JCheckBox
    private lateinit var myTfPackage: ReferenceEditorComboWithBrowseButton
    private lateinit var targetDestinationFolderComboBox: DestinationFolderComboBox


    init {
        init()
        title = GarconBundle.message("garcon.forms.generate_page_object.title")
    }


    override fun createCenterPanel(): JComponent? = JPanel(BorderLayout())

    override fun getPreferredFocusedComponent(): JComponent? = screenPageObjectClassNameTextField

    @Suppress("UnstableApiUsage")
    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                    text = GarconBundle.message("garcon.forms.generate_page_object.label.0", xmlFile.name),
                    bold = true
                )
            }

            row(GarconBundle.message("garcon.forms.generate_page_object.class_name")) {
                screenPageObjectClassNameTextField = editorTextField(
                    text = xmlFile.name.layoutFileNameToClassName(),
                    selectAll = true
                )
                screenPageObjectClassNameTextField(CCFlags.growX)
            }

            row(GarconBundle.message("garcon.forms.generate_page_object.destination_package")) {
                myTfPackage = targetPackageComboBox(
                    project = project,
                    recentPackageKey = Constants.SCREEN_PAGE_OBJECT_TARGET_PACKAGE_RECENT_KEY,
                    initialText = defaultTargetDirectory?.qualifiedPackageName ?: String.EMPTY,
                    labelText = GarconBundle.message("garcon.dialogs.choose_destination_package")
                )
                myTfPackage(CCFlags.growX)
            }

            targetDestinationFolderComboBox = targetFolderComboBox(
                project = project,
                targetPackageComboBox = myTfPackage,
                initialPsiDirectory = defaultTargetDirectory,
                onError = { message, component -> setErrorText(message, component) }
            )

            val isMultipleSourceRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(project).size > 1
            if (isMultipleSourceRoots) {
                row(GarconBundle.message("garcon.forms.generate_page_object.target_destination_directory")) {
                    targetDestinationFolderComboBox(CCFlags.growX)
                }
            }

            row {
                myOpenInEditorCb = checkBox(
                    text = GarconBundle.message("garcon.common.forms.open_in_editor"),
                    isSelected = PropertiesComponent.getInstance().getBoolean(KEY_OPEN_IN_EDITOR, true)
                )
                myOpenInEditorCb(CCFlags.pushX)
            }
        }
    }

    override fun doOKAction() {
        if (checkPackageNameIsValid() && checkClassNameIsValid() && checkTargetFolderIsValid()) {
            RecentsManager.getInstance(project)
                .registerRecentEntry(Constants.SCREEN_PAGE_OBJECT_TARGET_PACKAGE_RECENT_KEY, getTargetPackageName())
            saveOpenInEditorFlag(getOpenInEditorFlag())

            super.doOKAction()
        }
    }


    fun getTargetDirectory(): MoveDestination? {
        return fetchTargetMoveDestination()
    }

    fun getScreenPageObjectClassName(): String {
        return screenPageObjectClassNameTextField.text
    }

    fun getOpenInEditorFlag(): Boolean {
        return myOpenInEditorCb.isSelected
    }


    private fun getTargetPackageName(): String {
        return myTfPackage.text
    }

    private fun checkPackageNameIsValid(): Boolean {
        val packageName = getTargetPackageName()

        return when {
            packageName.isNotEmpty() && !packageName.isQualifiedPackageName(project) -> {
                showPackageNameError(GarconBundle.message("garcon.errors.invalid_package"))
                false
            }

            else -> {
                true
            }
        }
    }

    private fun checkClassNameIsValid(): Boolean {
        val className = getScreenPageObjectClassName()

        return when {
            className.isEmpty() -> {
                showClassNameError(GarconBundle.message("garcon.errors.no_class_name"))
                false
            }

            className.isValidIdentifier(project).not() -> {
                showClassNameError(RefactoringMessageUtil.getIncorrectIdentifierMessage(className))
                false
            }

            else -> {
                true
            }
        }
    }

    private fun checkTargetFolderIsValid(): Boolean {
        return try {
            if (fetchTargetMoveDestination() == null) {
                showTargetFolderError(GarconBundle.message("garcon.errors.invalid_target_folder"))
                false
            } else {
                true
            }
        } catch (e: IncorrectOperationException) {
            val errorMessage = e.message ?: GarconBundle.message("garcon.errors.invalid_target_folder")
            showTargetFolderError(errorMessage)
            false
        }
    }

    private fun fetchTargetMoveDestination(): MoveDestination? {
        val psiManager = PsiManager.getInstance(project)
        val targetPackageWrapper = PackageWrapper(psiManager, getTargetPackageName())

        return targetDestinationFolderComboBox.selectDirectory(targetPackageWrapper, false)
    }

    private fun showClassNameError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(
            project,
            errorMessage,
            screenPageObjectClassNameTextField
        )
    }

    private fun showPackageNameError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(project, errorMessage, myTfPackage)
    }

    private fun showTargetFolderError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(
            project,
            errorMessage,
            targetDestinationFolderComboBox
        )
    }

    private fun saveOpenInEditorFlag(currentValue: Boolean) {
        PropertiesComponent.getInstance().setValue(KEY_OPEN_IN_EDITOR, currentValue.toString())
    }

}