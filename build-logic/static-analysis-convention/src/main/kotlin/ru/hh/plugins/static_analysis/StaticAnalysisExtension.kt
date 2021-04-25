package ru.hh.plugins.static_analysis

import org.gradle.util.ConfigureUtil

abstract class StaticAnalysisExtension {

    val detekt = DetektConfigExtension()


    fun detekt(configure: DetektConfigExtension.() -> Unit) {
        detekt.apply(configure)
    }

}