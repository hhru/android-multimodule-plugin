package ru.hh.plugins.extensions.openapi

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.android.dom.manifest.cachedValueFromPrimaryManifest
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrCallExpression


private const val BUILD_GRADLE_FILENAME = "build.gradle"

private const val GRADLE_KEYWORD_APPLY = "apply"
private const val GRADLE_KEYWORD_PLUGIN = "plugin"

private const val PLUGIN_ANDROID_LIBRARY_NAME = "com.android.library"
private const val PLUGIN_JAVA_LIBRARY = "java-library"
private const val PLUGIN_ANDROID_APP = "com.android.application"


fun Module.isLibraryModule(): Boolean {
    return findPsiFileByName(BUILD_GRADLE_FILENAME)?.let { buildGradlePsiFile ->
        return buildGradlePsiFile.collectDescendantsOfType<GrCallExpression>()
            .filter { it.text.contains(GRADLE_KEYWORD_APPLY) && it.text.contains(GRADLE_KEYWORD_PLUGIN) }
            .any { it.text.contains(PLUGIN_ANDROID_LIBRARY_NAME) || it.text.contains(PLUGIN_JAVA_LIBRARY) }
    } ?: false
}

fun Module.isAppModule(): Boolean {
    return findPsiFileByName(BUILD_GRADLE_FILENAME)?.let { buildGradlePsiFile ->
        return buildGradlePsiFile.collectDescendantsOfType<GrCallExpression>()
            .filter { it.text.contains(GRADLE_KEYWORD_APPLY) && it.text.contains(GRADLE_KEYWORD_PLUGIN) }
            .any { it.text.contains(PLUGIN_ANDROID_APP) }
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

val Module.androidModulePackageName: String?
    get() {
        return this.androidFacet?.cachedValueFromPrimaryManifest { this.packageName }?.value
    }