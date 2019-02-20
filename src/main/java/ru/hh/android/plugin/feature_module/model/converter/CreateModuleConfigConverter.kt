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
        private const val TOKEN_NEED_CREATE_PRESENTATION_LAYER = "need_create_presentation_layer"
        private const val TOKEN_LAYOUT_NAME = "layout_name"
    }


    override fun convert(item: CreateModuleConfig): TemplatesFactoryModel {
        return object : TemplatesFactoryModel {

            override fun getTemplatesFilesList(): List<TemplateFileData> {
                return listOf()
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
                            TOKEN_LIBRARIES_MODULES to item.libraries.map { it.text },
                            TOKEN_NEED_CREATE_PRESENTATION_LAYER to needCreatePresentationLayer,
                            TOKEN_LAYOUT_NAME to layoutName
                    )
                }
            }
        }
    }

}