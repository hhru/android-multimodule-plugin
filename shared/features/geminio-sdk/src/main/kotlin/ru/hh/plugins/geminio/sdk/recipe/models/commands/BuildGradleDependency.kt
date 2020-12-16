package ru.hh.plugins.geminio.sdk.recipe.models.commands


/**
 * Dependency for build.gradle files
 */
sealed class BuildGradleDependency {

    abstract val value: String
    abstract val configuration: BuildGradleDependencyConfiguration


    /**
     * Dependency in Maven's notation, e.g. `org.company:artifact:123`
     */
    data class MavenArtifact(
        override val value: String,
        override val configuration: BuildGradleDependencyConfiguration
    ) : BuildGradleDependency()

    /**
     * Dependency on existing module (project), e.g. `project(":shared-core-model")`
     */
    data class Project(
        override val value: String,
        override val configuration: BuildGradleDependencyConfiguration
    ) : BuildGradleDependency()

    /**
     * Dependency from internal constants, e.g. `Libs.jetpack.viewmodel`.
     */
    data class LibsConstant(
        override val value: String,
        override val configuration: BuildGradleDependencyConfiguration
    ) : BuildGradleDependency()

}