plugins {
    id("convention.idea-plugin-library")
}

dependencies {
    intellijPlatform {
        bundledPlugins("org.intellij.groovy")
    }
    implementation(project(":shared:core:models"))
    implementation(project(":shared:core:utils"))
}
