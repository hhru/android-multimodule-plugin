package ru.hh.plugins.static_analysis

abstract class StaticAnalysisExtension {

    val detekt = DetektConfigExtension()


    fun detekt(configure: DetektConfigExtension.() -> Unit) {
        detekt.apply(configure)
    }

}