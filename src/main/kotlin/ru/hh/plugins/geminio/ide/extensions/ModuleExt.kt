package ru.hh.plugins.geminio.ide.extensions

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import ru.hh.plugins.geminio.logger.HHLogger

fun Module.findPsiFileByName(name: String): PsiFile? {
    val (time, result) = measureTimeMillisWithResult {
        FilenameIndex.getFilesByName(project, name, moduleContentScope).firstOrNull()
    }
    HHLogger.d("Searching for `$name` in ${this.name} content scope consumed $time ms")

    return result
}
