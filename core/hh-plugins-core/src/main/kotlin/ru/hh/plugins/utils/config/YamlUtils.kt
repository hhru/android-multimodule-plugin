package ru.hh.plugins.utils.config

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import java.io.File
import java.io.FileReader


object YamlUtils {

    inline fun <reified T> loadFromConfigFile(configFilePath: String, onError: (Throwable) -> Unit): T? {
        val configFile = File(configFilePath).takeIf { it.exists() } ?: return null

        return try {
            val yaml = Yaml(
                CustomClassLoaderConstructor(
                    T::class.java,
                    T::class.java.classLoader
                )
            )

            yaml.load<T>(FileReader(configFile))
        } catch (ex: Exception) {
            onError.invoke(ex)
            null
        }
    }

}