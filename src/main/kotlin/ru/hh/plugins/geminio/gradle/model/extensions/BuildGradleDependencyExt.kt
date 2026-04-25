package ru.hh.plugins.geminio.gradle.model.extensions

import ru.hh.plugins.geminio.gradle.model.BuildGradleDependency

fun BuildGradleDependency.toDependencyText(): String {
    return when (this) {
        is BuildGradleDependency.MavenArtifact -> "\"${value}\""
        is BuildGradleDependency.Project -> "project(\":${value.removePrefix(":")}\")"
        is BuildGradleDependency.LibsConstant -> value
    }
}
