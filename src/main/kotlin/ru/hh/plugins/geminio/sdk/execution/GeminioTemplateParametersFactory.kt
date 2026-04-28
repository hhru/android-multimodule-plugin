package ru.hh.plugins.geminio.sdk.execution

import ru.hh.plugins.geminio.sdk.form.GeminioFormSession

/**
 * Produces the parameter map consumed by FreeMarker templates and recipe expressions.
 */
internal object GeminioTemplateParametersFactory {

    fun create(
        session: GeminioFormSession,
        packageName: String,
        applicationPackageName: String,
        currentDirPath: String,
        additionalParameters: Map<String, Any?> = emptyMap(),
    ): Map<String, Any?> {
        return session.values() +
            additionalParameters +
            mapOf(
                HardcodedParams.PACKAGE_NAME to packageName,
                HardcodedParams.APPLICATION_PACKAGE to applicationPackageName,
                HardcodedParams.CURRENT_DIR_PACKAGE_NAME to currentDirPath.toCurrentDirPackageName(packageName),
            )
    }

    private fun String.toCurrentDirPackageName(packageName: String): String {
        val dottedPath = replace('/', '.')
        val packageNameIndex = dottedPath.indexOf(packageName)

        return if (packageNameIndex != -1) {
            dottedPath.substring(packageNameIndex)
        } else {
            dottedPath
        }
    }
}

internal object HardcodedParams {
    /**
     * Package name for the currently generated template/module.
     */
    const val PACKAGE_NAME = "packageName"

    /**
     * Application package of the host Android project.
     */
    const val APPLICATION_PACKAGE = "applicationPackage"

    /**
     * Package name inferred from the currently selected directory.
     */
    const val CURRENT_DIR_PACKAGE_NAME = "currentDirPackageName"
}
