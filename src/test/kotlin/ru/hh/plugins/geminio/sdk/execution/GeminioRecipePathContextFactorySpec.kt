package ru.hh.plugins.geminio.sdk.execution

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.services.android.GeminioAndroidPathContextFactory

internal class GeminioRecipePathContextFactorySpec : FreeSpec({

    "should build new-module path aliases from source set configuration" {
        val pathContext = GeminioAndroidPathContextFactory.createForNewModule(
            request = GeminioAndroidPathContextFactory.NewModulePathRequest(
                currentDirPath = "/workspace/features",
                newModuleRootDirectoryPath = "/workspace/features",
                moduleName = "payments",
                packageName = "ru.hh.payments",
                sourceSet = "main",
                sourceCodeFolderName = "kotlin",
            ),
        )

        pathContext.srcOut shouldBe "/workspace/features/payments/src/main/kotlin/ru/hh/payments"
        pathContext.resOut shouldBe "/workspace/features/payments/src/main/res"
        pathContext.manifestOut shouldBe "/workspace/features/payments/src/main"
        pathContext.rootOut shouldBe "/workspace/features/payments"
        pathContext.currentDirOut shouldBe "/workspace/features"
    }

    "should respect custom source set and source code folder names" {
        val pathContext = GeminioAndroidPathContextFactory.createForNewModule(
            request = GeminioAndroidPathContextFactory.NewModulePathRequest(
                currentDirPath = "/workspace/modules",
                newModuleRootDirectoryPath = "/workspace/modules",
                moduleName = "reports",
                packageName = "ru.hh.reports.internal",
                sourceSet = "qa",
                sourceCodeFolderName = "java",
            ),
        )

        pathContext.srcOut shouldBe "/workspace/modules/reports/src/qa/java/ru/hh/reports/internal"
        pathContext.resOut shouldBe "/workspace/modules/reports/src/qa/res"
        pathContext.manifestOut shouldBe "/workspace/modules/reports/src/qa"
        pathContext.rootOut shouldBe "/workspace/modules/reports"
        pathContext.currentDirOut shouldBe "/workspace/modules"
    }
})
