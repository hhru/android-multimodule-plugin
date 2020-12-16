package ru.hh.plugins.gradle.install_git_hooks

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.task
import ru.hh.plugins.gradle.extensions.isRoot
import java.io.File


class InstallGitHooksGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        check(target.isRoot()) {
            "Plugin must be applied to the root project but was applied to ${target.path}"
        }

        target.task("installGitHooks", Copy::class) {
            val file = File(target.rootDir, "infra/git/prepare-commit-msg")
            println("is file exists: ${file.exists()}")

            from(file.absolutePath)
            into(File(target.rootDir, ".git/hooks").absolutePath)

            println("All git hooks have been installed.")
        }
    }

}