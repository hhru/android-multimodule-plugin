package ru.hh.plugins.geminio.sdk


internal object GeminioSdkConstants {

    const val DEFAULT_REVISION_VALUE = 1
    const val DEFAULT_MIN_API_VALUE = 1
    const val DEFAULT_MIN_BUILD_API_VALUE = 1


    object HardcodedParams {
        /**
         * Package name from SELECTED file.
         */
        const val PACKAGE_NAME = "packageName"

        /**
         * Package name from current gradle module.
         */
        const val APPLICATION_PACKAGE = "applicationPackage"
    }

}