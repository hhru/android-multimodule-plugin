package ru.hh.plugins.geminio.services.templates

import com.android.tools.idea.npw.model.NewAndroidModuleModel
import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.npw.project.getModuleTemplates
import com.android.tools.idea.npw.project.getPackageForPath
import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep
import com.android.tools.idea.projectsystem.AndroidModulePaths
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.Template
import com.google.wireless.android.sdk.stats.AndroidStudioEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.extensions.toSlashedFilePath
import ru.hh.plugins.geminio.models.GeminioAndroidModulePaths
import ru.hh.plugins.geminio.models.GeminioConfigureTemplateStepModel
import ru.hh.plugins.geminio.services.StubProjectSyncInvoker
import ru.hh.plugins.logger.HHLogger
import java.io.File

class ConfigureTemplateParametersStepFactory(
    private val project: Project
) {

    private companion object {
        const val NAMED_MODULE_TEMPLATE_NAME = "GeminioNamedModuleTemplate"

        const val STUB_MODULE_NAME = "stub_module_name"
        const val STUB_PARENT_MODULE_NAME = "stub_parent_module_name"
        const val STUB_SOURCE_SET = "stub_source_set"
        const val STUB_SOURCE_CODE_FOLDER_NAME = "stub_source_code_folder_name"
    }

    fun createFromAndroidFacet(
        commandName: String,
        stepTitle: String,
        facet: AndroidFacet,
        targetDirectory: VirtualFile,
        androidStudioTemplate: Template
    ): GeminioConfigureTemplateStepModel {
        val moduleTemplates = facet.getNamedModuleTemplate(targetDirectory)
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
                moduleName = STUB_MODULE_NAME,
                sourceSet = STUB_SOURCE_SET,
                sourceCodeFolderName = STUB_SOURCE_CODE_FOLDER_NAME,
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
            projectSyncInvoker = StubProjectSyncInvoker(),
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
                projectSyncInvoker = StubProjectSyncInvoker(),
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

    private fun AndroidFacet.getNamedModuleTemplate(targetDirectory: VirtualFile): List<NamedModuleTemplate> {
        val originalModuleTemplates = this.getModuleTemplates(targetDirectory)
        assert(originalModuleTemplates.isNotEmpty())

        val moduleTemplate = originalModuleTemplates.first()

        /**
         * Sometimes after fetching module templates information from [org.jetbrains.android.facet.AndroidFacet]
         * there is no information about AIDL and SRC sources directory.
         *
         * But AIDL directory is necessary for [com.android.tools.idea.npw.template.ModuleTemplateDataBuilder] in
         * `build` method (has line with `aidlDir!!`) which leads to NullPointerException crash -->
         * not modules templates skip files generation.
         *
         * And SRC directory is necessary for files creation, without specifing this directory
         * [com.android.tools.idea.npw.model.RenderTemplateModel] will not start files creation.
         *
         * So, we need to manually fix this problem through simple wrapper over
         * [com.android.tools.idea.projectsystem.AndroidModulePaths].
         */
        val shouldReplaceAidlDirectory = moduleTemplate.paths.getAidlDirectory("stub.package") == null
        val shouldReplaceSrcDirectory = moduleTemplate.paths.getSrcDirectory("stub.package") == null

        HHLogger.d("Should replace AIDL directory: $shouldReplaceAidlDirectory")
        HHLogger.d("Should replace SRC directory: $shouldReplaceSrcDirectory")
        return listOf(
            moduleTemplate.copy(
                paths = StubAndroidModulePaths(
                    original = moduleTemplate.paths,
                    baseModuleName = moduleTemplate.name,
                    shouldReplaceAidlDirectory = shouldReplaceAidlDirectory,
                    shouldReplaceSrcDirectory = shouldReplaceSrcDirectory,
                ),
            )
        )
    }

    private class StubAndroidModulePaths(
        private val original: AndroidModulePaths,
        private val baseModuleName: String,
        private val shouldReplaceAidlDirectory: Boolean,
        private val shouldReplaceSrcDirectory: Boolean,
    ) : AndroidModulePaths {
        override val manifestDirectory: File?
            get() = original.manifestDirectory
        override val moduleRoot: File?
            get() = original.moduleRoot
        override val resDirectories: List<File>
            get() = original.resDirectories

        override fun getAidlDirectory(packageName: String?): File? {
            return if (shouldReplaceAidlDirectory) {
                original.moduleRoot
                    ?.resolve("src/$baseModuleName/aidl" + packageName?.toSlashedFilePath().orEmpty())
            } else {
                original.getAidlDirectory(packageName)
            }
        }

        override fun getSrcDirectory(packageName: String?): File? {
            return if (shouldReplaceSrcDirectory) {
                original.moduleRoot
                    ?.resolve("src/$baseModuleName/java" + packageName?.toSlashedFilePath().orEmpty())
            } else {
                original.getSrcDirectory(packageName)
            }
        }

        override fun getTestDirectory(packageName: String?): File? {
            return original.getTestDirectory(packageName)
        }

        override fun getUnitTestDirectory(packageName: String?): File? {
            return original.getUnitTestDirectory(packageName)
        }
    }
}
