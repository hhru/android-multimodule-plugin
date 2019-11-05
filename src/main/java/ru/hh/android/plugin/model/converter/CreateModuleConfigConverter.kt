package ru.hh.android.plugin.model.converter

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.core.model.ModelConverter
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.TemplateFileData
import ru.hh.android.plugin.model.TemplatesFactoryModel


class CreateModuleConfigConverter
    : ProjectComponent,
        ModelConverter<CreateModuleConfig, TemplatesFactoryModel> {

    companion object {
        private const val TOKEN_PACKAGE_NAME = "package_name"
        private const val TOKEN_FORMATTED_LIBRARY_NAME = "formatted_library_name"
        private const val TOKEN_LAYOUT_NAME = "layout_name"
        private const val TOKEN_LIBRARIES_MODULES = "libraries_modules"
    }


    override fun convert(item: CreateModuleConfig): TemplatesFactoryModel {
        return object : TemplatesFactoryModel {

            override fun getTemplatesFilesList(): List<TemplateFileData> {
                return listOf()
            }

            override fun toFreeMarkerDataModel(): Map<String, Any> {
                return with(item) {
                    val map = mutableMapOf<String, Any>()

                    map[TOKEN_PACKAGE_NAME] = params.packageName
                    map[TOKEN_FORMATTED_LIBRARY_NAME] = formattedLibraryName
                    map[TOKEN_LAYOUT_NAME] = layoutName
                    map[TOKEN_LIBRARIES_MODULES] = item.libraries.map { it.text }
                    map += getPredefinedSettingsMap()

                    map
                }
            }
        }
    }

}