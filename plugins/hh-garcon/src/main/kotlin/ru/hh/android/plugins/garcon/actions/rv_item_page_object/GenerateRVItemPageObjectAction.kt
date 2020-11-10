package ru.hh.android.plugins.garcon.actions.rv_item_page_object

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.executeCommand
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import ru.hh.android.plugins.garcon.actions.rv_item_page_object.dialog.GenerateRVItemPageObjectDialog
import ru.hh.android.plugins.garcon.extensions.*
import ru.hh.android.plugins.garcon.extensions.psi.addImportPackages
import ru.hh.android.plugins.garcon.extensions.psi.openInEditor
import ru.hh.android.plugins.garcon.extensions.psi.reformatWithCodeStyle
import ru.hh.android.plugins.garcon.generator.PageObjectFactory
import ru.hh.android.plugins.garcon.model.page_object.RecyclerViewItemPageObjectInitData
import ru.hh.android.plugins.garcon.utils.GarconBundle


/**
 * Action for generating KRecyclerItem class declaration from XML layout or into existing Screen class.
 */
class GenerateRVItemPageObjectAction : AnAction() {

    companion object {
        private val HARDCODED_IMPORTS_FOR_RV_ITEM_PAGE_OBJECT = listOf(
            "com.agoda.kakao.recycler.KRecyclerItem",
            "org.hamcrest.Matcher",
            "android.view.View"
        )
    }


    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = when {
            e.canReachLayoutXmlFileInsideAndroidModule() -> true
            // TODO - оставил на "подумать", это не очень просто сделать
//            e.canReachKtClassInsideAndroidModuleWithAncestor(Constants.KAKAO_SCREEN_CLASS_FQN) -> true
            else -> false
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        e.getXmlFileFromEditorOrSelection()?.let { handleActionFromXMLFile(it) }
            ?: e.getKtClassFromEditor()?.let { handleActionFromKtClass(it) }
            ?: return
    }


    private fun handleActionFromXMLFile(xmlFile: XmlFile) {
        println("handleActionFromXMLFile[xmlFile: $xmlFile]")

        val dialog = GenerateRVItemPageObjectDialog(
            xmlFile
        ).also { it.show() }
        handleDialogResult(dialog, xmlFile)
    }

    private fun handleActionFromKtClass(ktClass: KtClass) {
        println("handleActionFromKotlinClass[ktClass: $ktClass]")
    }


    private fun handleDialogResult(dialog: GenerateRVItemPageObjectDialog, xmlFile: XmlFile) {
        if (dialog.isOK.not()) {
            xmlFile.project.errorNotification(
                message = GarconBundle.message("garcon.notifications.rv_page_object_generation.cancel")
            )
            return
        }

        val pageObjectFactory = xmlFile.project.getComponent(PageObjectFactory::class.java)
        val pageObjectData = pageObjectFactory.convert(
            RecyclerViewItemPageObjectInitData(
                xmlFile = xmlFile,
                className = dialog.getPageObjectClassName()
            )
        )
        xmlFile.project.logDebug("pageObjectData: $pageObjectData")

        val targetKtClass = dialog.getTargetClass() as? KtClass
        xmlFile.project.logDebug("targetKtClass: $targetKtClass")
        if (targetKtClass == null) {
            return
        }
        val ktPsiFactory = KtPsiFactory(xmlFile.project)

        executeCommand {
            runUndoTransparentWriteAction {
                val pageObjectClass = ktPsiFactory.createClass(pageObjectData.classText)

                val classBody = targetKtClass.getOrCreateBody()
                classBody.addBefore(pageObjectClass, classBody.rBrace)
                val changedClass = classBody.addBefore(ktPsiFactory.createNewLine(), classBody.rBrace)

                targetKtClass.containingKtFile.addImportPackages(
                    *pageObjectData.properties.map { it.kakaoViewDeclaration.fqn }.toTypedArray(),
                    *HARDCODED_IMPORTS_FOR_RV_ITEM_PAGE_OBJECT.toTypedArray()
                )

                changedClass.containingFile.reformatWithCodeStyle()
                PsiDocumentManager.getInstance(xmlFile.project).apply {
                    val document = getDocument(changedClass.containingFile)
                    document?.let { doPostponedOperationsAndUnblockDocument(it) }
                }

                if (dialog.getOpenInEditor()) {
                    xmlFile.project.logDebug("force open in editor")
                    changedClass.openInEditor()
                }
                xmlFile.project.infoNotification(
                    message = GarconBundle.message("garcon.notifications.rv_page_object_generation.success")
                )
            }
        }
    }

}