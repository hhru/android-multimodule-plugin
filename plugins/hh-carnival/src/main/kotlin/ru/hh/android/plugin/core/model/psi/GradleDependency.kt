package ru.hh.android.plugin.core.model.psi


data class GradleDependency(
    val text: String,
    val type: GradleDependencyType,
    val mode: GradleDependencyMode
)