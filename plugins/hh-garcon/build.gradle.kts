plugins {
    id("convention.idea-plugin")
}

intellijPlatform {
    pluginConfiguration {
        id = "ru.hh.plugins.Garcon"
        name = "Garcon"
    }
}

dependencies {
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))
    implementation(project(":shared:core:logger"))
    implementation(project(":shared:core:notifications"))
    implementation(Libs.freemarker)
}
