package ru.hh.plugins.geminio.sdk.template.mapping

import com.android.tools.idea.wizard.template.template
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.sdk.template.mapping.optional.injectOptionalParams
import ru.hh.plugins.geminio.sdk.template.mapping.required.injectRequiredParams
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.injectWidgets


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate].
 */
internal fun GeminioRecipe.toAndroidStudioTemplate(): AndroidStudioTemplate {
    val recipe = this

    return template {
        injectRequiredParams(recipe)
        injectOptionalParams(recipe)

        val existingParametersMap = injectWidgets(recipe)
    }
}