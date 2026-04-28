package ru.hh.plugins.geminio.services.android

import org.jetbrains.android.dom.manifest.cachedValueFromPrimaryManifest
import org.jetbrains.android.facet.AndroidFacet

internal val AndroidFacet.packageName: String
    get() = cachedValueFromPrimaryManifest { this.packageName }.value ?: ""
