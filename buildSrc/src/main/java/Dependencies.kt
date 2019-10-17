
object Versions {

    // TODO - Value is duplicated in buildSrc/gradle.properties
    const val kotlinVersion = "1.3.50"

    const val intellijPluginVersion = "0.4.10"
    const val freeMarkerVersion = "2.3.29"
    const val commonsIOVersion = "2.4"
    const val moxyVersion = "1.5.5"

}

object Libs {

    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val freeMarker = "org.freemarker:freemarker:${Versions.freeMarkerVersion}"
    const val commonsIO = "commons-io:commons-io:${Versions.commonsIOVersion}"
    const val moxy = "com.arello-mobile:moxy:${Versions.moxyVersion}"

}

object Plugins {

    const val intellij = "gradle.plugin.org.jetbrains.intellij.plugins:gradle-intellij-plugin:${Versions.intellijPluginVersion}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"

}