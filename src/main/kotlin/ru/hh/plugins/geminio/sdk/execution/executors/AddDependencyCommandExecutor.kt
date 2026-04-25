package ru.hh.plugins.geminio.sdk.execution.executors

import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import ru.hh.plugins.geminio.gradle.BuildGradleModificationService
import ru.hh.plugins.geminio.logger.HHLogger
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathAlias
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import java.io.File

/**
 * Executes the `addDependencies` recipe command in the single-pass Geminio runtime.
 *
 * The old Android template integration executed this during a dry run to avoid duplicate Gradle
 * mutations across two passes. The custom Geminio runtime has only one execution pass, so we
 * apply the Gradle changes directly here.
 */
internal fun RecipeCommand.AddDependencies.execute(
    context: GeminioRecipeEvaluationContext,
    request: GeminioRecipeExecutionRequest,
) {
    HHLogger.d("AddDependencies command [$this]")

    val rootDir = context.requireRootDirectory(request)

    BuildGradleModificationService.getInstance(request.project).addDepsInModuleDirectory(
        rootDir = rootDir,
        gradleDependencies = dependencies,
        isInWriteCommand = true,
    )
}

internal fun GeminioRecipeEvaluationContext.requireRootDirectory(
    request: GeminioRecipeExecutionRequest,
) = requireNotNull(getPath(GeminioFormPathAlias.ROOT_OUT)) {
    "Path '${GeminioFormPathAlias.ROOT_OUT}' is required for Gradle dependency modification"
}.let { rootPath ->
    LocalFileSystem.getInstance()
        .refreshAndFindFileByIoFile(File(rootPath))
        ?.toPsiDirectory(request.project)
}
