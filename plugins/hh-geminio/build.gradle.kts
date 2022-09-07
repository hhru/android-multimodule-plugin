plugins {
    id("convention.idea-plugin")
}

// TODO [build-logic] Look with a fresh eye, why this needs to be duplicated, if there is common dependency resolution in settings.gradle
repositories {
    mavenCentral()
}

dependencies {
    // Core modules
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))
    implementation(project(":shared:core:logger"))
    implementation(project(":shared:core:notifications"))

    /**
     * IMPORTANT NOTE!
     *
     * Here added `compileOnly` with special purpose:
     * we need to compile our plugin even if there is no some classes in runtime of Android Studio.
     */
    compileOnly(project(":shared:core:android-studio-stubs"))

    // Feature modules
    implementation(project(":shared:feature:geminio-sdk"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.flexmark) // Markdown parser
}
