package ru.hh.android.plugin.feature_module.core.model


interface ModelConverter<F, T> {

    fun convert(item: F): T

}