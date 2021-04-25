plugins {
    id("convention.idea-plugin-library")
}

dependencies {
    implementation(project(":shared:core:utils"))

    implementation(kotlin("stdlib-jdk8"))
}