package ru.hh.plugins.model


/**
 * Dependency for build.gradle files
 */
sealed class BuildGradleDependency {

    abstract val type: BuildGradleDependencyType

    /**
     * Dependency in Maven's notation, e.g. `org.company:artifact:123`
     */
    data class MavenArtifact(
        val notation: String,
        override val type: BuildGradleDependencyType = BuildGradleDependencyType.COMPILE_ONLY
    ) : BuildGradleDependency()

    /**
     * Dependency on existing module (project), e.g. `project(":shared-core-model")`
     */
    data class Project(
        val projectName: String,
        override val type: BuildGradleDependencyType = BuildGradleDependencyType.COMPILE_ONLY
    ) : BuildGradleDependency()

    /**
     * Dependency from internal constants, e.g. `Libs.jetpack.viewmodel`, or `org.company:artifact:123`
     */
    data class LibsConstant(
        val constant: String,
        override val type: BuildGradleDependencyType = BuildGradleDependencyType.COMPILE_ONLY
    ) : BuildGradleDependency()

}