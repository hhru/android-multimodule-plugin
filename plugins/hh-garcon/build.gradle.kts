plugins {
    id("convention.idea-plugin")
}

// TODO [build-logic] Look with a fresh eye, why this needs to be duplicated, if there is common dependency resolution in settings.gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared:core:ui"))
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:freemarker"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(Libs.freemarker)
}
