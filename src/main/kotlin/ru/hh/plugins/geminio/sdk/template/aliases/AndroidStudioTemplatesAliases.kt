package ru.hh.plugins.geminio.sdk.template.aliases

// templates
internal typealias AndroidStudioTemplate = com.android.tools.idea.wizard.template.Template
internal typealias AndroidStudioTemplateBuilder = com.android.tools.idea.wizard.template.TemplateBuilder

// template enums
internal typealias AndroidStudioTemplateCategory = com.android.tools.idea.wizard.template.Category
internal typealias AndroidStudioTemplateScreen = com.android.tools.idea.wizard.template.WizardUiContext
internal typealias AndroidStudioTemplateConstraint = com.android.tools.idea.wizard.template.TemplateConstraint
internal typealias AndroidStudioTemplateFormFactor = com.android.tools.idea.wizard.template.FormFactor

// template parameters
internal typealias AndroidStudioTemplateParameter = com.android.tools.idea.wizard.template.Parameter<*>
internal typealias AndroidStudioTemplateBooleanParameter = com.android.tools.idea.wizard.template.BooleanParameter
internal typealias AndroidStudioTemplateStringParameter = com.android.tools.idea.wizard.template.StringParameter

internal typealias AndroidStudioTemplateStringParameterConstraint = com.android.tools.idea.wizard.template.Constraint

internal typealias AndroidStudioTemplateParameterBooleanLambda = (
    com.android.tools.idea.wizard.template.WizardParameterData
) -> Boolean
internal typealias AndroidStudioTemplateParameterStringLambda = (
    com.android.tools.idea.wizard.template.WizardParameterData
) -> String?
