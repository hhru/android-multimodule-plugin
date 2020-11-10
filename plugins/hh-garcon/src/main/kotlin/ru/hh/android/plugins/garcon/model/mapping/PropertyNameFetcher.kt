package ru.hh.android.plugins.garcon.model.mapping

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.snakeToCamelCase
import ru.hh.android.plugins.garcon.model.page_object.PageObjectProperty


class PropertyNameFetcher : ProjectComponent {

    fun getPropertyName(item: PageObjectProperty): String {
        return getPropertyName(item.xmlFileName, item.id)
    }

    fun getPropertyName(fileName: String, viewId: String): String {
        val fileNameWithoutExtension = fileName.removeSuffix(".xml")
        val purifiedViewId = viewId
            .replace(fileNameWithoutExtension, String.EMPTY)
            .replace("__", "_")

        val viewClassPart = when {
            purifiedViewId.contains("button") -> "button"
            purifiedViewId.contains("recycler") -> "recycler"
            purifiedViewId.contains("edit_text") -> "edit_text"
            purifiedViewId.contains("text_view") -> "text_view"
            purifiedViewId.contains("image") -> "image"
            purifiedViewId.contains("container") -> "container"
            purifiedViewId.contains("view") -> "view"
            else -> String.EMPTY
        }
        if (viewClassPart.isEmpty()) {
            return viewId
        }
        val viewIdWithoutClassPrefix = purifiedViewId.replace(viewClassPart, String.EMPTY)
        val words = viewIdWithoutClassPrefix.split("_")
        val description = words.joinToString(separator = "_")
        if (description.isEmpty()) {
            return viewClassPart
        }

        return "${description}_${viewClassPart}".snakeToCamelCase().decapitalize()
    }

}