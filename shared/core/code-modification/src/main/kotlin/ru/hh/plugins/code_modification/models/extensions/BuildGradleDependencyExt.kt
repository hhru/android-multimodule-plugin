package ru.hh.plugins.code_modification.models.extensions

import ru.hh.plugins.code_modification.models.BuildGradleDependency


fun BuildGradleDependency.toDependencyText(): String {
    return when (this) {
        is BuildGradleDependency.MavenArtifact -> "\"${value}\""
        is BuildGradleDependency.Project -> "project(\":${value.removePrefix(":")}\")"
        is BuildGradleDependency.LibsConstant -> value
    }
}