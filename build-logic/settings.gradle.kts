rootProject.name = "build-logic"

include("kotlin-convention")
include("testing-convention")
include("idea-convention")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}