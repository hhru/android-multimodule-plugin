rootProject.name = "build-logic"

includeBuild("../libraries")

include("kotlin-convention")
include("testing-convention")
include("static-analysis-convention")
include("idea-convention")

include(":gradle:core-utils")
include(":gradle:build-all-plugins")
include(":gradle:collect-update-plugins")


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