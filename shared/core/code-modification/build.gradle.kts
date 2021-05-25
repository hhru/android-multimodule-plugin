plugins {
    id("convention.idea-plugin-library")
}

// TODO [build-logic] Look with a fresh eye, why this needs to be duplicated, if there is common dependency resolution in settings.gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))
}
