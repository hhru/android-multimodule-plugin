package ru.hh.android.plugins.garcon.generator

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.TemplatesConstants
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.psi.androidManifestPackageName
import ru.hh.android.plugins.garcon.generator.freemarker.FreeMarkerWrapper
import ru.hh.android.plugins.garcon.model.mapping.ModelConverter
import ru.hh.android.plugins.garcon.model.mapping.PageObjectPropertyConverter
import ru.hh.android.plugins.garcon.model.mapping.XmlFileConverter
import ru.hh.android.plugins.garcon.model.page_object.PageObjectData
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty
import ru.hh.android.plugins.garcon.model.page_object.ScreenPageObjectInitData


class ScreenPageObjectInitDataConverter(
    private val eventLogger: GarconEventLogger,
    private val xmlFileConverter: XmlFileConverter,
    private val freeMarkerWrapper: FreeMarkerWrapper,
    private val pageObjectPropertyConverter: PageObjectPropertyConverter
) : ProjectComponent,
    ModelConverter<ScreenPageObjectInitData, PageObjectData> {

    override fun convert(item: ScreenPageObjectInitData): PageObjectData {
        val properties = xmlFileConverter.convert(item.xmlFile)

        return PageObjectData(
            classText = getScreenPageObjectClassText(item, properties),
            properties = properties
        )
    }


    private fun getScreenPageObjectClassText(
        item: ScreenPageObjectInitData,
        properties: List<PageObjectProperty>
    ): String {
        val rFilePackageName = item.xmlFile.androidManifestPackageName ?: String.EMPTY
        val propertiesDeclarations = properties.map { pageObjectPropertyConverter.convert(it) }
        val importClassesFQNList = properties.map { it.kakaoViewDeclaration.fqn }.toSet()
        val params = mapOf(
            TemplatesConstants.PARAMS_KEY_PACKAGE_NAME to item.packageName,
            TemplatesConstants.PARAMS_KEY_R_FILE_PACKAGE_NAME to rFilePackageName,
            TemplatesConstants.PARAMS_KEY_CLASS_NAME to item.className,
            TemplatesConstants.PARAMS_KEY_IMPORT_CLASSES_FQN_LIST to importClassesFQNList,
            TemplatesConstants.PARAMS_KEY_PROPERTIES_DECLARATIONS to propertiesDeclarations
        )

        logParams(params)

        return freeMarkerWrapper.resolveTemplate(
            templateName = TemplatesConstants.TEMPLATE_SCREEN_PAGE_OBJECT,
            params = params
        )
    }

    private fun logParams(params: Map<String, Any>) {
        eventLogger.debug(
            """
            Params for ${TemplatesConstants.TEMPLATE_SCREEN_PAGE_OBJECT} template
            
            ${params.map { entry -> "${entry.key} -> ${entry.value}" }.joinToString(separator = "\n")}
            """
        )
    }

}