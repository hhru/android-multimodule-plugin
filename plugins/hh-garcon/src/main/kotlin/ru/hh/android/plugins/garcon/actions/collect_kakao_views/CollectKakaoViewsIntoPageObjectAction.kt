package ru.hh.android.plugins.garcon.actions.collect_kakao_views

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType
import ru.hh.android.plugins.garcon.actions.collect_kakao_views.dialog.CollectKakaoViewsIntoPageObjectDialog
import ru.hh.android.plugins.garcon.extensions.canReachLayoutXmlFileInsideAndroidModule
import ru.hh.android.plugins.garcon.extensions.errorNotification
import ru.hh.android.plugins.garcon.extensions.getXmlFileFromEditorOrSelection
import ru.hh.android.plugins.garcon.extensions.psi.addImportPackages
import ru.hh.android.plugins.garcon.extensions.psi.openInEditor
import ru.hh.android.plugins.garcon.extensions.psi.reformatWithCodeStyle
import ru.hh.android.plugins.garcon.model.mapping.PageObjectPropertyConverter
import ru.hh.android.plugins.garcon.model.mapping.XmlFileConverter
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty
import ru.hh.android.plugins.garcon.utils.GarconBundle


class CollectKakaoViewsIntoPageObjectAction : AnAction() {

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
        val dialog = CollectKakaoViewsIntoPageObjectDialog(
            xmlFile = xmlFile
        ).also { it.show() }

        handleDialogResult(dialog, xmlFile)
    }

    private fun handleDialogResult(dialog: CollectKakaoViewsIntoPageObjectDialog, xmlFile: XmlFile) {
        if (dialog.isOK.not()) {
            xmlFile.project.errorNotification(
                message = GarconBundle.message("garcon.notifications.collect_kakao_views_into_page_object.cancel")
            )
            return
        }

        val properties = getKakaoViewsProperties(xmlFile)
        val propertiesDeclarations = getPropertiesDeclarations(xmlFile.project, properties)
        val imports = properties.map { it.kakaoViewDeclaration.fqn }.toSet()

        handleTargetClass(
            dialog = dialog,
            propertiesDeclarations = propertiesDeclarations,
            imports = imports
        )
    }

    private fun handleTargetClass(
        dialog: CollectKakaoViewsIntoPageObjectDialog,
        propertiesDeclarations: List<KtProperty>,
        imports: Set<String>
    ) {
        executeCommand {
            runUndoTransparentWriteAction {
                val targetClass = dialog.getTargetClass() as? KtClass ?: return@runUndoTransparentWriteAction

                val body = targetClass.getOrCreateBody()
                propertiesDeclarations.forEach { property ->
                    body.addAfter(property,
                        body.properties.lastOrNull { it.visibilityModifierType()?.value == "private" }
                            ?: body.properties.lastOrNull())
                }

                targetClass.containingKtFile.addImportPackages(
                    *imports.toTypedArray()
                )

                targetClass.reformatWithCodeStyle()

                if (dialog.getOpenInEditor()) {
                    targetClass.openInEditor()
                }
            }
        }
    }

    private fun getKakaoViewsProperties(xmlFile: XmlFile): List<PageObjectProperty> {
        return xmlFile.project.getComponent(XmlFileConverter::class.java).convert(xmlFile)
    }

    private fun getPropertiesDeclarations(project: Project, properties: List<PageObjectProperty>): List<KtProperty> {
        val ktPsiFactory = KtPsiFactory(project)
        val pageObjectPropertyConverter = project.getComponent(PageObjectPropertyConverter::class.java)

        val propertyDeclarationsTexts = properties.map { pageObjectPropertyConverter.convert(it) }

        return propertyDeclarationsTexts.map { ktPsiFactory.createProperty(it) }
    }

}