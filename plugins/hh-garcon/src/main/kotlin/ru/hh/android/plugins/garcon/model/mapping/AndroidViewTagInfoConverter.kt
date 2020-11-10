package ru.hh.android.plugins.garcon.model.mapping

import com.intellij.openapi.components.ProjectComponent
import com.intellij.psi.PsiClass
import com.intellij.psi.util.InheritanceUtil
import ru.hh.android.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.android.plugins.garcon.model.KakaoViewDeclaration
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty


class AndroidViewTagInfoConverter : ProjectComponent {

    fun convert(item: AndroidViewTagInfo): PageObjectProperty {
        return PageObjectProperty(
            id = item.id,
            xmlFileName = item.xmlFieName,
            kakaoViewDeclaration = item.tagPsiClass.findClosestKakaoClassDeclaration()
        )
    }


    private fun PsiClass.findClosestKakaoClassDeclaration(): KakaoViewDeclaration {
        val firstCheckDeclaration = KakaoViewDeclaration.values()
            .filter { it != KakaoViewDeclaration.VIEW && it != KakaoViewDeclaration.TEXT_VIEW }
            .firstOrNull { this.isInheritor(it) }

        return when {
            firstCheckDeclaration != null -> firstCheckDeclaration
            this.isInheritor(KakaoViewDeclaration.TEXT_VIEW) -> KakaoViewDeclaration.TEXT_VIEW
            else -> KakaoViewDeclaration.VIEW
        }
    }

    private fun PsiClass.isInheritor(kakaoViewDeclaration: KakaoViewDeclaration): Boolean {
        for (androidViewClassName in kakaoViewDeclaration.androidWidgetsClasses) {
            if (InheritanceUtil.isInheritor(this, androidViewClassName)) {
                return true
            }

        }

        return false
    }

}