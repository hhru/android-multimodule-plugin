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
    intellijPlatform.bundledPlugin("org.intellij.groovy")

    // Libraries
    implementation(Libs.flexmark) // Markdown parser
    implementation(Libs.freemarker)

    testImplementation(Libs.tests.kotest)
}
