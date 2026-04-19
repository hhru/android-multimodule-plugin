package ru.hh.plugins.freemarker_wrapper.adapters

import com.android.tools.idea.observable.core.BoolValueProperty
import freemarker.template.AdapterTemplateModel
import freemarker.template.ObjectWrapper
import freemarker.template.TemplateBooleanModel
import freemarker.template.TemplateModelException
import freemarker.template.WrappingTemplateModel

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
