package ru.hh.plugins.garcon.services

import android.databinding.tool.ext.toCamelCase
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.collections.firstOrNull
import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import ru.hh.plugins.garcon.model.AndroidViewTagInfo
import ru.hh.plugins.garcon.model.PageObjectPropertyParams
import ru.hh.plugins.garcon.model.extensions.rFilePackageName
import ru.hh.plugins.garcon.model.extensions.xmlFileName
import ru.hh.plugins.psi_utils.isInheritedFrom

@Service(Service.Level.PROJECT)
class PageObjectPropertyConverter(
    private val project: Project
) {

    companion object {

        /**
         * Simple map for storing mapping of tags PsiClasses to special widgets description.
         */
        private val classesMap = mutableMapOf<PsiClass, GarconPluginConfig.WidgetDescription>()

        fun getInstance(project: Project): PageObjectPropertyConverter = project.service()
    }

    fun convert(item: AndroidViewTagInfo): String {
        val pluginConfig = GarconPluginSettings.getConfig(project)
        val widgetDescription = item.toWidgetDescription(pluginConfig)

        val params = PageObjectPropertyParams(
            viewIdDeclaration = "${item.rFilePackageName}.id.${item.id}",
            propertyName = item.toPropertyName(widgetDescription),
            kakaoClassFQN = widgetDescription.kakaoWidgetFQN
        )

        return when {
            item.isRecyclerViewWidget() -> getRecyclerViewWidgetDeclaration(params)
            else -> getAndroidWidgetDeclaration(params)
        }
    }

    private fun getRecyclerViewWidgetDeclaration(params: PageObjectPropertyParams): String {
        return with(params) {
            """
            private val $propertyName = $kakaoClassFQN({ withId($viewIdDeclaration) }) {
                // TODO - add KRecyclerItem declarations   
            }    
            """.trimMargin()
        }
    }

    private fun getAndroidWidgetDeclaration(params: PageObjectPropertyParams): String {
        return with(params) {
            """
            private val $propertyName = $kakaoClassFQN { withId($viewIdDeclaration) }    
            """.trimMargin()
        }
    }

    private fun AndroidViewTagInfo.toWidgetDescription(
        pluginConfig: GarconPluginConfig
    ): GarconPluginConfig.WidgetDescription {
        val mapValue = classesMap[tagPsiClass]
        if (mapValue != null) {
            return mapValue
        }

        val configMap = pluginConfig.widgetsClassesMap

        val firstCheckDeclaration = configMap[requireNotNull(tagPsiClass.qualifiedName)]
        if (firstCheckDeclaration != null) {
            classesMap[tagPsiClass] = firstCheckDeclaration
            return firstCheckDeclaration
        }

        val secondCheckDeclaration = configMap.firstOrNull { tagPsiClass.isInheritedFrom(it.key) }

        return when {
            secondCheckDeclaration != null -> secondCheckDeclaration.value
            else -> requireNotNull(configMap["android.view.View"])
        }.also { classesMap[tagPsiClass] = it }
    }

    private fun AndroidViewTagInfo.toPropertyName(widgetDescription: GarconPluginConfig.WidgetDescription): String {
        val idSuffixes = widgetDescription.idSuffixes

        val layoutName = xmlFileName.removeSuffix(PluginsConstants.XML_FILE_EXTENSION)

        val purifiedViewId = id
            .replace(layoutName, String.EMPTY)
            .replace("__", "_")
            .run {
                var result = this
                idSuffixes.forEach { result = result.replace(it, String.EMPTY) }
                result
            }

        return "${purifiedViewId}_${idSuffixes.first()}".toCamelCase().replaceFirstChar { it.uppercaseChar() }
    }

    private fun AndroidViewTagInfo.isRecyclerViewWidget(): Boolean {
        return tagPsiClass.qualifiedName == "androidx.recyclerview.widget.RecyclerView"
    }
}
