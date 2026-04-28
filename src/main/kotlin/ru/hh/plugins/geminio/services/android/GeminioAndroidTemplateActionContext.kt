package ru.hh.plugins.geminio.services.android

import com.android.tools.idea.model.AndroidModel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet

internal data class GeminioAndroidTemplateActionContext(
    val project: Project,
    val androidFacet: AndroidFacet,
)

/**
 * Returns `true` when the current action event points to a module with an initialized Android
 * project model, which is the minimum context needed for Geminio template execution.
 */
internal fun AnActionEvent.hasAvailableAndroidTemplateContext(): Boolean {
    val facet = findAndroidFacet() ?: return false
    return AndroidModel.get(facet) != null
}

/**
 * Resolves the Android action context or fails with a descriptive message if the action was
 * invoked outside an Android module.
 */
internal fun AnActionEvent.requireAndroidTemplateContext(): GeminioAndroidTemplateActionContext {
    val facet = requireNotNull(findAndroidFacet()) {
        "Geminio template action requires an Android module context"
    }

    return GeminioAndroidTemplateActionContext(
        project = requireNotNull(project),
        androidFacet = facet,
    )
}

private fun AnActionEvent.findAndroidFacet(): AndroidFacet? {
    val module = LangDataKeys.MODULE.getData(dataContext) ?: return null
    return AndroidFacet.getInstance(module)
}
