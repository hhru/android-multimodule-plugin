package ru.hh.plugins.geminio.sdk.form

/**
 * Mutable runtime state for a [GeminioForm].
 *
 * The session owns current field values, computes suggestions and evaluates visibility/enabled
 * rules. This is the state object used by the custom Geminio dialogs instead of `ModelWizard`.
 */
internal class GeminioFormSession(
    private val form: GeminioForm,
    private val pathContext: GeminioFormPathContext = GeminioFormPathContext(),
) : GeminioFormEvaluationContext {

    private val values = linkedMapOf<String, Any?>()

    init {
        form.fields.forEach(::initializeFieldValue)
    }

    override fun getValue(parameterId: String): Any? {
        return values[parameterId]
    }

    override fun getPath(pathAlias: GeminioFormPathAlias): String? {
        return pathContext.get(pathAlias)
    }

    fun stringValue(fieldId: String): String? {
        requireStringField(fieldId)
        return values[fieldId] as? String
    }

    fun booleanValue(fieldId: String): Boolean {
        requireBooleanField(fieldId)
        return values[fieldId] as? Boolean ?: false
    }

    fun suggestValue(fieldId: String): String {
        val field = requireSuggestField(fieldId)

        return (values[fieldId] as? String)
            ?.takeIf { value -> field.isSealed.not() || field.containsValue(value) }
            ?: resolveSuggestFallbackValue(field)
    }

    fun setStringValue(fieldId: String, value: String?) {
        requireStringField(fieldId)
        values[fieldId] = value
    }

    fun setBooleanValue(fieldId: String, value: Boolean) {
        requireBooleanField(fieldId)
        values[fieldId] = value
    }

    fun setSuggestValue(fieldId: String, value: String?) {
        val field = requireSuggestField(fieldId)
        require(field.isSealed.not() || field.containsValue(value)) {
            "Field with id='$fieldId' does not contain suggested option '$value'"
        }
        values[fieldId] = value
    }

    fun suggestedStringValue(fieldId: String): String? {
        val field = requireStringField(fieldId)
        return field.suggestEvaluator?.invoke(this)
    }

    fun suggestedSuggestValue(fieldId: String): String? {
        val field = requireSuggestField(fieldId)
        val suggestedValue = field.suggestEvaluator?.invoke(this)

        return suggestedValue?.takeIf { value ->
            field.isSealed.not() || field.containsValue(value)
        }
    }

    /**
     * Recomputes a string field suggestion and writes it back to the stored value.
     *
     * This mirrors how the current Android Studio parameter runtime eagerly updates the target
     * parameter when a `suggest` lambda is invoked.
     */
    fun applySuggestion(fieldId: String): String? {
        val field = requireStringField(fieldId)
        val suggestedValue = field.suggestEvaluator?.invoke(this) ?: stringValue(fieldId)
        values[fieldId] = suggestedValue
        return suggestedValue
    }

    /**
     * Recomputes a suggest field suggestion and writes back a valid value.
     *
     * For open-set fields we can store any string. Sealed fields ignore invalid suggestions, then
     * preserve the current valid value, then fall back to `default`, and finally to the first
     * option.
     */
    fun applySuggestFieldSuggestion(fieldId: String): String? {
        val field = requireSuggestField(fieldId)
        val resolvedValue = when {
            field.isSealed -> suggestedSuggestValue(fieldId)
                ?: (values[fieldId] as? String)?.takeIf(field::containsValue)
                ?: resolveSuggestFallbackValue(field)

            else -> field.suggestEvaluator?.invoke(this) ?: suggestValue(fieldId)
        }

        values[fieldId] = resolvedValue
        return resolvedValue
    }

    /**
     * Returns the evaluated view of a field for the current session values.
     */
    fun fieldState(fieldId: String): GeminioFormFieldState {
        val field = form.requireField(fieldId)

        return when (field) {
            is GeminioFormField.StringField -> GeminioFormFieldState(
                id = field.id,
                value = stringValue(fieldId),
                visible = field.visibilityEvaluator?.invoke(this) ?: true,
                enabled = field.availabilityEvaluator?.invoke(this) ?: true,
                suggestedValue = field.suggestEvaluator?.invoke(this),
            )

            is GeminioFormField.BooleanField -> GeminioFormFieldState(
                id = field.id,
                value = booleanValue(fieldId),
                visible = field.visibilityEvaluator?.invoke(this) ?: true,
                enabled = field.availabilityEvaluator?.invoke(this) ?: true,
            )

            is GeminioFormField.SuggestField -> GeminioFormFieldState(
                id = field.id,
                value = suggestValue(fieldId),
                visible = field.visibilityEvaluator?.invoke(this) ?: true,
                enabled = field.availabilityEvaluator?.invoke(this) ?: true,
                suggestedValue = suggestedSuggestValue(fieldId),
            )
        }
    }

    /**
     * Immutable copy of the current raw values map.
     */
    fun values(): Map<String, Any?> {
        return values.toMap()
    }

    private fun initializeFieldValue(field: GeminioFormField) {
        when (field) {
            is GeminioFormField.StringField -> {
                values[field.id] = field.initialValueEvaluator?.invoke(this) ?: field.defaultValue
            }

            is GeminioFormField.BooleanField -> {
                values[field.id] = field.initialValueEvaluator?.invoke(this) ?: field.defaultValue ?: false
            }

            is GeminioFormField.SuggestField -> {
                values[field.id] = resolveSuggestInitialValue(field)
            }
        }
    }

    private fun resolveSuggestInitialValue(field: GeminioFormField.SuggestField): String? {
        val initialValue = field.initialValueEvaluator?.invoke(this)

        return when {
            field.isSealed -> initialValue?.takeIf(field::containsValue) ?: resolveSuggestFallbackValue(field)
            else -> initialValue ?: resolveSuggestFallbackValue(field)
        }
    }

    private fun resolveSuggestFallbackValue(field: GeminioFormField.SuggestField): String {
        return field.defaultValue
            ?.takeIf { value -> field.isSealed.not() || field.containsValue(value) }
            ?: field.options.first().value
    }

    private fun requireStringField(fieldId: String): GeminioFormField.StringField {
        return form.requireField(fieldId) as? GeminioFormField.StringField
            ?: throw IllegalArgumentException("Field with id='$fieldId' is not a string field")
    }

    private fun requireBooleanField(fieldId: String): GeminioFormField.BooleanField {
        return form.requireField(fieldId) as? GeminioFormField.BooleanField
            ?: throw IllegalArgumentException("Field with id='$fieldId' is not a boolean field")
    }

    private fun requireSuggestField(fieldId: String): GeminioFormField.SuggestField {
        return form.requireField(fieldId) as? GeminioFormField.SuggestField
            ?: throw IllegalArgumentException("Field with id='$fieldId' is not a suggest field")
    }
}
