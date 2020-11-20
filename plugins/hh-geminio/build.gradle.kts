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
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)

    testImplementation("io.kotest:kotest-runner-junit5:4.3.1") // for kotest framework
//    testImplementation("io.kotest:kotest-assertions-core:<version>") // for kotest core jvm assertions
//    testImplementation("io.kotest:kotest-property:<version>") // for kotest property test
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
    setPlugins("java", "Kotlin", "android")
}
// endregion

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
