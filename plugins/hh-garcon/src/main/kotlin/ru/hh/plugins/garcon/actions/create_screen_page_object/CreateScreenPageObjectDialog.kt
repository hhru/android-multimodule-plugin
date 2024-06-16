package ru.hh.plugins.garcon.actions.create_screen_page_object

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.refactoring.MoveDestination
import com.intellij.refactoring.PackageWrapper
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.kotlin.idea.refactoring.ui.KotlinDestinationFolderComboBox
import ru.hh.plugins.extensions.isQualifiedPackageName
import ru.hh.plugins.extensions.isValidIdentifier
import ru.hh.plugins.extensions.packageName
import ru.hh.plugins.extensions.toKotlinFileName
import ru.hh.plugins.garcon.GarconConstants
import ru.hh.plugins.garcon.extensions.showErrorDialog
import ru.hh.plugins.psi_utils.checkFileCanBeCreated
import ru.hh.plugins.psi_utils.xml.extractClassNameFromFileName
import ru.hh.plugins.utils.recents_manager.RecentsUtils
import ru.hh.plugins.views.layouts.kotlinDestinationFolderComboBox
import ru.hh.plugins.views.layouts.targetPackageComboBox
import javax.swing.JComponent

class CreateScreenPageObjectDialog(
    private val xmlFile: XmlFile,
) : DialogWrapper(xmlFile.project) {

    private val project: Project get() = xmlFile.project

    private var className = xmlFile.extractClassNameFromFileName()
    private var openInEditor: Boolean = false

    private lateinit var packageNameChooserComboBox: PackageNameReferenceEditorCombo
    private lateinit var destinationFolderComboBox: KotlinDestinationFolderComboBox

    init {
        init()
        title = "Create <Screen> Page Object"
    }

    override fun createCenterPanel(): JComponent {
        openInEditor = RecentsUtils.getBooleanFromProperties(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG)
        return panel {
            group(title = "Page Object Class Name") {
                row {
                    textField()
                        .bindText(::className)
                        .comment("Enter &lt;Screen&gt; page object class name")
                        .columns(COLUMNS_LARGE)
                }
            }
            group("Destination Package") {
                row {
                    label("Choose destination package name")
                }
                row {
                    val initialPackageName = xmlFile.androidFacet?.packageName
                        ?: GarconConstants.DEFAULT_PACKAGE_NAME

                    packageNameChooserComboBox = targetPackageComboBox(
                        project = project,
                        initialText = initialPackageName,
                        recentPackageKey = GarconConstants.RecentsKeys.TARGET_PACKAGE_NAME,
                        labelText = "Choose package name"
                    )
                    cell(packageNameChooserComboBox)
                        .resizableColumn()
                        .align(Align.FILL)
                }.bottomGap(BottomGap.MEDIUM)

                row {
                    val initialPsiDirectory = xmlFile.containingDirectory

                    destinationFolderComboBox = kotlinDestinationFolderComboBox(
                        project = project,
                        initialPsiDirectory = initialPsiDirectory,
                        packageNameChooserComboBox = packageNameChooserComboBox
                    )
                    cell(destinationFolderComboBox)
                        .resizableColumn()
                        .align(Align.FILL)
                }
            }
            row {
                checkBox("Open in editor")
                    .bindSelected(::openInEditor)
            }
        }
    }

    override fun doOKAction() {
        if (isFormValid()) {
            super.doOKAction()
            saveRecentsValues()
        }
    }

    fun getDialogResult(): CreateScreenPageObjectDialogResult {
        return CreateScreenPageObjectDialogResult(
            xmlFile = xmlFile,
            className = className,
            packageName = getTargetPackageName(),
            targetMoveDestination = requireNotNull(getTargetMoveDestination()),
            openInEditor = openInEditor
        )
    }

    private fun getTargetPackageName(): String {
        return packageNameChooserComboBox.text.trim()
    }

    private fun getTargetMoveDestination(): MoveDestination? {
        val psiManager = PsiManager.getInstance(project)
        val targetPackageWrapper = PackageWrapper(psiManager, getTargetPackageName())

        return destinationFolderComboBox.selectDirectory(targetPackageWrapper, false)
    }

    private fun isFormValid(): Boolean {
        return isClassNameValid() && isPackageNameValid() && isTargetDirectoryValid() && checkFileCanBeCreated()
    }

    private fun isClassNameValid(): Boolean {
        val currentClassName = className

        return when {
            currentClassName.isBlank() -> {
                project.showErrorDialog("Class name is blank")
                false
            }

            currentClassName.isValidIdentifier(project).not() -> {
                project.showErrorDialog("''$currentClassName'' is not a legal java identifier")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun isPackageNameValid(): Boolean {
        val packageName = getTargetPackageName()

        return when {
            packageName.isBlank() -> {
                project.showErrorDialog("Package name is blank")
                false
            }

            packageName.isNotEmpty() && !packageName.isQualifiedPackageName(project) -> {
                project.showErrorDialog("Invalid target package name specified")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun isTargetDirectoryValid(): Boolean {
        return when (getTargetMoveDestination()) {
            null -> {
                project.showErrorDialog("Wrong selection of destination folder")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun checkFileCanBeCreated(): Boolean {
        val targetPsiDirectory = getTargetMoveDestination()?.targetPackage?.directories?.lastOrNull()
        return when (targetPsiDirectory?.checkFileCanBeCreated(className.toKotlinFileName()) == true) {
            false -> {
                project.showErrorDialog(message = "Class with such name already exists")
                false
            }

            else -> {
                true
            }
        }
    }

    private fun saveRecentsValues() {
        RecentsUtils.putRecentsEntry(
            project = project,
            key = GarconConstants.RecentsKeys.TARGET_PACKAGE_NAME,
            value = getTargetPackageName()
        )
        saveOpenInEditorFlag(openInEditor)
    }

    private fun saveOpenInEditorFlag(isOpenInEditor: Boolean) {
        RecentsUtils.putProperty(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG, isOpenInEditor.toString())
    }
}
