import java.net.URI

plugins {
    id("org.jetbrains.intellij") version Versions.intellijPlugin
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven {
        url = URI.create("https://packages.atlassian.com/maven/repository/public")
    }
}

dependencies {
    implementation(project(":hh-plugins-core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("commons-io:commons-io:2.4")
    implementation("com.vladsch.flexmark:flexmark-all:0.50.42")
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0") {
        exclude(group = "org.slf4j")
        dependencies {
            implementation("com.atlassian.fugue:fugue:2.6.1")
        }
    }
}

// region Setup gradle-intellij-plugin
val currentVersion = Versions.chosenProduct

intellij {
    type = "IC"
    if (currentVersion.isLocal) {
        localPath = currentVersion.ideVersion
    } else {
        version = currentVersion.ideVersion
    }
    setPlugins(*currentVersion.pluginsNames.toTypedArray())
}
// endregion