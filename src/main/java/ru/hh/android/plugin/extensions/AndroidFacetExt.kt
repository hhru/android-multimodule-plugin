package ru.hh.android.plugin.extensions

import org.jetbrains.android.dom.manifest.cachedValueFromPrimaryManifest
import org.jetbrains.android.dom.manifest.packageName
import org.jetbrains.android.facet.AndroidFacet

val AndroidFacet.packageName: String get() = cachedValueFromPrimaryManifest { this.packageName }.value ?: String.EMPTY