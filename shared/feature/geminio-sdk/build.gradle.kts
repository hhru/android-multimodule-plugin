plugins {
    id("convention.idea-plugin-library")
}

dependencies {
    // Core modules
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))
    implementation(project(":shared:core:utils"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}