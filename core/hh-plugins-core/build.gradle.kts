plugins {
    id("org.jetbrains.intellij") version Versions.intellijPlugin
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.freemarker)
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