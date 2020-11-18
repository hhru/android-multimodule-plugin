package ru.hh.plugins.utils.freemarker

import com.google.common.base.Charsets
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler

class FreemarkerConfiguration : Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS) {

    init {
        defaultEncoding = Charsets.UTF_8.name()
        localizedLookup = false
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        objectWrapper = PropertyObjectWrapper()
    }

}