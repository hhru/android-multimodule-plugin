package ru.hh.android.plugins.garcon.extensions

import com.intellij.ide.util.ClassFilter
import com.intellij.psi.util.InheritanceUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.android.plugins.garcon.Constants


fun kakaoScreensClassFilter(): ClassFilter {
    return ClassFilter { aClass ->
        return@ClassFilter if (aClass is KtLightClassForSourceDeclaration) {
            when (val classOrObject = aClass.kotlinOrigin) {
                is KtClass -> (classOrObject.isInner() || classOrObject.isAnnotation()).not()
                        && InheritanceUtil.isInheritor(aClass, Constants.KAKAO_SCREEN_CLASS_FQN)
                else -> false
            }
        } else {
            false
        }
    }
}