package ru.hh.android.plugin.feature_module.model.converter

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.core.model.ModelConverter
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.TemplateFileData
import ru.hh.android.plugin.feature_module.model.TemplatesFactoryModel


class CreateModuleConfigConverter
    : ProjectComponent,
        ModelConverter<CreateModuleConfig, TemplatesFactoryModel> {

    companion object {
        private const val TOKEN_PACKAGE_NAME = "package_name"
        private const val TOKEN_FORMATTED_LIBRARY_NAME = "formatted_library_name"
        private const val TOKEN_ENABLE_MOXY = "enable_moxy"
        private const val TOKEN_ADD_UI_MODULES_DEPENDENCIES = "need_add_ui_modules_dependencies"
        private const val TOKEN_NEED_CREATE_API_INTERFACE = "need_create_api_interface"
        private const val TOKEN_NEED_CREATE_REPOSITORY_WITH_INTERACTOR = "need_create_repository_with_interactor"
        private const val TOKEN_NEED_CREATE_INTERFACE_FOR_REPOSITORY = "need_create_interface_for_repository"
        private const val TOKEN_LIBRARIES_MODULES = "libraries_modules"

        private const val TEMPLATE_NAME_BUILD_GRADLE = "build.gradle.ftl"
        private const val TEMPLATE_NAME_GITIGNORE = "gitignore.ftl"
        private const val TEMPLATE_NAME_PROGUARD_RULES = "proguard-rules.pro.ftl"
        private const val TEMPLATE_NAME_ANDROID_MANIFEST = "AndroidManifest.xml.ftl"
        private const val TEMPLATE_NAME_DI_MODULE = "DIModule.kt.ftl"
        private const val TEMPLATE_NAME_API_INTERFACE = "ModuleApi.kt.ftl"
        private const val TEMPLATE_NAME_API_PROVIDER = "ApiProvider.kt.ftl"
        private const val TEMPLATE_NAME_REPOSITORY = "ModuleRepositoryImpl.kt.ftl"
        private const val TEMPLATE_NAME_REPOSITORY_INTERFACE = "ModuleRepository.kt.ftl"
        private const val TEMPLATE_NAME_INTERACTOR = "ModuleInteractor.kt.ftl"
    }


    override fun convert(item: CreateModuleConfig): TemplatesFactoryModel {
        return object : TemplatesFactoryModel {

            override fun getTemplatesFilesList(): List<TemplateFileData> {
                return with(item.mainParams) {
                    val basePath = "/${moduleType.typeRootFolder}/$moduleName"
                    val basePackageName = "src/main/java/$slashedPackageName"

                    mutableListOf<TemplateFileData>().apply {
                        this += TemplateFileData(
                                targetFilePath = "$basePath/build.gradle",
                                templateFileName = TEMPLATE_NAME_BUILD_GRADLE
                        )

                        this += TemplateFileData(
                                targetFilePath = "$basePath/.gitignore",
                                templateFileName = TEMPLATE_NAME_GITIGNORE
                        )

                        this += TemplateFileData(
                                targetFilePath = "$basePath/proguard-rules.pro",
                                templateFileName = TEMPLATE_NAME_PROGUARD_RULES
                        )

                        this += TemplateFileData(
                                targetFilePath = "$basePath/src/main/AndroidManifest.xml",
                                templateFileName = TEMPLATE_NAME_ANDROID_MANIFEST
                        )

                        this += TemplateFileData(
                                targetFilePath = "$basePath/$basePackageName/di/${formattedLibraryName}Module.kt",
                                templateFileName = TEMPLATE_NAME_DI_MODULE
                        )

                        if (needCreateAPIInterface) {
                            this += TemplateFileData(
                                    targetFilePath = "$basePath/$basePackageName/${formattedLibraryName}Api.kt",
                                    templateFileName = TEMPLATE_NAME_API_INTERFACE
                            )

                            this += TemplateFileData(
                                    targetFilePath = "$basePath/$basePackageName/di/${formattedLibraryName}ApiProvider.kt",
                                    templateFileName = TEMPLATE_NAME_API_PROVIDER
                            )
                        }

                        if (needCreateRepositoryWithInteractor) {
                            val repositoryImplClassName = if (needCreateInterfaceForRepository) {
                                "RepositoryImpl"
                            } else {
                                "Repository"
                            }
                            this += TemplateFileData(
                                    targetFilePath = "$basePath/$basePackageName/repository/$formattedLibraryName$repositoryImplClassName.kt",
                                    templateFileName = TEMPLATE_NAME_REPOSITORY
                            )
                            if (needCreateInterfaceForRepository) {
                                this += TemplateFileData(
                                        targetFilePath = "$basePath/$basePackageName/repository/${formattedLibraryName}Repository.kt",
                                        templateFileName = TEMPLATE_NAME_REPOSITORY_INTERFACE
                                )
                            }

                            this += TemplateFileData(
                                    targetFilePath = "$basePath/$basePackageName/interactor/${formattedLibraryName}Interactor.kt",
                                    templateFileName = TEMPLATE_NAME_INTERACTOR
                            )
                        }
                    }
                }
            }

            override fun toFreeMarkerDataModel(): Map<String, Any> {
                return with(item.mainParams) {
                    mapOf(
                            TOKEN_PACKAGE_NAME to packageName,
                            TOKEN_FORMATTED_LIBRARY_NAME to formattedLibraryName,
                            TOKEN_ENABLE_MOXY to enableMoxy,
                            TOKEN_ADD_UI_MODULES_DEPENDENCIES to addUIModulesDependencies,
                            TOKEN_NEED_CREATE_API_INTERFACE to needCreateAPIInterface,
                            TOKEN_NEED_CREATE_REPOSITORY_WITH_INTERACTOR to needCreateRepositoryWithInteractor,
                            TOKEN_NEED_CREATE_INTERFACE_FOR_REPOSITORY to needCreateInterfaceForRepository,
                            TOKEN_LIBRARIES_MODULES to item.libraries.map { it.text }
                    )
                }
            }
        }
    }

}