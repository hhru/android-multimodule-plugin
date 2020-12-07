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
    implementation(project(":hh-freemarker-wrapper"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}