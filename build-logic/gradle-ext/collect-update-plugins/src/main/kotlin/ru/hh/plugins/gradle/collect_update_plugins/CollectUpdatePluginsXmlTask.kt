package ru.hh.plugins.gradle.collect_update_plugins

import groovy.util.Node
import groovy.util.NodeList
import groovy.util.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.file.collections.FileCollectionAdapter
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import javax.inject.Inject

abstract class CollectUpdatePluginsXmlTask @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    private companion object {
        const val FILE_NAME = "plugin.xml"
    }

    private var customRepositoryUrl: String = ""

    @Option(option = "customRepositoryUrl", description = "Configures the URL of custom plugins repository")
    open fun setUrl(url: String) {
        this.customRepositoryUrl = url
    }

    @Input
    open fun getUrl(): String? {
        return customRepositoryUrl
    }

    @InputFiles
    val inputFiles: ConfigurableFileCollection = objects.fileCollection()

    @OutputFile
    val outputFile: RegularFileProperty = objects.fileProperty()

    @Suppress("UNCHECKED_CAST")
    @TaskAction
    fun produceUpdatePluginsXmlFile() {
        val patchFilesDirs = (inputFiles.from as Set<FileCollectionAdapter>)
            .filter { it.asPath.endsWith(FILE_NAME).not() }
            .toSet()
        val repositoryBaseUrl = customRepositoryUrl

        val updatePluginsXmlFileText = getUpdatePluginsXmlFileText(patchFilesDirs, repositoryBaseUrl)

        val output = outputFile.get().asFile
        output.writeText(updatePluginsXmlFileText)
    }

    private fun getUpdatePluginsXmlFileText(
        patchFilesDirs: Set<FileCollectionAdapter>,
        repositoryBaseUrl: String
    ): String {
        val pluginDescriptions = patchFilesDirs.map { it.toPluginDescription() }
        val pluginsTags = pluginDescriptions.map { it.toXmlTag(repositoryBaseUrl) }

        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <plugins>
            ${pluginsTags.joinToString(separator = "\n")}
        </plugins>
        """.trimIndent()
    }

    private fun PluginDescription.toXmlTag(baseUrl: String): String {
        return """
        <plugin id="${ideaPluginData.pluginId}" version="${ideaPluginData.pluginVersion}" url="$baseUrl/$zipArchiveName">
            <idea-version since-build="${ideaPluginData.sinceBuildVersion}" />
        </plugin>
        """
    }

    private fun FileCollectionAdapter.toPluginDescription(): PluginDescription {
        val pluginXmlDir = File(this.asPath.removeSuffix(FILE_NAME))
        val pluginXmlFile = pluginXmlDir.resolve(FILE_NAME)
        val pluginModuleDir = File(this.asPath).parentFile.parentFile

        val ideaPluginData = pluginXmlFile.parseIdeaPluginXml()

        return PluginDescription(
            ideaPluginData = ideaPluginData,
            zipArchiveName = "${pluginModuleDir.name}.zip"
        )
    }

    private fun File.parseIdeaPluginXml(): IdeaPluginData {
        val ideaPlugins = XmlParser().parse(this)

        val id = ideaPlugins.getNodeByName("id").getStringValue()
        val version = ideaPlugins.getNodeByName("version").getStringValue()
        val sinceBuildVersion = ideaPlugins.getNodeByName("idea-version").getStringAttribute("since-build")

        return IdeaPluginData(
            pluginId = id,
            pluginVersion = version,
            sinceBuildVersion = sinceBuildVersion
        )
    }

    private fun Node.getNodeByName(name: String) = (this[name] as NodeList)[0] as Node
    private fun Node.getStringValue(): String = value().toString().removePrefix("[").removeSuffix("]")
    private fun Node.getStringAttribute(name: String): String {
        return attribute(name).toString().removePrefix("[").removeSuffix("]")
    }

    private data class IdeaPluginData(
        val pluginId: String,
        val pluginVersion: String,
        val sinceBuildVersion: String
    )

    private data class PluginDescription(
        val ideaPluginData: IdeaPluginData,
        val zipArchiveName: String
    )
}
