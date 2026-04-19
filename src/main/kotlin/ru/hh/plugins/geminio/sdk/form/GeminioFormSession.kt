package ru.hh.plugins.geminio.sdk.form

/**
 * Mutable runtime state for a [GeminioForm].
 *
 * The session owns current field values, computes suggestions and evaluates visibility/enabled
 * rules. This is the state object the future custom dialog will talk to instead of `ModelWizard`.
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

    fun setStringValue(fieldId: String, value: String?) {
        requireStringField(fieldId)
        values[fieldId] = value
    }

    fun setBooleanValue(fieldId: String, value: Boolean) {
        requireBooleanField(fieldId)
        values[fieldId] = value
    }

    fun suggestedStringValue(fieldId: String): String? {
        val field = requireStringField(fieldId)
        return field.suggestEvaluator?.invoke(this)
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
        }
    }

    /**
     * Convenience method for rendering the whole form in field order.
     */
    fun allFieldStates(): List<GeminioFormFieldState> {
        return form.fields.map { field -> fieldState(field.id) }
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
        }
    }

    private fun requireStringField(fieldId: String): GeminioFormField.StringField {
        return form.requireField(fieldId) as? GeminioFormField.StringField
            ?: throw IllegalArgumentException("Field with id='$fieldId' is not a string field")
    }

    private fun requireBooleanField(fieldId: String): GeminioFormField.BooleanField {
        return form.requireField(fieldId) as? GeminioFormField.BooleanField
            ?: throw IllegalArgumentException("Field with id='$fieldId' is not a boolean field")
    }
}
