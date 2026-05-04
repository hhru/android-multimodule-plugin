package ru.hh.plugins.geminio.actions.module_template

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files

internal class ExecuteGeminioModuleTemplateActionSpec : FreeSpec({

    "should build relative module path when module is created in project root" {
        withTempDirectory { projectRoot ->
            createNewModuleRelativePath(
                projectBasePath = projectRoot.toString(),
                directoryPath = projectRoot.toString(),
                moduleName = "applicant:core",
            ) shouldBe "applicant/core"
        }
    }

    "should build relative module path when module is created in nested directory" {
        withTempDirectory { projectRoot ->
            val selectedDirectory = projectRoot.resolve("features")

            createNewModuleRelativePath(
                projectBasePath = projectRoot.toString(),
                directoryPath = selectedDirectory.toString(),
                moduleName = "applicant:feature:part-time",
            ) shouldBe "features/applicant/feature/part-time"
        }
    }
})

private fun withTempDirectory(action: (java.nio.file.Path) -> Unit) {
    val root = Files.createTempDirectory("geminio-module-action")
    try {
        action(root)
    } finally {
        root.toFile().deleteRecursively()
    }
}
