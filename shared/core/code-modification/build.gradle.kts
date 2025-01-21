plugins {
    id("convention.idea-plugin-library")
}

dependencies {
    intellijPlatform.bundledPlugin("org.intellij.groovy")
    implementation(project(":shared:core:utils"))
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:psi-utils"))
    implementation(project(":shared:core:logger"))
}
