package ru.hh.android.plugins.garcon.extensions

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.module.Module
import com.intellij.psi.search.FilenameIndex
import ru.hh.android.plugins.garcon.extensions.psi.packageName


private const val BUILD_GRADLE_FILENAME = "build.gradle"

private const val GRADLE_KEYWORD_APPLY = "apply"
private const val GRADLE_KEYWORD_PLUGIN = "plugin"

private const val PLUGIN_ANDROID_APP_NAME = "com.android.application"


fun Module.isAppModule(): Boolean {
    return FilenameIndex.getFilesByName(
        project,
        BUILD_GRADLE_FILENAME,
        moduleContentScope
    ).firstOrNull()?.let { buildGradlePsiFile ->
        return buildGradlePsiFile.children.any { psiElement ->
            val text = psiElement.text

            text.contains(GRADLE_KEYWORD_APPLY)
                    && text.contains(GRADLE_KEYWORD_PLUGIN)
                    && (text.contains(PLUGIN_ANDROID_APP_NAME))
        }
    } ?: false
}

val Module.androidModulePackageName: String?
    get() {
        return this.androidFacet?.packageName
    }