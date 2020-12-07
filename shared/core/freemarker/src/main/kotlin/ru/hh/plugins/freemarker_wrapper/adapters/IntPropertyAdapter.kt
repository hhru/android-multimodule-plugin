package ru.hh.plugins.freemarker_wrapper.adapters

import com.android.tools.idea.observable.core.IntValueProperty
import freemarker.template.*


class IntPropertyAdapter(
    objectWrapper: ObjectWrapper?,
    private val myIntProperty: IntValueProperty
) : WrappingTemplateModel(objectWrapper), TemplateNumberModel, AdapterTemplateModel {

    override fun getAdaptedObject(hint: Class<*>?): Any {
        return myIntProperty
    }

    @Throws(TemplateModelException::class)
    override fun getAsNumber(): Number {
        return myIntProperty.get()
    }

}