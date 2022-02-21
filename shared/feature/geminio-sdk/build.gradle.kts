plugins {
    id("convention.idea-plugin-library")
}

// TODO [build-logic] Look with a fresh eye, why this needs to be duplicated, if there is common dependency resolution in settings.gradle
repositories {
    mavenCentral()
}

dependencies {
    // Core modules
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:models"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}
