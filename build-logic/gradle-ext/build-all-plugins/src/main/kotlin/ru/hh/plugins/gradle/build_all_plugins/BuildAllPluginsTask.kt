package ru.hh.plugins.gradle.build_all_plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class BuildAllPluginsTask @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    @InputFiles
    val inputFiles: ConfigurableFileCollection = objects.fileCollection()

    @OutputDirectory
    val outputFiles: DirectoryProperty = objects.directoryProperty()

    @TaskAction
    fun buildAllPlugins() {
        inputFiles.forEach { pluginArchive ->
            val newPluginArchiveDestinationFile = outputFiles.get().asFile.resolve(pluginArchive.name)
            if (newPluginArchiveDestinationFile.exists().not()) {
                Files.move(
                    pluginArchive.toPath(),
                    newPluginArchiveDestinationFile.toPath()
                )
            }
        }
    }
}
