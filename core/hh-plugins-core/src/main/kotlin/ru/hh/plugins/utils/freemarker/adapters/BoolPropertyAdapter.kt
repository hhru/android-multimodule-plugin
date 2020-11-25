package ru.hh.plugins.utils.freemarker.adapters

import com.android.tools.idea.observable.core.BoolValueProperty
import freemarker.template.*


class BoolPropertyAdapter(
    objectWrapper: ObjectWrapper?,
    private val myBoolProperty: BoolValueProperty
) : WrappingTemplateModel(objectWrapper), TemplateBooleanModel, AdapterTemplateModel {

    override fun getAdaptedObject(hint: Class<*>?): Any {
        return myBoolProperty
    }

    @Throws(TemplateModelException::class)
    override fun getAsBoolean(): Boolean {
        return myBoolProperty.get()
    }

}