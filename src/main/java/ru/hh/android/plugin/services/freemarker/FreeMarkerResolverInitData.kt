package ru.hh.android.plugin.services.freemarker

import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import java.io.File


data class FreeMarkerResolverInitData(
    val project: Project,
    val androidFacet: AndroidFacet,
    val moduleRootDir: File,
    val modulePackageName: String
)