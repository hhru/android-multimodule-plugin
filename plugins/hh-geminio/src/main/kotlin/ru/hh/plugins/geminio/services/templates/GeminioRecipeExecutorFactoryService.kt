package ru.hh.plugins.geminio.services.templates

import com.android.tools.idea.npw.template.ModuleTemplateDataBuilder
import com.android.tools.idea.npw.template.ProjectTemplateDataBuilder
import com.android.tools.idea.templates.recipe.DefaultRecipeExecutor
import com.android.tools.idea.templates.recipe.RenderingContext
import com.android.tools.idea.wizard.template.ApiTemplateData
import com.android.tools.idea.wizard.template.ApiVersion
import com.android.tools.idea.wizard.template.BaseFeature
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.Language
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.ThemeData
import com.android.tools.idea.wizard.template.ThemesData
import com.android.tools.idea.wizard.template.ViewBindingSupport
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.models.GeminioAndroidModulePaths
import ru.hh.plugins.geminio.models.GeminioRecipeExecutorModel
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import java.io.File

@Service
class GeminioRecipeExecutorFactoryService {

    companion object {
        private const val STUB_PREFIX = "geminio.stub"
        private const val STUB_BASE_FEATURE_NAME = "$STUB_PREFIX.base_feature"
        private const val STUB_APP_NAME = "$STUB_PREFIX.app_name"
        private const val STUB_MAIN_THEME_DATA_NAME = "$STUB_PREFIX.main_theme_data"
        private const val STUB_API_VERSION = 30
        private const val STUB_API_VERSION_STRING = "30"
        private const val STUB_COMMAND_RENDERING_CONTEXT = "$STUB_PREFIX.rendering_context_command"

        fun getInstance(project: Project): GeminioRecipeExecutorFactoryService = project.service()
    }

    fun createRecipeExecutor(
        project: Project,
        newModuleRootDirectoryPath: String,
        geminioTemplateData: GeminioTemplateData
    ): GeminioRecipeExecutorModel {
        val moduleName = geminioTemplateData.getModuleName()
        val packageName = geminioTemplateData.getPackageName()

        val moduleTemplateData = createModuleTemplateData(
            project = project,
            directoryPath = newModuleRootDirectoryPath,
            moduleName = moduleName,
            packageName = packageName
        )

        val renderingContext = RenderingContext(
            project = project,
            module = null,
            commandName = STUB_COMMAND_RENDERING_CONTEXT,
            templateData = moduleTemplateData,
            outputRoot = File(newModuleRootDirectoryPath).resolve(moduleName),
            moduleRoot = File(newModuleRootDirectoryPath).resolve(moduleName),
            dryRun = false,
            showErrors = true
        )

        return GeminioRecipeExecutorModel(
            moduleName = moduleName,
            recipeExecutor = DefaultRecipeExecutor(renderingContext),
            moduleTemplateData = moduleTemplateData
        )
    }

    private fun createModuleTemplateData(
        project: Project,
        directoryPath: String,
        moduleName: String,
        packageName: String
    ): ModuleTemplateData {
        val projectTemplateDataBuilder = ProjectTemplateDataBuilder(isNewProject = false)
            .also { builder ->
                builder.applicationPackage = packageName
                builder.language = Language.Kotlin
                builder.setProjectDefaults(project)
            }

        return ModuleTemplateDataBuilder(
            projectTemplateDataBuilder = projectTemplateDataBuilder,
            isNewModule = true,
            viewBindingSupport = ViewBindingSupport.NOT_SUPPORTED
        ).also { builder ->
            builder.setModuleRoots(
                paths = GeminioAndroidModulePaths(
                    basePath = directoryPath,
                    moduleName = moduleName
                ),
                projectPath = project.basePath!!,
                moduleName = ":$moduleName",
                packageName = packageName
            )
            builder.isLibrary = false
            builder.formFactor = FormFactor.Mobile
            builder.baseFeature = BaseFeature(
                STUB_BASE_FEATURE_NAME,
                File("$directoryPath/$moduleName/src/main/java"),
                File("$directoryPath/$moduleName/src/main/res"),
            )
            builder.themesData = ThemesData(
                appName = STUB_APP_NAME,
                main = ThemeData(STUB_MAIN_THEME_DATA_NAME, true)
            )
            builder.apis = ApiTemplateData(
                buildApi = createStubApiVersion(),
                targetApi = createStubApiVersion(),
                minApi = createStubApiVersion(),
                appCompatVersion = STUB_API_VERSION,
            )
            builder.category = Category.Other
        }.build()
    }

    private fun createStubApiVersion(): ApiVersion {
        return ApiVersion(STUB_API_VERSION, STUB_API_VERSION_STRING)
    }

    private fun GeminioTemplateData.getModuleName(): String {
        return existingParametersMap[geminioIds.newModuleNameParameterId]!!.value as String
    }

    private fun GeminioTemplateData.getPackageName(): String {
        return existingParametersMap[geminioIds.newModulePackageNameParameterId]!!.value as String
    }
}
