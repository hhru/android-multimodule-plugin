package ru.hh.plugins.utils.freemarker

import com.google.common.base.Charsets
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import ru.hh.plugins.utils.freemarker.adapters.PropertyObjectWrapper
import java.io.File
import java.io.StringWriter

/**
 * Base configuration wrapper for resolving Freemarker's templates.
 */
class FreemarkerConfiguration(
    private val configuredPath: String
) : Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS) {

    companion object {

        fun buildAndResolve(
            templatesDirPath: String,
            templateName: String,
            params: Map<String, Any>
        ): String {
            val freemarkerConfiguration = FreemarkerConfiguration(templatesDirPath)
            return freemarkerConfiguration.resolveTemplate(templateName, params)
        }

    }


    init {
        defaultEncoding = Charsets.UTF_8.name()
        localizedLookup = false
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        objectWrapper = PropertyObjectWrapper()

        setDirectoryForTemplateLoading(File(configuredPath))
    }


    /**
     * Method for resolving template.ftl into [String].
     *
     * @param templateRelativePath -- path to FreeMarker's template file relatively to configured root
     *                                @see [freemarker.template.Configuration.setDirectoryForTemplateLoading]
     * @param templateParams       -- parameters for template resolving
     */
    fun resolveTemplate(
        templateRelativePath: String,
        templateParams: Map<String, Any?>
    ): String {
        val template = try {
            getTemplate(templateRelativePath)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw FreemarkerException(
                "Can't find template [configuredPath: ${configuredPath}, templateRelativePath: $templateRelativePath]",
                ex
            )
        }

        return StringWriter().use { writer ->
            try {
                template.process(templateParams, writer)
                writer.buffer.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw FreemarkerException(
                    "Can't resolve template [configuredPath: ${configuredPath}, templateRelativePath: $templateRelativePath, templateParams: $templateParams]",
                    ex
                )
            }
        }
    }

}