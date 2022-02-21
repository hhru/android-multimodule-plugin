package ru.hh.android.plugin.actions.boilerplate.fragment_view_model

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.idea.actions.generate.KotlinGenerateActionBase
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import ru.hh.android.plugin.extensions.psi.kotlin.addImportPackages
import ru.hh.android.plugin.utils.notifyError
import ru.hh.android.plugin.utils.notifyInfo
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.toKotlinFileName
import ru.hh.plugins.psi_utils.isInheritedFrom
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle

class GenerateFragmentViewModelAction : KotlinGenerateActionBase() {

    companion object {
        private const val COMMAND_NAME = "GenerateFragmentViewModelActionCommand"

        private const val MODELS_PACKAGE_NAME = "model"

        private const val HH_EXTENSION_STATE_VIEW_MODEL_FQCN = "ru.hh.android.mvvm.stateViewModel"
        private const val HH_EXTENSION_DI_GET_INSTANCE_FQCN =
            "ru.hh.shared_core_ui.fragment_plugin.common.di.getInstance"
        private const val BASE_FRAGMENT_FQCN = "ru.hh.shared_core_ui.fragment.BaseFragment"
    }

    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val featurePrefix = Messages.showInputDialog(
            project,
            "Enter feature prefix",
            "Generate Fragment ViewModel",
            Messages.getQuestionIcon(),
        )

        if (featurePrefix.isNullOrBlank()) {
            project.notifyError("You should enter feature prefix! E.g. 'SearchFilter'")
            return
        }

        val fragmentKtClass = getTargetClass(editor, psiFile) as KtClass
        val packageName = fragmentKtClass.containingKtFile.packageDirective?.fqName?.asString() ?: String.EMPTY

        val names = GenerateFragmentViewModelNames.from(featurePrefix, packageName)
        val psiElements = createPsiElements(project, names)

        modifyCode(project, psiFile, psiElements, fragmentKtClass)
        project.notifyInfo("${names.viewModelClassName} successfully created!")
    }

    override fun isValidForClass(targetClass: KtClassOrObject): Boolean {
        return targetClass is KtClass &&
            targetClass.toLightClass()?.isInheritedFrom(BASE_FRAGMENT_FQCN) ?: false
    }

    private fun createPsiElements(
        project: Project,
        names: GenerateFragmentViewModelNames
    ): GenerateFragmentViewModelPsiElements {
        val ktPsiFactory = KtPsiFactory(project)
        val textFactory = GenerateFragmentViewModelTextFactory.getInstance(project)

        val singleEventClassPsiFile = ktPsiFactory.createFile(
            fileName = names.singleEventClassName.toKotlinFileName(),
            text = textFactory.getSingleEventClassText(names)
        )
        val uiStateClassPsiFile = ktPsiFactory.createFile(
            fileName = names.uiStateClassName.toKotlinFileName(),
            text = textFactory.getUiStateClassText(names)
        )
        val uiStateClassConverterPsiFile = ktPsiFactory.createFile(
            fileName = names.uiStateConverterClassName.toKotlinFileName(),
            text = textFactory.getUiStateConverterClassText(names)
        )

        val viewModelClassPsiFile = ktPsiFactory.createFile(
            fileName = names.viewModelClassName.toKotlinFileName(),
            text = textFactory.getViewModelClassText(names)
        )

        val viewModelKtProperty = ktPsiFactory.createProperty(
            textFactory.getViewModelPropertyText(names)
        )
        val handleEventKtFunction = ktPsiFactory.createFunction(
            textFactory.getHandleEventMethodText(names)
        )
        val renderStateKtFunction = ktPsiFactory.createFunction(
            textFactory.getRenderStateMethodText(names)
        )

        return GenerateFragmentViewModelPsiElements(
            singleEventClassPsiFile = singleEventClassPsiFile,
            uiStateClassPsiFile = uiStateClassPsiFile,
            uiStateClassConverterPsiFile = uiStateClassConverterPsiFile,
            viewModelClassPsiFile = viewModelClassPsiFile,
            viewModelKtProperty = viewModelKtProperty,
            handleEventKtFunction = handleEventKtFunction,
            renderStateKtFunction = renderStateKtFunction
        )
    }

    private fun modifyCode(
        project: Project,
        psiFile: PsiFile,
        psiElements: GenerateFragmentViewModelPsiElements,
        fragmentKtClass: KtClass
    ) {
        project.executeWriteCommand(COMMAND_NAME) {
            // create new classes

            // model package
            val modelPsiDirectory = psiFile.containingDirectory.createSubdirectory(MODELS_PACKAGE_NAME)
            val singleEventClassAddedPsiFile = modelPsiDirectory.add(psiElements.singleEventClassPsiFile) as KtFile
            val uiStateClassAddedPsiFile = modelPsiDirectory.add(psiElements.uiStateClassPsiFile) as KtFile
            val uiStateClassConverterAddedPsiFile =
                modelPsiDirectory.add(psiElements.uiStateClassConverterPsiFile) as KtFile

            // root package
            val viewModelClassAddedPsiFile =
                psiFile.containingDirectory.add(psiElements.viewModelClassPsiFile) as KtFile

            // modify current Fragment class
            val body = fragmentKtClass.getOrCreateBody()
            body.addAfter(psiElements.viewModelKtProperty, body.lBrace)

            body.addBefore(psiElements.handleEventKtFunction, body.rBrace)
            body.addBefore(psiElements.renderStateKtFunction, body.rBrace)

            (psiFile as KtFile).addImportPackages(
                HH_EXTENSION_STATE_VIEW_MODEL_FQCN,
                HH_EXTENSION_DI_GET_INSTANCE_FQCN
            )

            listOf(
                singleEventClassAddedPsiFile,
                uiStateClassAddedPsiFile,
                uiStateClassConverterAddedPsiFile,
                viewModelClassAddedPsiFile,
                psiFile
            ).forEach { ktFile ->
                ktFile.shortReferencesAndReformatWithCodeStyle()
            }
        }
    }
}
