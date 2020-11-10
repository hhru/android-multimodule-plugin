plugins {
    id("org.jetbrains.intellij") version Versions.intellijPlugin
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":hh-plugins-core"))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(Libs.junitJupiterApi)
    testImplementation(Libs.junitJupiterParams)
    testRuntimeOnly(Libs.junitJupiterEngine)
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

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}