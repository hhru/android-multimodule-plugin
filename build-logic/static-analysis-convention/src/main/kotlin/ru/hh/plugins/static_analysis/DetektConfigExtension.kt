package ru.hh.plugins.static_analysis

import java.io.File

class DetektConfigExtension(
    var configPath: Iterable<*>? = null,
    var baselinePath: File? = null
)
