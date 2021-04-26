package ru.hh.plugins.garcon.actions.create_recycler_item_page_object

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import ru.hh.plugins.garcon.extensions.psi.collectAndroidViewsTagsInfo
import ru.hh.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.plugins.garcon.services.FreeMarkerWrapper
import ru.hh.plugins.garcon.services.PageObjectPropertyConverter
import ru.hh.plugins.garcon.services.balloonError
import ru.hh.plugins.garcon.services.balloonInfo
import ru.hh.plugins.actions.XmlLayoutCodeInsightAction
import ru.hh.plugins.extensions.openapi.executeWithoutCodeStyle
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
import ru.hh.plugins.psi_utils.openInEditor


class CreateRecyclerItemPageObjectAction : XmlLayoutCodeInsightAction() {

    companion object {
        private const val COMMAND_NAME = "CreateRecyclerItemPageObjectActionCommand"
    }

    private val logger by lazy { Logger.getInstance(CreateRecyclerItemPageObjectAction::class.java) }


    override fun handleAction(project: Project, editor: Editor, psiFile: PsiFile) {
        val dialog = CreateRecyclerItemPageObjectDialog(psiFile as XmlFile).also { it.show() }
        if (dialog.isOK) {
            handleDialogResult(dialog.getDialogResult())
        } else {
            project.balloonError(message = "RecyclerItem Page object dialog dismissed")
        }
    }


    private fun handleDialogResult(result: CreateRecyclerItemPageObjectDialogResult) {
        val project = result.xmlFile.project
        val collectedTags = result.xmlFile.collectAndroidViewsTagsInfo()

        val propertyConverter = PageObjectPropertyConverter.getInstance(project)
        val properties = collectedTags.map { tagInfo ->
            propertyConverter.convert(tagInfo)
        }

        val config = GarconPluginSettings.getConfig(project)
        val freeMarkerWrapper = FreeMarkerWrapper.getInstance(project)

        val pageObjectClassText = freeMarkerWrapper.resolveTemplate(
            templateName = config.templatesPaths.rvItemPageObjectTemplatePath,
            params = mapOf(
                "class_name" to result.className,
                "properties_declarations_list" to properties
            )
        )

        val ktPsiFactory = KtPsiFactory(project)
        val psiClass = ktPsiFactory.createClass(pageObjectClassText)

        logActionData(
            params = result,
            tags = collectedTags,
            properties = properties,
            pageObjectText = pageObjectClassText
        )

        project.executeWriteCommand(COMMAND_NAME) {
            val targetClassBody = result.targetClass.getOrCreateBody()

            project.executeWithoutCodeStyle {
                targetClassBody.addBefore(psiClass, targetClassBody.rBrace)
            }

            result.targetClass.containingKtFile.shortReferencesAndReformatWithCodeStyle()
            if (result.openInEditor) {
                result.targetClass.containingKtFile.openInEditor()
            }
            project.balloonInfo(message = "RecyclerItem Page object '${result.className}' successfully created")
        }
    }


    private fun logActionData(
        params: CreateRecyclerItemPageObjectDialogResult,
        tags: List<AndroidViewTagInfo>,
        properties: List<String>,
        pageObjectText: String
    ) {
        logger.debug(
            """
        Create new screen page object
        ===
        dialog params: $params
        collected tags:
        --
        ${tags.joinToString(separator = "\n")}
        --
        collected properties:
        --
        ${properties.joinToString(separator = "\n")}
        --
        future page object text: 
        --
        $pageObjectText
        --
        """
        )
    }

}