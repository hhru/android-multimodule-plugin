@file:Suppress("UnstableApiUsage")

package ru.hh.android.plugin.extensions.lint

import com.android.SdkConstants
import com.android.tools.idea.util.androidFacet
import com.android.tools.lint.detector.api.XmlContext
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.idea.core.util.toPsiFile

val XmlContext.ideaProject: Project get() = this.project.ideaProject

val XmlContext.androidFacet: AndroidFacet? get() = this.file.toPsiFile(ideaProject)?.module?.androidFacet

val XmlContext.fileNameWithoutExtension: String get() = this.file.name.removeSuffix(SdkConstants.DOT_XML)
