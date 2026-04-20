package ru.hh.plugins.extensions.openapi

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import ru.hh.plugins.logger.HHLogger

fun Module.findPsiFileByName(name: String): PsiFile? {
    val (time, result) = measureTimeMillisWithResult {
        FilenameIndex.getFilesByName(project, name, moduleContentScope).firstOrNull()
    }
    HHLogger.d("Searching for `$name` in ${this.name} content scope consumed $time ms")

    return result
}

fun Module.findClassesAnnotatedWith(annotationFullQualifiedName: String): Collection<PsiMember>? {
    val psiManager = PsiManager.getInstance(project)

    return ClassUtil.findPsiClass(psiManager, annotationFullQualifiedName)?.let { psiClass ->
        AnnotatedMembersSearch.search(psiClass, moduleContentScope).findAll()
    }
}
