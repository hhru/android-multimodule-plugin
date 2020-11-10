package ru.hh.android.plugins.garcon.actions.screen_page_object

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.executeCommand
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.KotlinFileType
import ru.hh.android.plugins.garcon.actions.screen_page_object.dialog.GeneratePageObjectDialog
import ru.hh.android.plugins.garcon.config.PluginConfig
import ru.hh.android.plugins.garcon.extensions.*
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.packageToPsiDirectory
import ru.hh.android.plugins.garcon.extensions.psi.openInEditor
import ru.hh.android.plugins.garcon.extensions.psi.reformatWithCodeStyle
import ru.hh.android.plugins.garcon.generator.PageObjectFactory
import ru.hh.android.plugins.garcon.model.page_object.ScreenPageObjectInitData
import ru.hh.android.plugins.garcon.utils.GarconBundle


/**
 * Action for generating Kakao's Screen class declaration from XML layout file.
 */
class GeneratePageObjectAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = when {
            e.canReachLayoutXmlFileInsideAndroidModule() -> true
            else -> false
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        e.getXmlFileFromEditorOrSelection()
            ?.let { handleActionFromXmlFile(it) }
            ?: return
    }


    private fun handleActionFromXmlFile(xmlFile: XmlFile) {
        val project = xmlFile.project

        val defaultTargetPackageName = PluginConfig.getInstance(project).defaultTargetPackageName
        val defaultTargetFolderPath = PluginConfig.getInstance(project).defaultTargetFolderPath
        val targetPsiDirectory = defaultTargetPackageName
            .packageToPsiDirectory(project = project, withPath = defaultTargetFolderPath)

        val dialog = GeneratePageObjectDialog(
            xmlFile = xmlFile,
            defaultTargetDirectory = targetPsiDirectory
        ).also { it.show() }

        handleDialogResult(dialog, xmlFile)
    }

    private fun handleDialogResult(dialog: GeneratePageObjectDialog, xmlFile: XmlFile) {
        if (dialog.isOK.not()) {
            xmlFile.project.errorNotification(
                message = GarconBundle.message("garcon.notifications.page_object_generation.cancel")
            )
            return
        }

        val pageObjectClassText = getPageObjectText(xmlFile, dialog)
        createPageObjectFile(dialog, pageObjectClassText, xmlFile)
    }

    private fun getPageObjectText(xmlFile: XmlFile, dialog: GeneratePageObjectDialog): String {
        val pageObjectFactory = xmlFile.project.getComponent(PageObjectFactory::class.java)

        return pageObjectFactory.convert(
            ScreenPageObjectInitData(
                xmlFile = xmlFile,
                className = dialog.getScreenPageObjectClassName(),
                packageName = dialog.getTargetDirectory()?.targetPackageName ?: String.EMPTY
            )
        ).run {
            xmlFile.project.logDebug("pageObjectData: $this")
            classText
        }
    }

    private fun createPageObjectFile(
        dialog: GeneratePageObjectDialog,
        pageObjectClassText: String,
        xmlFile: XmlFile
    ) {
        val psiFileFactory = PsiFileFactory.getInstance(xmlFile.project)

        executeCommand {
            runUndoTransparentWriteAction {
                val fileName = "${dialog.getScreenPageObjectClassName()}.kt"
                val pageObjectPsiFile = psiFileFactory.createFileFromText(
                    fileName,
                    KotlinFileType.INSTANCE,
                    pageObjectClassText
                )
                xmlFile.project.logDebug("Future file name: $fileName")
                val psiDirectory = dialog.getTargetDirectory()?.targetPackage?.directories?.lastOrNull()
                xmlFile.project.logDebug("psiDirectory: $psiDirectory")

                psiDirectory?.let { directory ->
                    val createdFile = directory.add(pageObjectPsiFile)
                    createdFile.containingFile.reformatWithCodeStyle()

                    if (dialog.getOpenInEditorFlag()) {
                        xmlFile.project.logDebug("Force open in editor")
                        createdFile.openInEditor()
                    }
                }

                xmlFile.project.infoNotification(
                    message = GarconBundle.message("garcon.notifications.page_object_generation.success")
                )
            }
        }
    }

}