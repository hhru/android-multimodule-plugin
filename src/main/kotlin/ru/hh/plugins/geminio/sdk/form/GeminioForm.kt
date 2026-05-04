package ru.hh.plugins.geminio.sdk.form

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint

internal typealias GeminioFormStringEvaluator = GeminioFormEvaluationContext.() -> String?
internal typealias GeminioFormBooleanEvaluator = GeminioFormEvaluationContext.() -> Boolean

/**
 * Minimal read-only contract used by form evaluators.
 *
 * It deliberately exposes only current parameter values and path aliases so the runtime stays
 * independent of Android Studio template/wizard types.
 */
internal interface GeminioFormEvaluationContext {
    fun getValue(parameterId: String): Any?
    fun getPath(pathAlias: GeminioFormPathAlias): String?
}

internal fun GeminioFormEvaluationContext.stringValue(parameterId: String): String? {
    return getValue(parameterId) as? String
}

internal fun GeminioFormEvaluationContext.booleanValue(parameterId: String): Boolean {
    return getValue(parameterId) as? Boolean ?: false
}

/**
 * Path placeholders supported by Geminio expressions.
 *
 * These aliases mirror the old wizard/template runtime so the future custom execution layer can
 * reuse the same expression semantics.
 */
internal enum class GeminioFormPathAlias {
    SRC_OUT,
    RES_OUT,
    MANIFEST_OUT,
    ROOT_OUT,
    CURRENT_DIR_OUT,
}

/**
 * Concrete path values available for expression evaluation in a particular form session.
 */
internal data class GeminioFormPathContext(
    val srcOut: String? = null,
    val resOut: String? = null,
    val manifestOut: String? = null,
    val rootOut: String? = null,
    val currentDirOut: String? = null,
) {
    fun get(pathAlias: GeminioFormPathAlias): String? {
        return when (pathAlias) {
            GeminioFormPathAlias.SRC_OUT -> srcOut
            GeminioFormPathAlias.RES_OUT -> resOut
            GeminioFormPathAlias.MANIFEST_OUT -> manifestOut
            GeminioFormPathAlias.ROOT_OUT -> rootOut
            GeminioFormPathAlias.CURRENT_DIR_OUT -> currentDirOut
        }
    }
}

/**
 * Explains why a field exists in the generated form.
 *
 * This is useful when we later render different UI sections for user widgets, globals and
 * synthetic fields such as the "show hidden globals" toggle.
 */
internal enum class GeminioFormFieldOrigin {
    PREDEFINED,
    WIDGET,
    INTERNAL,
    GLOBAL,
}

/**
 * Pure runtime representation of the configuration form derived from a [GeminioRecipe].
 */
internal data class GeminioForm(
    val fields: List<GeminioFormField>,
) {
    init {
        require(fields.distinctBy { it.id }.size == fields.size) {
            "Form fields ids should be unique: ${fields.map { it.id }}"
        }
    }

    val fieldsById: Map<String, GeminioFormField> = fields.associateBy { it.id }

    fun requireField(fieldId: String): GeminioFormField {
        return fieldsById[fieldId]
            ?: throw IllegalArgumentException("Unknown form field with id='$fieldId'")
    }
}

/**
 * Declarative field description used by the custom Geminio form runtime.
 *
 * The field keeps only recipe semantics: labels, defaults, constraints and dynamic evaluators.
 * Rendering details stay outside of this model.
 */
internal sealed class GeminioFormField {

    abstract val id: String
    abstract val name: String
    abstract val help: String?
    abstract val origin: GeminioFormFieldOrigin
    abstract val visibilityEvaluator: GeminioFormBooleanEvaluator?
    abstract val availabilityEvaluator: GeminioFormBooleanEvaluator?

    /**
     * Text input field with optional `suggest`, constraints and dynamic visibility/enabled state.
     */
    data class StringField(
        override val id: String,
        override val name: String,
        override val help: String?,
        override val origin: GeminioFormFieldOrigin,
        override val visibilityEvaluator: GeminioFormBooleanEvaluator? = null,
        override val availabilityEvaluator: GeminioFormBooleanEvaluator? = null,
        val defaultValue: String? = null,
        val initialValueEvaluator: GeminioFormStringEvaluator? = null,
        val suggestEvaluator: GeminioFormStringEvaluator? = null,
        val constraints: List<StringParameterConstraint> = emptyList(),
    ) : GeminioFormField()

    /**
     * Boolean checkbox-like field with optional dynamic visibility/enabled state.
     */
    data class BooleanField(
        override val id: String,
        override val name: String,
        override val help: String?,
        override val origin: GeminioFormFieldOrigin,
        override val visibilityEvaluator: GeminioFormBooleanEvaluator? = null,
        override val availabilityEvaluator: GeminioFormBooleanEvaluator? = null,
        val defaultValue: Boolean? = null,
        val initialValueEvaluator: GeminioFormBooleanEvaluator? = null,
    ) : GeminioFormField()

    /**
     * Searchable completion field with optional closed-set validation.
     */
    data class SuggestField(
        override val id: String,
        override val name: String,
        override val help: String?,
        override val origin: GeminioFormFieldOrigin,
        override val visibilityEvaluator: GeminioFormBooleanEvaluator? = null,
        override val availabilityEvaluator: GeminioFormBooleanEvaluator? = null,
        val defaultValue: String? = null,
        val isSealed: Boolean = false,
        val initialValueEvaluator: GeminioFormStringEvaluator? = null,
        val suggestEvaluator: GeminioFormStringEvaluator? = null,
        val options: List<GeminioFormSuggestOption>,
    ) : GeminioFormField() {

        init {
            require(options.isNotEmpty()) {
                "Suggest field '$id' should contain at least one option."
            }
        }

        fun containsValue(value: String?): Boolean {
            return value != null && options.any { option -> option.value == value }
        }

        fun findOption(textOrValue: String?): GeminioFormSuggestOption? {
            return options.firstOrNull { option ->
                option.value == textOrValue || option.label == textOrValue
            }
        }

        fun resolveStoredValue(input: String?): String? {
            return findOption(input)?.value ?: input
        }
    }
}

/**
 * Snapshot of a field after evaluating dynamic properties against current session values.
 */
internal data class GeminioFormFieldState(
    val id: String,
    val value: Any?,
    val visible: Boolean,
    val enabled: Boolean,
    val suggestedValue: String? = null,
)
