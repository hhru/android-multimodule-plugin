package ru.hh.plugins.geminio.models

import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep


data class GeminioConfigureTemplateStepModel(
    val renderTemplateModel: RenderTemplateModel,
    val configureTemplateParametersStep: ConfigureTemplateParametersStep
)