package ru.hh.plugins.geminio.services.templates

import com.android.sdklib.AndroidMajorVersion
import com.android.sdklib.AndroidVersion
import com.android.tools.idea.gradle.plugin.AgpVersions
import com.android.tools.idea.npw.template.ModuleTemplateDataBuilder
import com.android.tools.idea.npw.template.ProjectTemplateDataBuilder
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.templates.recipe.DefaultRecipeExecutor
import com.android.tools.idea.templates.recipe.RenderingContext
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.ApiTemplateData
import com.android.tools.idea.wizard.template.ApiVersion
import com.android.tools.idea.wizard.template.BaseFeature
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.Language
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.ProjectTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.ThemeData
import com.android.tools.idea.wizard.template.ThemesData
import com.android.tools.idea.wizard.template.ViewBindingSupport
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.gradle.util.GradleVersion
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.extensions.packageName
import ru.hh.plugins.geminio.models.GeminioAndroidModulePaths
import ru.hh.plugins.geminio.models.GeminioRecipeExecutorModel
import ru.hh.plugins.geminio.models.GeminioSourceSetConfig
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import java.io.File

class GeminioRecipeExecutorFactoryService(
    private val project: Project
) {

    private companion object {
        const val STUB_PREFIX = "geminio.stub"
        const val STUB_BASE_FEATURE_NAME = "$STUB_PREFIX.base_feature"
        const val STUB_APP_NAME = "$STUB_PREFIX.app_name"
        const val STUB_MAIN_THEME_DATA_NAME = "$STUB_PREFIX.main_theme_data"
        const val STUB_API_VERSION = 30
        const val STUB_API_VERSION_STRING = "30"
        const val STUB_COMMAND_RENDERING_CONTEXT = "$STUB_PREFIX.rendering_context_command"
        const val STUB_CURRENT_VARIANT = "main"
    }

    fun createRecipeExecutor(
        newModuleRootDirectoryPath: String,
        geminioTemplateData: GeminioTemplateData
    ): GeminioRecipeExecutorModel {
        val moduleName = geminioTemplateData.getModuleName()
        val packageName = geminioTemplateData.getPackageName()
        val sourceSet = geminioTemplateData.getSourceSet()
        val sourceCodeFolderName = geminioTemplateData.getSourceCodeFolderName()

        val moduleTemplateData = createModuleTemplateData(
            project = project,
            directoryPath = newModuleRootDirectoryPath,
            moduleName = moduleName,
            packageName = packageName,
            sourceSetConfig = GeminioSourceSetConfig(
                sourceSet = sourceSet,
                sourceCodeFolderName = sourceCodeFolderName,
            ),
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

    fun createRecipeExecutorForExistingModule(
        facet: AndroidFacet,
        targetDirectory: VirtualFile,
        targetPackageName: String,
    ): PreparedExistingModuleRecipeExecution {
        val templateContext = facet.createGeminioNamedModuleTemplateContext(targetDirectory)
        val moduleTemplateData = createExistingModuleTemplateData(
            project = project,
            module = facet.module,
            namedModuleTemplate = templateContext.namedModuleTemplate,
            targetPackageName = targetPackageName,
            applicationPackageName = facet.packageName.ifBlank { targetPackageName },
        )
        val createdFiles = linkedSetOf<File>()

        val renderingContext = RenderingContext(
            project = project,
            module = facet.module,
            commandName = STUB_COMMAND_RENDERING_CONTEXT,
            templateData = moduleTemplateData,
            outputRoot = moduleTemplateData.rootDir,
            moduleRoot = moduleTemplateData.rootDir,
            dryRun = false,
            showErrors = true,
        )

        return PreparedExistingModuleRecipeExecution(
            recipeExecutor = RecordingRecipeExecutor(
                delegate = DefaultRecipeExecutor(renderingContext),
                createdFiles = createdFiles,
            ),
            moduleTemplateData = moduleTemplateData,
            createdFiles = createdFiles,
        )
    }

    private fun createModuleTemplateData(
        project: Project,
        directoryPath: String,
        moduleName: String,
        packageName: String,
        sourceSetConfig: GeminioSourceSetConfig,
    ): ModuleTemplateData {
        return ModuleTemplateDataBuilder(
            projectTemplateDataBuilder = newProjectTemplateDataBuilder(
                project,
                packageName
            ),
            isNewModule = true,
            viewBindingSupport = ViewBindingSupport.NOT_SUPPORTED
        ).also { builder ->
            builder.setModuleRoots(
                paths = GeminioAndroidModulePaths(
                    basePath = directoryPath,
                    moduleName = moduleName,
                    sourceSetConfig = sourceSetConfig,
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
                buildApi = createStubAndroidVersion(),
                targetApi = createStubAndroidMajorVersion(),
                minApi = createStubAndroidMajorVersion(),
                appCompatVersion = STUB_API_VERSION,
            )
            builder.category = Category.Other
        }.build()
    }

    private fun newProjectTemplateDataBuilder(
        project: Project,
        packageName: String
    ): ProjectTemplateDataBuilder = ProjectTemplateDataBuilder(isNewProject = false)
        .also { builder ->
            builder.applicationPackage = packageName
            builder.language = Language.Kotlin

            // Starting from Android Studio Iguana this property is required
            builder.agpVersion = AgpVersions.latestKnown

            builder.setProjectDefaults(project)
        }

    private fun createStubAndroidVersion(): com.android.sdklib.AndroidVersion {
        return AndroidVersion(STUB_API_VERSION, STUB_API_VERSION_STRING)
    }

    private fun createStubAndroidMajorVersion(): AndroidMajorVersion {
        return AndroidMajorVersion(STUB_API_VERSION, STUB_API_VERSION_STRING)
    }

    private fun createExistingModuleTemplateData(
        project: Project,
        module: Module,
        namedModuleTemplate: NamedModuleTemplate,
        targetPackageName: String,
        applicationPackageName: String,
    ): ModuleTemplateData {
        val moduleRoot = requireNotNull(namedModuleTemplate.paths.moduleRoot) {
            "Cannot resolve module root for Android module '${namedModuleTemplate.name}'"
        }
        val srcDir = requireNotNull(namedModuleTemplate.paths.getSrcDirectory(targetPackageName)) {
            "Cannot resolve src directory for package '$targetPackageName' in module '${namedModuleTemplate.name}'"
        }

        return ModuleTemplateData(
            projectTemplateData = createProjectTemplateData(
                project = project,
                applicationPackageName = applicationPackageName,
            ),
            srcDir = srcDir,
            resDir = namedModuleTemplate.paths.resDirectories.firstOrNull()
                ?: moduleRoot.resolve("src/main/res"),
            manifestDir = namedModuleTemplate.paths.manifestDirectory
                ?: moduleRoot.resolve("src/main"),
            testDir = namedModuleTemplate.paths.getTestDirectory(targetPackageName)
                ?: moduleRoot.resolve("src/test/java"),
            unitTestDir = namedModuleTemplate.paths.getUnitTestDirectory(targetPackageName)
                ?: moduleRoot.resolve("src/test/java"),
            aidlDir = namedModuleTemplate.paths.getAidlDirectory(targetPackageName)
                ?: moduleRoot.resolve("src/main/aidl"),
            rootDir = moduleRoot,
            isNewModule = false,
            name = module.name,
            isLibrary = facetLikeIsLibrary(module),
            packageName = targetPackageName,
            formFactor = FormFactor.Mobile,
            themesData = ThemesData(
                appName = STUB_APP_NAME,
                main = ThemeData(STUB_MAIN_THEME_DATA_NAME, true),
            ),
            baseFeature = null,
            apis = ApiTemplateData(
                buildApi = createStubAndroidVersion(),
                targetApi = createStubAndroidMajorVersion(),
                minApi = createStubAndroidMajorVersion(),
                appCompatVersion = STUB_API_VERSION,
            ),
            viewBindingSupport = ViewBindingSupport.NOT_SUPPORTED,
            category = Category.Other,
            isMaterial3 = false,
            useGenericLocalTests = true,
            useGenericInstrumentedTests = true,
            isCompose = false,
            currentVariant = STUB_CURRENT_VARIANT,
        )
    }

    private fun createProjectTemplateData(
        project: Project,
        applicationPackageName: String,
    ): ProjectTemplateData {
        return ProjectTemplateData(
            androidXSupport = true,
            agpVersion = com.android.ide.common.repository.AgpVersion(8, 0),
            sdkDir = File(project.basePath ?: "."),
            language = Language.Kotlin,
            kotlinVersion = KotlinVersion.CURRENT.toString(),
            rootDir = File(project.basePath ?: "."),
            applicationPackage = applicationPackageName,
            includedFormFactorNames = mapOf(
                FormFactor.Mobile to listOf("mobile"),
            ),
            debugKeystoreSha1 = null,
            overridePathCheck = false,
            isNewProject = false,
            additionalMavenRepos = emptyList(),
            gradleVersion = GradleVersion.current(),
        )
    }

    private fun facetLikeIsLibrary(module: Module): Boolean {
        return AndroidFacet.getInstance(module)?.configuration?.isLibraryProject ?: false
    }

    private fun GeminioTemplateData.getModuleName(): String {
        return existingParametersMap[geminioIds.newModuleNameParameterId]!!.value as String
    }

    private fun GeminioTemplateData.getPackageName(): String {
        return existingParametersMap[geminioIds.newModulePackageNameParameterId]!!.value as String
    }

    private fun GeminioTemplateData.getSourceCodeFolderName(): String {
        return existingParametersMap[geminioIds.newModuleSourceCodeFolderParameterId]!!.value as String
    }

    private fun GeminioTemplateData.getSourceSet(): String {
        return existingParametersMap[geminioIds.newModuleSourceSetParameterId]!!.value as String
    }

    data class PreparedExistingModuleRecipeExecution(
        val recipeExecutor: RecipeExecutor,
        val moduleTemplateData: ModuleTemplateData,
        val createdFiles: LinkedHashSet<File>,
    )

    /**
     * Temporary bridge executor that keeps track of created files for post-processing.
     *
     * We still rely on Android Studio's `DefaultRecipeExecutor` underneath, but the custom Geminio
     * dialog no longer gets `createdFiles` from `RenderTemplateModel`, so we record them here.
     */
    private class RecordingRecipeExecutor(
        private val delegate: RecipeExecutor,
        private val createdFiles: LinkedHashSet<File>,
    ) : RecipeExecutor by delegate {

        override fun save(source: String, to: File) {
            delegate.save(source, to)
            createdFiles += to
        }

        override fun copy(from: File, to: File) {
            delegate.copy(from, to)
            createdFiles += to
        }

        override fun append(source: String, to: File) {
            delegate.append(source, to)
            createdFiles += to
        }
    }
}
