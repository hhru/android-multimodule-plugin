package ru.hh.plugins.garcon.services

import com.intellij.ide.util.ClassFilter
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.util.InheritanceUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.plugins.garcon.GarconConstants

@Service
class ClassFiltersFactory {

    companion object {
        fun getInstance(project: Project) = project.service<ClassFiltersFactory>()
    }

    fun createKakaoScreensClassFilter(): ClassFilter {
        return ClassFilter { isAccepted(it) }
    }

    private fun isAccepted(aClass: PsiClass?): Boolean {
        return if (aClass is KtLightClassForSourceDeclaration) {
            when (val classOrObject = aClass.kotlinOrigin) {
                is KtClass -> (classOrObject.isInner() || classOrObject.isAnnotation()).not() &&
                    InheritanceUtil.isInheritor(aClass, GarconConstants.AGODA_SCREEN_CLASS_FQN)

                else -> false
            }
        } else {
            false
        }
    }
}
