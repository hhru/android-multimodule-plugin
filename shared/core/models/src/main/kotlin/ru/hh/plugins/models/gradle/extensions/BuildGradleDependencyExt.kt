package ru.hh.plugins.models.gradle.extensions

import ru.hh.plugins.models.gradle.BuildGradleDependency


fun BuildGradleDependency.toDependencyText(): String {
    return when (this) {
        is BuildGradleDependency.MavenArtifact -> "\"${value}\""
        is BuildGradleDependency.Project -> "project(\":${value.removePrefix(":")}\")"
        is BuildGradleDependency.LibsConstant -> value
    }
}