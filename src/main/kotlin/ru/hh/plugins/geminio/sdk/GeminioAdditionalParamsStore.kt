package ru.hh.plugins.geminio.sdk

/**
 * Simple wrapper for map of objects.
 */
class GeminioAdditionalParamsStore(
    private val params: MutableMap<String, Any> = mutableMapOf()
) : MutableMap<String, Any> by params
