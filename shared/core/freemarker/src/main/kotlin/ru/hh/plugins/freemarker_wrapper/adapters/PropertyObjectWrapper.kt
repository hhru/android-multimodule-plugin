package ru.hh.plugins.freemarker_wrapper.adapters

import com.android.tools.idea.observable.core.BoolValueProperty
import com.android.tools.idea.observable.core.IntValueProperty
import com.android.tools.idea.observable.core.StringValueProperty
import freemarker.template.Configuration
import freemarker.template.DefaultObjectWrapper
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException

class PropertyObjectWrapper : DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS) {

    @Throws(TemplateModelException::class)
    override fun handleUnknownType(obj: Any): TemplateModel {
        return when (obj) {
            is StringValueProperty -> StringPropertyAdapter(this, obj)
            is IntValueProperty -> IntPropertyAdapter(this, obj)
            is BoolValueProperty -> BoolPropertyAdapter(this, obj)
            else -> super.handleUnknownType(obj) as TemplateModel
        }
    }
}