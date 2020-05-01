package ru.hh.android.plugin.extensions

import org.jetbrains.android.dom.manifest.AndroidManifestUtils
import org.jetbrains.android.facet.AndroidFacet


val AndroidFacet.packageName: String get() = AndroidManifestUtils.getPackageName(this) ?: String.EMPTY