package ru.hh.plugins.geminio.model.temp_data

import com.android.tools.idea.wizard.template.ModuleTemplateData
import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.utils.freemarker.FreemarkerConfiguration


data class GeminioRecipeExecutorData(
    val moduleTemplateData: ModuleTemplateData,
    val existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    val resolvedParamsMap: Map<String, Any?>,
    val freemarkerConfiguration: FreemarkerConfiguration
)