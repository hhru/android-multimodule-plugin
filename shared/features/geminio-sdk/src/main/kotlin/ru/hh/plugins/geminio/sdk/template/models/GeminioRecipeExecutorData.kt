package ru.hh.plugins.geminio.sdk.template.models

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.intellij.openapi.project.Project
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


data class GeminioRecipeExecutorData(
    val project: Project,
    val isDryRun: Boolean,
    val moduleTemplateData: ModuleTemplateData,
    val existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    val resolvedParamsMap: Map<String, Any?>,
    val freemarkerConfiguration: FreemarkerConfiguration
)