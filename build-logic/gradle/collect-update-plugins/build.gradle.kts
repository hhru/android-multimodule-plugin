plugins {
    `kotlin-dsl`
    id("convention.libraries")
}

group = "ru.hh.plugins.gradle"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
    implementation("ru.hh.plugins.build_logic:idea-convention")
    implementation("ru.hh.plugins.gradle:core-utils")
    implementation(Libs.gradleIntelliJPlugin)
}

gradlePlugin {
    plugins {
        create("collectUpdatePlugins") {
            id = "ru.hh.plugins.gradle.collect-update-plugins"
            implementationClass = "ru.hh.plugins.gradle.collect_update_plugins.CollectUpdatePluginsXmlGradlePlugin"
            displayName = "Collect updatePlugins.xml file"
        }
    }
}
