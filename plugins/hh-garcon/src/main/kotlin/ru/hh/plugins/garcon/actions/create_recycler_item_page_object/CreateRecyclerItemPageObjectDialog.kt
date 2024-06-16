package ru.hh.plugins.garcon.actions.create_recycler_item_page_object

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.RecentsManager
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.isValidIdentifier
import ru.hh.plugins.garcon.GarconConstants
import ru.hh.plugins.garcon.extensions.showErrorDialog
import ru.hh.plugins.garcon.services.ClassFiltersFactory
import ru.hh.plugins.layout.KotlinFileComboBoxWrapper
import ru.hh.plugins.psi_utils.xml.extractClassNameFromFileName
import ru.hh.plugins.views.layouts.createKotlinClassChooserComboBox
import javax.swing.JComponent

class CreateRecyclerItemPageObjectDialog(
    private val xmlFile: XmlFile
) : DialogWrapper(xmlFile.project) {

    private val project: Project get() = xmlFile.project

    private lateinit var targetClassChooser: KotlinFileComboBoxWrapper

    private var className = "${xmlFile.extractClassNameFromFileName()}RecyclerItem"
    private var openInEditor: Boolean = false
    private var targetClass: PsiElement? = null

    init {
        init()
        title = "Create <RecyclerItem> Page Object"
    }

    override fun doOKAction() {
        if (isFormValid()) {
            super.doOKAction()
            targetClass?.let { aClass ->
                RecentsManager.getInstance(project).registerRecentEntry(
                    GarconConstants.RecentsKeys.TARGET_SCREEN_CLASS, aClass.kotlinFqName.toString()
                )
            }
            PropertiesComponent.getInstance()
                .setValue(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG, openInEditor.toString())
        }
    }

    @Suppress("detekt.LongMethod")
    override fun createCenterPanel(): JComponent {
        openInEditor = PropertiesComponent.getInstance()
            .getBoolean(GarconConstants.RecentsKeys.OPEN_IN_EDITOR_FLAG, true)
        return panel {
            group(title = "Page Object Class Name") {
                row {
                    textField()
                        .bindText(::className)
                        .comment("Enter page object class name")
                        .columns(COLUMNS_LARGE)
                }
            }
            group("Page Object Class") {
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
                    cell(targetClassChooser)
                        .comment("Choose target &lt;Screen&gt; Page Object class")
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

    fun getDialogResult(): CreateRecyclerItemPageObjectDialogResult {
        return CreateRecyclerItemPageObjectDialogResult(
            xmlFile = xmlFile,
            className = className,
            targetClass = targetClass as KtClass,
            openInEditor = openInEditor
        )
    }

    private fun isFormValid(): Boolean {
        return isClassNameValid() && isTargetClassValid()
    }

    private fun isClassNameValid(): Boolean {
        val className = className

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
