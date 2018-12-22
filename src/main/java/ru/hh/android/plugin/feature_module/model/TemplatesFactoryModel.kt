package ru.hh.android.plugin.feature_module.model


interface TemplatesFactoryModel {

    fun getTemplatesFilesList(): List<TemplateFileData>

    fun toFreeMarkerDataModel(): Map<String, Any>

}