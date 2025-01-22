plugins {
    id("convention.idea-plugin-library")
}

dependencies {
    // Core modules
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:logger"))

    // Libraries
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest) // for kotest framework
}
