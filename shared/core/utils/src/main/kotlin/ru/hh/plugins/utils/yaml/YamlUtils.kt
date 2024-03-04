package ru.hh.plugins.utils.yaml

import org.yaml.snakeyaml.LoaderOptions
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
                    T::class.java.classLoader,
                    LoaderOptions(),
                )
            )

            yaml.load<T>(FileReader(configFile))
        } catch (ex: Exception) {
            onError.invoke(ex)
            null
        }
    }

    fun loadFromConfigFile(filePath: String, onError: (Throwable) -> Unit): LinkedHashMap<String, Any>? {
        val configFile = File(filePath).takeIf { it.exists() } ?: return null

        return try {
            val yaml = Yaml()

            return yaml.load(FileReader(configFile))
        } catch (ex: Exception) {
            onError.invoke(ex)
            null
        }
    }

    fun Map<String, Any>.getBooleanOrStringExpression(key: String): String? {
        return (this[key] as? Boolean)?.let { "$it" } ?: this[key] as? String
    }
}
