plugins {
    id("convention.idea-plugin")
}

dependencies {
    // Core modules
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))

    // Feature modules
    implementation(project(":shared:feature:geminio-sdk"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.flexmark)   // Markdown parser
}