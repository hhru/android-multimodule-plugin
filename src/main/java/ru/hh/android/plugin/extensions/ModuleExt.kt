package ru.hh.android.plugin.extensions

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import ru.hh.android.plugin.CodeGeneratorConstants.BUILD_GRADLE_FILE_NAME


const val GRADLE_KEYWORD_APPLY = "apply"
const val GRADLE_KEYWORD_PLUGIN = "plugin"

const val PLUGIN_ANDROID_LIBRARY_NAME = "com.android.library"
const val PLUGIN_JAVA_LIBRARY = "java-library"


fun Module.isLibraryModule(): Boolean {
    return FilenameIndex.getFilesByName(
        project,
        BUILD_GRADLE_FILE_NAME,
        moduleContentScope
    ).firstOrNull()?.let { buildGradlePsiFile ->
        return buildGradlePsiFile.children.any { psiElement ->
            val text = psiElement.text

            text.contains(GRADLE_KEYWORD_APPLY)
                && text.contains(GRADLE_KEYWORD_PLUGIN)
                && (text.contains(PLUGIN_ANDROID_LIBRARY_NAME) || text.contains(PLUGIN_JAVA_LIBRARY))
        }

    } ?: false
}

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