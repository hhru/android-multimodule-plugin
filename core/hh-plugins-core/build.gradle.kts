plugins {
    id(GradlePlugins.gradleIntelliJPlugin)
    kotlin("jvm")
    id(GradlePlugins.setupIdeaPlugin)
    id(GradlePlugins.coreModuleMarker)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":hh-geminio-sdk"))// todo remove

    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)
}