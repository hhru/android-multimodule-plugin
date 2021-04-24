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
    implementation(project(":shared:core:utils"))

    implementation(kotlin("stdlib-jdk8"))
}