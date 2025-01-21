plugins {
    id("convention.idea-plugin")
}

intellijPlatform {
    pluginConfiguration {
        id = "ru.hh.plugins.Carnival"
        name = "Carnival"
    }
}

dependencies {
    intellijPlatform {
        bundledPlugins(
            "Git4Idea",
            "org.intellij.groovy",
            "org.intellij.intelliLang",
        )
    }

    // Core modules
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:code-modification"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))
    implementation(project(":shared:core:logger"))
    implementation(project(":shared:core:notifications"))

    // Libraries
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0") {
        exclude(group = "org.slf4j")
        dependencies {
            implementation("com.atlassian.fugue:fugue:2.6.1")
        }
    }
}
