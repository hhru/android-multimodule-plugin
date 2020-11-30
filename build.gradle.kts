import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(BuildPlugins.kotlinPlugin)
    }
}

allprojects {
    group = "ru.hh.plugins"

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.languageVersion = "1.4"
    }
}

plugins {
    id(GradlePlugins.collectUpdatePluginsXml)
    id(GradlePlugins.buildAllPlugins)
}