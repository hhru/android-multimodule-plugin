package ru.hh.plugins.geminio.services.templates

import com.android.tools.idea.npw.model.NewAndroidModuleModel
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.npw.project.getModuleTemplates
import com.android.tools.idea.npw.project.getPackageForPath
import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.Template
import com.google.wireless.android.sdk.stats.AndroidStudioEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.geminio.models.GeminioAndroidModulePaths
import ru.hh.plugins.geminio.models.GeminioConfigureTemplateStepModel


@Service
class ConfigureTemplateParametersStepFactory {

    companion object {
        private const val NAMED_MODULE_TEMPLATE_NAME = "GeminioNamedModuleTemplate"

        private const val STUB_MODULE_NAME = "stub_module_name"
        private const val STUB_PARENT_MODULE_NAME = "stub_parent_module_name"


        fun getInstance(project: Project): ConfigureTemplateParametersStepFactory = project.service()
    }


    fun createFromAndroidFacet(
        commandName: String,
        stepTitle: String,
        facet: AndroidFacet,
        targetDirectory: VirtualFile,
        androidStudioTemplate: Template
    ): GeminioConfigureTemplateStepModel {
        val moduleTemplates = facet.getModuleTemplates(targetDirectory)
        assert(moduleTemplates.isNotEmpty())

        val renderTemplateModel = createRenderTemplateModelFromFacet(
            facet,
            targetDirectory = targetDirectory,
            moduleTemplates = moduleTemplates,
            commandName = commandName,
            androidStudioTemplate = androidStudioTemplate
        )

        val configureTemplateStep = ConfigureTemplateParametersStep(
            model = renderTemplateModel,
            title = stepTitle,
            templates = moduleTemplates
        )

        return GeminioConfigureTemplateStepModel(
            renderTemplateModel = renderTemplateModel,
            configureTemplateParametersStep = configureTemplateStep
        )
    }

    fun createForNewModule(
        project: Project,
        stepTitle: String,
        directoryPath: String,
        defaultPackageName: String,
        androidStudioTemplate: Template
    ): GeminioConfigureTemplateStepModel {
        val namedModuleTemplate = createNamedModuleTemplate(directoryPath)
        val renderTemplateModel = createRenderTemplateModelFromScratch(
            project = project,
            namedModuleTemplate = namedModuleTemplate,
            defaultPackageName = defaultPackageName,
            androidStudioTemplate = androidStudioTemplate
        )
        val configureTemplateStep = ConfigureTemplateParametersStep(
            model = renderTemplateModel,
            title = stepTitle,
            templates = listOf(namedModuleTemplate)
        )

        return GeminioConfigureTemplateStepModel(
            renderTemplateModel = renderTemplateModel,
            configureTemplateParametersStep = configureTemplateStep
        )
    }


    private fun createNamedModuleTemplate(directoryPath: String): NamedModuleTemplate {
        return NamedModuleTemplate(
            name = NAMED_MODULE_TEMPLATE_NAME,
            paths = GeminioAndroidModulePaths(
                basePath = directoryPath,
                moduleName = STUB_MODULE_NAME
            )
        )
    }

    private fun createRenderTemplateModelFromFacet(
        facet: AndroidFacet,
        targetDirectory: VirtualFile,
        moduleTemplates: List<NamedModuleTemplate>,
        commandName: String,
        androidStudioTemplate: Template
    ): RenderTemplateModel {
        val initialPackageSuggestion = facet.getPackageForPath(moduleTemplates, targetDirectory).orEmpty()

        return RenderTemplateModel.fromFacet(
            facet = facet,
            initialPackageSuggestion = initialPackageSuggestion,
            template = moduleTemplates[0],
            commandName = commandName,
            projectSyncInvoker = ProjectSyncInvoker.DefaultProjectSyncInvoker(),
            shouldOpenFiles = true,
            wizardContext = AndroidStudioEvent.TemplatesUsage.TemplateComponent.WizardUiContext.UNKNOWN_UI_CONTEXT
        ).apply {
            newTemplate = androidStudioTemplate
        }
    }

    private fun createRenderTemplateModelFromScratch(
        project: Project,
        namedModuleTemplate: NamedModuleTemplate,
        defaultPackageName: String,
        androidStudioTemplate: Template
    ): RenderTemplateModel {
        return RenderTemplateModel.fromModuleModel(
            NewAndroidModuleModel.fromExistingProject(
                project = project,
                moduleParent = STUB_PARENT_MODULE_NAME,
                projectSyncInvoker = ProjectSyncInvoker.DefaultProjectSyncInvoker(),
                isLibrary = true,
                formFactor = FormFactor.Mobile,
                category = Category.Other
            ).also {
                it.packageName.set(defaultPackageName)
                it.template.set(namedModuleTemplate)
            }
        ).apply {
            newTemplate = androidStudioTemplate
        }
    }

}
