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
    implementation(project(":shared-feature-geminio-sdk"))// todo remove
    implementation(project(":shared-core-freemarker"))// todo remove

    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)
}