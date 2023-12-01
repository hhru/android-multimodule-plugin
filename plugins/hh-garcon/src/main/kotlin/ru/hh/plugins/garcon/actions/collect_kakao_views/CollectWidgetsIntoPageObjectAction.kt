package ru.hh.plugins.garcon.actions.collect_kakao_views

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType
import ru.hh.plugins.actions.XmlLayoutCodeInsightAction
import ru.hh.plugins.extensions.openapi.executeWithoutCodeStyle
import ru.hh.plugins.garcon.extensions.psi.collectAndroidViewsTagsInfo
import ru.hh.plugins.garcon.services.PageObjectPropertyConverter
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
import ru.hh.plugins.psi_utils.openInEditor

class CollectWidgetsIntoPageObjectAction : XmlLayoutCodeInsightAction() {

    companion object {
        private const val COMMAND_NAME = "CollectWidgetsIntoPageObjectActionCommand"

        private const val PRIVATE_MODIFIER_TOKEN = "private"
    }

    override fun handleAction(project: Project, editor: Editor, psiFile: PsiFile) {
        val dialog = CollectWidgetsIntoPageObjectDialog(psiFile as XmlFile).also { it.show() }
        if (dialog.isOK) {
            handleDialogParams(dialog.getDialogResult())
        } else {
            HHNotifications.error(message = "Collect widgets into Page object dialog dismissed")
        }
    }

    private fun handleDialogParams(params: CollectWidgetsIntoPageObjectDialogResult) {
        val project = params.xmlFile.project
        val collectedTags = params.xmlFile.collectAndroidViewsTagsInfo()

        val propertyConverter = PageObjectPropertyConverter.getInstance(project)
        val propertiesDeclarations = collectedTags.map { tagInfo ->
            propertyConverter.convert(tagInfo)
        }

        val ktPsiFactory = KtPsiFactory(project)
        val ktProperties = propertiesDeclarations.map { ktPsiFactory.createProperty(it) }

        project.executeWriteCommand(COMMAND_NAME) {
            val classBody = params.targetClass.getOrCreateBody()

            project.executeWithoutCodeStyle {
                ktProperties.forEach { property ->
                    val anchor = classBody.findLastAcceptableAnchorProperty()
                    classBody.addAfter(property, anchor)
                }
            }

            params.targetClass.containingKtFile.shortReferencesAndReformatWithCodeStyle()
            if (params.openInEditor) {
                params.targetClass.containingKtFile.openInEditor()
            }
            HHNotifications.info(
                message = "Collecting Kakao widgets for '${params.targetClass.name}' successfully finished"
            )
        }
    }

    private fun KtClassBody.findLastAcceptableAnchorProperty(): KtProperty? {
        return properties.lastOrNull { it.visibilityModifierType()?.value == PRIVATE_MODIFIER_TOKEN }
            ?: properties.lastOrNull()
    }
}
