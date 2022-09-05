package ru.hh.plugins.code_modification.utils

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import ru.hh.plugins.code_modification.GradleConstants
import ru.hh.plugins.extensions.openapi.findPsiFileByName
import ru.hh.plugins.logger.HHLogger


/**
 * Try to find Groovy or KTS variant of your file in the module content scope or in project scope.
 * Search gradle files in the following order:
 *
 * - Try to find Groovy variant ({filename}.gradle) in module scope
 * - Try to find KTS variant ({filename}.gradle.kts) in module scope
 * - Try to find Groovy variant in project scope
 * - Try to find KTS variant in project scope
 *
 * If you pass KTS file as `filename`, method will skip searching Groovy variants.
 *
 * @param filename - filename of your gradle file with extension (e.g. "build.gradle" / "settings.gradle.kts" / etc)
 */
internal fun Module.searchGradlePsiFile(filename: String): PsiFile? {
    when (filename.split(".").lastOrNull()) {
        GradleConstants.GROOVY_EXTENSION -> {
            val ktsFilename = "${filename}.${GradleConstants.KTS_EXTENSION}"

            val moduleGroovyFile = this.findPsiFileByName(filename)
            if (moduleGroovyFile != null) {
                return moduleGroovyFile
            }

            HHLogger.d("searchGradlePsiFile -> didn't find $filename in ${this.name} --> try to find KTS variant")
            val moduleKtsFile = this.findPsiFileByName(ktsFilename)
            if (moduleKtsFile != null) {
                return moduleKtsFile
            }

            /**
             * IMPORTANT!
             *
             * From Android Studio Chipmunk Patch 2 sometimes searching in module content scope is not enough, so we
             * expand searching through module's project - it can be time-consuming, so be patient.
             */
            HHLogger.d(
                "searchGradlePsiFile -> didn't find $ktsFilename in $${this.name} --> try to search in project scope"
            )
            val projectGroovyFile = this.findPsiFileInProjectScope(filename)
            if (projectGroovyFile != null) {
                return projectGroovyFile
            }

            HHLogger.d("searchGradlePsiFile -> didn't find $filename in project --> try to find KTS variant")
            val projectKtsFile = this.findPsiFileInProjectScope(ktsFilename)
            if (projectKtsFile == null) {
                HHLogger.d("searchGradlePsiFile -> didn't find $ktsFilename in project")
            }

            return projectKtsFile
        }

        GradleConstants.KTS_EXTENSION -> {
            val moduleKtsFile = this.findPsiFileByName(filename)
            if (moduleKtsFile != null) {
                return moduleKtsFile
            }

            /**
             * IMPORTANT!
             *
             * From Android Studio Chipmunk Patch 2 sometimes searching in module content scope is not enough, so we
             * expand searching through module's project - it can be time-consuming, so be patient.
             */
            HHLogger.d("searchGradlePsiFile -> didn't find $filename in ${this.name} --> try to search in project")
            val projectKtsFile = this.findPsiFileInProjectScope(filename)
            if (projectKtsFile == null) {
                HHLogger.d("searchGradlePsiFile -> didn't find $filename in project")
            }
            return projectKtsFile
        }

        else -> {
            throw IllegalArgumentException(
                "Wrong file extension for method `Module.searchGradlePsiFile`, " +
                        "expected `${GradleConstants.GROOVY_EXTENSION}` or `${GradleConstants.KTS_EXTENSION}"
            )
        }
    }
}

/**
 * Try to search PSI file in module's project scope.
 * This method use [FilenameIndex.getAllFilesByExt] method then search first item with module path.
 *
 * @param filename - name of your file with extension
 *  (e.g. "build.gradle" -> extension == "gradle", "settings.gradle.kts" -> extension == "kts", etc)
 */
private fun Module.findPsiFileInProjectScope(filename: String): PsiFile? {
    val extension = filename.split(".").last()
    val targetFilePath = "${this.name}/${filename}"

    val (time, result) = measureTimeMillisWithResult {
        FilenameIndex.getAllFilesByExt(project, extension)
            .firstOrNull { it.path.endsWith(targetFilePath) }
            ?.toPsiFile(project)
    }
    HHLogger.d("Searching in project scope for `${targetFilePath}` consumed $time ms")

    return result
}