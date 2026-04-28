package ru.hh.plugins.geminio.sdk.form

import ru.hh.plugins.extensions.toFormattedModuleName
import ru.hh.plugins.extensions.toPackageNameFromModuleName
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_SOURCE_SET_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.GLOBALS_SHOW_HIDDEN_VALUES_ID
import ru.hh.plugins.geminio.sdk.form.expressions.toBooleanEvaluator
import ru.hh.plugins.geminio.sdk.form.expressions.toStringEvaluator
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint

/**
 * Builds a pure Geminio form model from recipe data.
 *
 * The resulting form intentionally keeps only runtime semantics needed by our future custom UI:
 * predefined module params, user widgets and globals. `optionalParams` are ignored here because
 * they belong to the old Android template catalog metadata rather than to form rendering.
 */
internal fun GeminioRecipe.toGeminioForm(): GeminioForm {
    val fields = mutableListOf<GeminioFormField>()

    predefinedFeaturesSection.moduleCreationParameter()
        ?.let { moduleCreationParameter ->
            fields += createPredefinedModuleFields(moduleCreationParameter)
        }

    fields += widgetsSection.parameters.map { widgetParameter ->
        widgetParameter.toFormField()
    }

    if (globalsSection.parameters.isNotEmpty()) {
        fields += globalsSection.toShowHiddenGlobalsField(existingFieldIds = fields.map { it.id }.toSet())
        fields += globalsSection.parameters.map { globalsParameter ->
            globalsParameter.toFormField(showHiddenValuesId = GLOBALS_SHOW_HIDDEN_VALUES_ID)
        }
    }

    return GeminioForm(fields)
}

private fun PredefinedFeaturesSection.moduleCreationParameter(): PredefinedFeatureParameter.ModuleCreationParameter? {
    return features[PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS]
        as? PredefinedFeatureParameter.ModuleCreationParameter
}

private fun createPredefinedModuleFields(
    moduleCreationParameter: PredefinedFeatureParameter.ModuleCreationParameter,
): List<GeminioFormField> {
    return listOf(
        GeminioFormField.StringField(
            id = FEATURE_MODULE_NAME_PARAMETER_ID,
            name = "Module name",
            help = "The Gradle path of creating module without ':' prefix",
            origin = GeminioFormFieldOrigin.PREDEFINED,
            defaultValue = "mymodule",
            constraints = listOf(
                StringParameterConstraint.MODULE,
                StringParameterConstraint.UNIQUE,
            ),
        ),
        GeminioFormField.StringField(
            id = FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID,
            name = "Module's classes prefix",
            help = "Prefix for classes with module name",
            origin = GeminioFormFieldOrigin.PREDEFINED,
            defaultValue = "MyModule",
            suggestEvaluator = {
                stringValue(FEATURE_MODULE_NAME_PARAMETER_ID)?.toFormattedModuleName()
            },
        ),
        GeminioFormField.StringField(
            id = FEATURE_PACKAGE_NAME_PARAMETER_ID,
            name = "Package name",
            help = "Package name of creating module",
            origin = GeminioFormFieldOrigin.PREDEFINED,
            defaultValue = "${moduleCreationParameter.defaultPackageNamePrefix}.mymodule",
            suggestEvaluator = {
                stringValue(FEATURE_MODULE_NAME_PARAMETER_ID)
                    ?.toPackageNameFromModuleName(moduleCreationParameter.defaultPackageNamePrefix)
            },
            constraints = listOf(
                StringParameterConstraint.PACKAGE,
                StringParameterConstraint.UNIQUE,
            ),
        ),
        GeminioFormField.StringField(
            id = FEATURE_SOURCE_SET_PARAMETER_ID,
            name = "Source set",
            help = "Source set of classes",
            origin = GeminioFormFieldOrigin.PREDEFINED,
            defaultValue = moduleCreationParameter.defaultSourceSet,
            suggestEvaluator = { moduleCreationParameter.defaultSourceSet },
            constraints = listOf(
                StringParameterConstraint.SOURCE_SET_FOLDER,
                StringParameterConstraint.UNIQUE,
            ),
        ),
        GeminioFormField.StringField(
            id = FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID,
            name = "Code src folder name",
            help = "Code src folder name (kotlin/java)",
            origin = GeminioFormFieldOrigin.PREDEFINED,
            defaultValue = moduleCreationParameter.defaultSourceCodeFolderName,
        ),
    )
}

private fun RecipeParameter.toFormField(): GeminioFormField {
    // Enum widget support is intentionally deferred until Geminio recipes start exposing it
    // through the custom UI runtime.
    return when (this) {
        is RecipeParameter.StringParameter -> GeminioFormField.StringField(
            id = id,
            name = name,
            help = help,
            origin = GeminioFormFieldOrigin.WIDGET,
            visibilityEvaluator = visibilityExpression?.toBooleanEvaluator(),
            availabilityEvaluator = availabilityExpression?.toBooleanEvaluator(),
            defaultValue = default,
            suggestEvaluator = suggestExpression?.toStringEvaluator(),
            constraints = constraints,
        )

        is RecipeParameter.BooleanParameter -> GeminioFormField.BooleanField(
            id = id,
            name = name,
            help = help,
            origin = GeminioFormFieldOrigin.WIDGET,
            visibilityEvaluator = visibilityExpression?.toBooleanEvaluator(),
            availabilityEvaluator = availabilityExpression?.toBooleanEvaluator(),
            defaultValue = default,
        )
    }
}

private fun GlobalsSection.toShowHiddenGlobalsField(
    existingFieldIds: Set<String>,
): GeminioFormField.BooleanField {
    check(GLOBALS_SHOW_HIDDEN_VALUES_ID !in existingFieldIds) {
        "You cannot have template parameter with id='$GLOBALS_SHOW_HIDDEN_VALUES_ID' " +
            "with 'globals' section in your recipe.yaml. Rename your parameter from widgets section."
    }

    return GeminioFormField.BooleanField(
        id = GLOBALS_SHOW_HIDDEN_VALUES_ID,
        name = "Show hidden globals values",
        help = "Shows values of 'globals' section",
        origin = GeminioFormFieldOrigin.INTERNAL,
        defaultValue = false,
    )
}

private fun GlobalsSectionParameter.toFormField(
    showHiddenValuesId: String,
): GeminioFormField {
    return when (this) {
        is GlobalsSectionParameter.StringParameter -> GeminioFormField.StringField(
            id = id,
            name = id,
            help = id,
            origin = GeminioFormFieldOrigin.GLOBAL,
            visibilityEvaluator = { booleanValue(showHiddenValuesId) },
            availabilityEvaluator = { true },
            initialValueEvaluator = value.toStringEvaluator(),
            suggestEvaluator = value.toStringEvaluator(),
        )

        is GlobalsSectionParameter.BooleanParameter -> GeminioFormField.BooleanField(
            id = id,
            name = id,
            help = id,
            origin = GeminioFormFieldOrigin.GLOBAL,
            visibilityEvaluator = { booleanValue(showHiddenValuesId) },
            availabilityEvaluator = { true },
            initialValueEvaluator = value.toBooleanEvaluator(),
        )
    }
}
