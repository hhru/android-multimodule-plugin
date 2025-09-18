package ru.hh.plugins.extensions.openapi

import com.android.tools.idea.projectsystem.gradle.getHolderModule
import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.module.Module
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMember
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.kotlin.utils.addToStdlib.measureTimeMillisWithResult
import ru.hh.plugins.logger.HHLogger

fun Module.isAndroidLibraryModule(): Boolean {
    return androidFacet?.configuration?.isLibraryProject ?: false
}

fun Module.isAndroidAppModule(): Boolean {
    val isAppProject = androidFacet?.configuration?.isAppProject ?: false

    /**
     * Starting from `Android Studio Chipmunk 2021.2.1 Patch 2` in the list of
     * application modules we see not only application modules but also "submodules" of these applications, e.g.:
     *
     * - 'headhunter-applicant.unitTests'
     * - 'headhunter-applicant.androidTest'
     *
     * etc.
     *
     * To remove these submodules we add this condition.
     */
    val isHolderModule = this == androidFacet?.module?.getHolderModule()

    return isAppProject && isHolderModule
}

fun Module.findPsiFileByName(name: String): PsiFile? {
    val (time, result) = measureTimeMillisWithResult {
        FilenameIndex.getFilesByName(project, name, moduleContentScope).firstOrNull()
    }
    HHLogger.d("Searching for `$name` in ${this.name} content scope consumed $time ms")

    return result
}

fun Module.findClassesAnnotatedWith(annotationFullQualifiedName: String): MutableCollection<PsiMember>? {
    val psiManager = PsiManager.getInstance(project)

    return ClassUtil.findPsiClass(psiManager, annotationFullQualifiedName)?.let { psiClass ->
        AnnotatedMembersSearch.search(psiClass, moduleContentScope).findAll()
    }
}
