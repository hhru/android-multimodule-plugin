package ru.hh.android.plugins.garcon.model.mapping

import com.intellij.openapi.components.ProjectComponent
import com.intellij.psi.xml.XmlFile
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.extensions.psi.collectAndroidViewsTagsInfo
import ru.hh.android.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty


class XmlFileConverter(
    private val eventLogger: GarconEventLogger,
    private val androidViewTagInfoConverter: AndroidViewTagInfoConverter
) : ProjectComponent,
    ModelConverter<XmlFile, List<PageObjectProperty>> {

    override fun convert(item: XmlFile): List<PageObjectProperty> {
        val viewsTags = item.collectAndroidViewsTagsInfo()
        logCollectedView(item, viewsTags)
        return viewsTags.map { androidViewTagInfoConverter.convert(it) }
    }


    private fun logCollectedView(
        item: XmlFile,
        viewsTags: List<AndroidViewTagInfo>
    ) {
        eventLogger.debug(
            """
            Collected views from ${item.name}:
            
            ${viewsTags.joinToString(separator = "\n")}
            ======
            """
        )
    }

}