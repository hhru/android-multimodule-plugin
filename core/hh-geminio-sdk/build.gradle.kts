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
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)
}