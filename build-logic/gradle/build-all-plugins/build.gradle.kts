plugins {
    `kotlin-dsl`
    id("convention.libraries")
}

group = "ru.hh.plugins.gradle"

dependencies {
    implementation("ru.hh.plugins.build_logic:libraries")
    implementation("ru.hh.plugins.build_logic:idea-convention")
    implementation("ru.hh.plugins.gradle:core-utils")
}

gradlePlugin {
    plugins {
        create("buildAllPlugins") {
            id = "ru.hh.plugins.gradle.build-all-plugins"
            implementationClass = "ru.hh.plugins.gradle.build_all_plugins.BuildAllPluginsGradlePlugin"
            displayName = "Build all plugins and move artifacts into single directory"
        }
    }
}
