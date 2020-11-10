package ru.hh.android.plugins.garcon.model.mapping

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugins.garcon.extensions.base_types.getClassNameFromFQN
import ru.hh.android.plugins.garcon.model.KakaoViewDeclaration
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty


class PageObjectPropertyConverter(
    private val propertyNameFetcher: PropertyNameFetcher
) : ProjectComponent {

    fun convert(item: PageObjectProperty): String {
        return when (item.kakaoViewDeclaration) {
            KakaoViewDeclaration.RECYCLER_VIEW -> getKRecyclerViewPropertyDeclaration(item)
            else -> getKViewPropertyDeclaration(item)
        }
    }


    private fun getKRecyclerViewPropertyDeclaration(item: PageObjectProperty): String {
        val kViewClassName = item.kakaoViewDeclaration.fqn.getClassNameFromFQN()
        val propertyName = propertyNameFetcher.getPropertyName(item)

        return """
        private val $propertyName = $kViewClassName({ withId(R.id.${item.id}) }) {
            // TODO - add KRecyclerItem declarations   
        }
        """.trimMargin()
    }

    private fun getKViewPropertyDeclaration(item: PageObjectProperty): String {
        val kViewClassName = item.kakaoViewDeclaration.fqn.getClassNameFromFQN()
        val propertyName = propertyNameFetcher.getPropertyName(item)

        return """
        private val $propertyName = $kViewClassName { withId(R.id.${item.id}) }    
        """.trimMargin()
    }

}