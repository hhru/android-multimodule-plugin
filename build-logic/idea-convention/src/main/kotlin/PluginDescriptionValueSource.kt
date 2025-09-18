import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.jetbrains.changelog.markdownToHTML

/**
 * Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
 */
abstract class PluginDescriptionValueSource : ValueSource<String, PluginDescriptionValueSource.Parameters> {
    override fun obtain(): String? {
        val readmeFile = parameters.readmeFilePath.get().asFile
        val lines = readmeFile.readText().lines()

        val start = lines.indexOf(DESCRIPTION_START_MARKER)
        val end = lines.indexOf(DESCRIPTION_END_MARKER)

        if (start < 0 || end < 0) {
            throw GradleException(
                "Plugin description section not found in README.md:\n" +
                    "$DESCRIPTION_START_MARKER ... $DESCRIPTION_END_MARKER"
            )
        }

        return lines.subList(start + 1, end).joinToString("\n").let(::markdownToHTML)
    }

    interface Parameters : ValueSourceParameters {
        val readmeFilePath: RegularFileProperty
    }

    private companion object {
        const val DESCRIPTION_START_MARKER = "<!-- Plugin description -->"
        const val DESCRIPTION_END_MARKER = "<!-- Plugin description end -->"
    }
}
