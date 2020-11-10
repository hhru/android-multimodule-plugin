package ru.hh.android.plugins.garcon.generator

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.model.mapping.ModelConverter
import ru.hh.android.plugins.garcon.model.page_object.PageObjectData
import ru.hh.android.plugins.garcon.model.page_object.PageObjectInitData
import ru.hh.android.plugins.garcon.model.page_object.RecyclerViewItemPageObjectInitData
import ru.hh.android.plugins.garcon.model.page_object.ScreenPageObjectInitData


class PageObjectFactory(
    private val eventLogger: GarconEventLogger,
    private val screenPageObjectInitDataConverter: ScreenPageObjectInitDataConverter,
    private val recyclerViewItemPageObjectInitDataConverter: RecyclerViewItemPageObjectInitDataConverter
) : ProjectComponent,
    ModelConverter<PageObjectInitData, PageObjectData> {

    override fun convert(item: PageObjectInitData): PageObjectData {
        eventLogger.debug("Converting page object item [item: $item]")
        return when (item) {
            is ScreenPageObjectInitData -> screenPageObjectInitDataConverter.convert(item)
            is RecyclerViewItemPageObjectInitData -> recyclerViewItemPageObjectInitDataConverter.convert(item)
        }
    }

}