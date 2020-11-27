plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
    implementation("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.6.5")
}

gradlePlugin {
    plugins {
        create("setupIdeaPlugin") {
            id = "ru.hh.plugins.gradle.setup_idea_plugin"
            implementationClass = "ru.hh.plugins.gradle.setup_idea_plugin.SetupIdeaPluginGradlePlugin"
            displayName = "Apply common settings to gradle-intellij-plugin"
        }
    }
}