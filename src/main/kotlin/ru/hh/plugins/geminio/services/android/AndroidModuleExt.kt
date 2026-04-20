package ru.hh.plugins.geminio.services.android

import com.android.tools.idea.projectsystem.gradle.getHolderModule
import com.intellij.openapi.module.Module
import org.jetbrains.android.facet.AndroidFacet

/**
 * Returns `true` only for top-level Android application modules.
 *
 * Android Studio project model may expose synthetic test holder modules alongside the main
 * application module, so we additionally check that the current module is the holder module.
 */
fun Module.isAndroidAppModule(): Boolean {
    val androidFacet = AndroidFacet.getInstance(this)
    val isAppProject = androidFacet?.configuration?.isAppProject ?: false
    val isHolderModule = this == androidFacet?.module?.getHolderModule()

    return isAppProject && isHolderModule
}
