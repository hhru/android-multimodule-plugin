plugins {
    id("convention.idea-plugin")
}

intellijPlatform {
    pluginConfiguration {
        id = "ru.hh.plugins.Geminio"
        name = "Geminio"
    }
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
    implementation(Libs.flexmark) // Markdown parser
}
