plugins {
    id(GradlePlugins.gradleIntelliJPlugin)
    kotlin("jvm")
    id(GradlePlugins.setupIdeaPlugin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:freemarker"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)
}
