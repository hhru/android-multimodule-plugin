package ru.hh.android.plugin.extensions

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory

fun Module.findPsiFileByName(name: String): PsiFile? {
    return FilenameIndex.getFilesByName(project, name, moduleContentScope).firstOrNull()
}

fun Module.findClassesAnnotatedWith(annotationFullQualifiedName: String): MutableCollection<PsiMember>? {
    val psiManager = PsiManager.getInstance(project)

    return ClassUtil.findPsiClass(psiManager, annotationFullQualifiedName)?.let { psiClass ->
        AnnotatedMembersSearch.search(psiClass, moduleContentScope).findAll()
    }
}

/**
 * Imagine module structure like this:
 *
 * /parentFolder                !!! <-- parent for parent for moduleFile !!!
 *      /my-module-name         <-- root for moduleFile
 *          my-module-name.iml  <-- moduleFile
 */
val Module.moduleParentPsiDirectory: PsiDirectory?
    get() = moduleFile?.parent?.parent?.toPsiDirectory(project)

/**
 * Imagine module structure like this:
 *
 * /parentFolder                <-- parent for parent for moduleFile
 *      /my-module-name         !!! <-- root for moduleFile !!!
 *          my-module-name.iml  <-- moduleFile
 */
val Module.rootPsiDirectory: PsiDirectory?
    get() = moduleFile?.parent?.toPsiDirectory(project)

val Module.relativePathToParent: String
    get() = ".${this.moduleFile?.parent?.parent?.path?.removePrefix("${this.project.basePath}")}"
