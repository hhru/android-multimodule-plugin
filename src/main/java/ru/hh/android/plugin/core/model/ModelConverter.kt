package ru.hh.android.plugin.core.model


interface ModelConverter<F, T> {

    fun convert(item: F): T

}