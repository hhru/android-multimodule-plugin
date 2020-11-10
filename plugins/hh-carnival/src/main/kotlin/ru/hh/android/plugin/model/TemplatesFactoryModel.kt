package ru.hh.android.plugin.model


interface TemplatesFactoryModel {

    fun getTemplatesFilesList(): List<TemplateFileData>

    fun toFreeMarkerDataModel(): Map<String, Any>

}