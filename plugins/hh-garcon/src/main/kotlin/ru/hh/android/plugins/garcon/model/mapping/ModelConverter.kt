package ru.hh.android.plugins.garcon.model.mapping


interface ModelConverter<T, R> {

    fun convert(item: T): R

}