package ru.hh.plugins.utils.freemarker.adapters

import com.android.tools.idea.observable.core.StringValueProperty
import freemarker.template.*


class StringPropertyAdapter(
    objectWrapper: ObjectWrapper?,
    private val myStringProperty: StringValueProperty
) : WrappingTemplateModel(objectWrapper), TemplateScalarModel, AdapterTemplateModel {

    override fun getAdaptedObject(hint: Class<*>?): Any {
        return myStringProperty
    }

    @Throws(TemplateModelException::class)
    override fun getAsString(): String {
        return myStringProperty.get()
    }

}