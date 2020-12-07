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
    // Core modules
    implementation(project(":shared-core-freemarker"))
    implementation(project(":shared-core-yaml"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}