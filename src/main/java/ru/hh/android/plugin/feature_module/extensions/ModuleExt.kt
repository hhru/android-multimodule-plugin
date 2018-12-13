package ru.hh.android.plugin.feature_module.extensions

import com.intellij.openapi.module.Module
import com.intellij.psi.search.FilenameIndex


const val BUILD_GRADLE_FILE_NAME = "build.gradle"

const val GRADLE_KEYWORD_APPLY = "apply"
const val GRADLE_KEYWORD_PLUGIN = "plugin"

const val PLUGIN_ANDROID_LIBRARY_NAME = "com.android.library"
const val PLUGIN_JAVA_LIBRARY = "java-library"


fun Module.isLibraryModule(): Boolean {
    return FilenameIndex.getFilesByName(
            project,
            BUILD_GRADLE_FILE_NAME,
            moduleContentScope
    ).firstOrNull()?.let { buildGradlePsiFile ->
        return buildGradlePsiFile.children.any { psiElement ->
            val text = psiElement.text

            text.contains(GRADLE_KEYWORD_APPLY)
                    && text.contains(GRADLE_KEYWORD_APPLY)
                    && (text.contains(PLUGIN_ANDROID_LIBRARY_NAME) || text.contains(PLUGIN_JAVA_LIBRARY))
        }

    } ?: false
}