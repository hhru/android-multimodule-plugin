package ru.hh.plugins.garcon.actions.create_screen_page_object

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.hh.plugins.actions.XmlLayoutCodeInsightAction
import ru.hh.plugins.extensions.toKotlinFileName
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import ru.hh.plugins.garcon.extensions.psi.collectAndroidViewsTagsInfo
import ru.hh.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.plugins.garcon.services.FreeMarkerWrapper
import ru.hh.plugins.garcon.services.PageObjectPropertyConverter
import ru.hh.plugins.garcon.services.balloonError
import ru.hh.plugins.garcon.services.balloonInfo
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
import ru.hh.plugins.psi_utils.openInEditor

/**
 * Action for generating Kakao's Screen class declaration from XML layout file.
 */
class CreateScreenPageObjectAction : XmlLayoutCodeInsightAction() {

    companion object {
        private const val COMMAND_NAME = "CreateScreenPageObjectActionCommand"
    }

    private val logger by lazy { Logger.getInstance(CreateScreenPageObjectAction::class.java) }

    override fun handleAction(project: Project, editor: Editor, psiFile: PsiFile) {
        val dialog = CreateScreenPageObjectDialog(psiFile as XmlFile).also { it.show() }

        if (dialog.isOK) {
            handleDialogResult(dialog.getDialogResult())
        } else {
            project.balloonError(message = "Screen Page object dialog dismissed")
        }
    }

    private fun handleDialogResult(result: CreateScreenPageObjectDialogResult) {
        val project = result.xmlFile.project
        val collectedTags = result.xmlFile.collectAndroidViewsTagsInfo()

        val propertyConverter = PageObjectPropertyConverter.getInstance(project)
        val properties = collectedTags.map { tagInfo ->
            propertyConverter.convert(tagInfo)
        }

        val freeMarkerWrapper = FreeMarkerWrapper.getInstance(project)

        val config = GarconPluginSettings.getConfig(project)
        val pageObjectClassText = freeMarkerWrapper.resolveTemplate(
            templateName = config.templatesPaths.screenPageObjectTemplatePath,
            params = mapOf(
                "package_name" to result.packageName,
                "class_name" to result.className,
                "properties_declarations_list" to properties
            )
        )

        val ktPsiFactory = KtPsiFactory(project)
        val psiFile = ktPsiFactory.createFile(result.className.toKotlinFileName(), pageObjectClassText)

        val targetPsiDirectory = result.targetMoveDestination.targetPackage.directories.lastOrNull()

        logActionData(
            result = result,
            tags = collectedTags,
            properties = properties,
            pageObjectText = pageObjectClassText,
            targetPsiDirectory = targetPsiDirectory
        )

        if (targetPsiDirectory == null) {
            return
        }

        project.executeWriteCommand(COMMAND_NAME) {
            val createdFile = targetPsiDirectory.add(psiFile) as KtFile
            createdFile.shortReferencesAndReformatWithCodeStyle()
            if (result.openInEditor) {
                createdFile.openInEditor()
            }
            project.balloonInfo(message = "Screen Page object '${result.className}' successfully created")
        }
    }

    private fun logActionData(
        result: CreateScreenPageObjectDialogResult,
        tags: List<AndroidViewTagInfo>,
        properties: List<String>,
        pageObjectText: String,
        targetPsiDirectory: PsiDirectory?
    ) {
        logger.debug(
            """
        Create new screen page object
        ===
        dialog params: $result
        targetPsiDirectory: $targetPsiDirectory
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
