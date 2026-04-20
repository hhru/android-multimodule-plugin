package ru.hh.plugins.geminio.wizard

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.util.textCompletion.TextCompletionValueDescriptor
import com.intellij.util.textCompletion.TextFieldWithCompletion
import com.intellij.util.textCompletion.ValuesCompletionProvider
import ru.hh.plugins.geminio.sdk.form.GeminioFormSuggestOption

/**
 * Searchable suggest selector backed by IntelliJ completion popup.
 *
 * The component lets the user search among predefined options. In sealed mode the entered text
 * must match one of the available entries; otherwise the raw text is accepted as the final value.
 */
internal class GeminioSearchableSuggestField(
    project: Project,
    private val options: List<GeminioFormSuggestOption>,
    initialValue: String?,
    private val isSealed: Boolean,
) : TextFieldWithCompletion(
    project,
    GeminioSuggestCompletionProvider(options),
    options.firstOrNull { option -> option.value == initialValue }?.label ?: initialValue.orEmpty(),
    true,
    true,
    false,
    false,
) {

    fun selectedOptionOrNull(): GeminioFormSuggestOption? {
        val currentText = text.trim()

        return options.firstOrNull { option ->
            option.label == currentText || option.value == currentText
        }
    }

    fun isValidValue(): Boolean {
        return isSealed.not() || selectedOptionOrNull() != null
    }

    fun resolvedValue(): String? {
        return selectedOptionOrNull()?.value ?: text
    }

    fun setResolvedValue(value: String?) {
        val option = options.firstOrNull { currentOption -> currentOption.value == value }
        val expectedText = option?.label ?: value.orEmpty()

        if (text != expectedText) {
            text = expectedText
        }
    }

    fun addValueListener(listener: () -> Unit) {
        addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    listener()
                }
            }
        )
    }
}

private class GeminioSuggestCompletionProvider(
    options: List<GeminioFormSuggestOption>,
) : ValuesCompletionProvider<GeminioFormSuggestOption>(
    GeminioSuggestCompletionDescriptor,
    options,
)

private object GeminioSuggestCompletionDescriptor : TextCompletionValueDescriptor<GeminioFormSuggestOption> {

    override fun createLookupBuilder(option: GeminioFormSuggestOption): LookupElementBuilder {
        return LookupElementBuilder
            .create(option.label)
            .withPresentableText(option.label)
            .withLookupString(option.value)
            .let { builder ->
                if (option.label == option.value) {
                    builder
                } else {
                    builder.withTypeText(option.value, true)
                }
            }
    }

    override fun compare(
        first: GeminioFormSuggestOption,
        second: GeminioFormSuggestOption,
    ): Int {
        return first.label.compareTo(second.label, ignoreCase = true)
    }
}
