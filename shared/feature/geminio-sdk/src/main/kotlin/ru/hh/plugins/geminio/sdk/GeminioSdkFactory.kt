package ru.hh.plugins.geminio.sdk

object GeminioSdkFactory {

    fun createGeminioSdk(): GeminioSdk {
        return GeminioSdkImpl()
    }
}
