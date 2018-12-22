package ru.hh.android.plugin.feature_module.component.logger

import com.intellij.openapi.components.ProjectComponent


class PluginLogger : ProjectComponent {

    companion object {
        private const val IS_DEBUG_MODE = true
    }


    fun log(text: String) {
        if (IS_DEBUG_MODE) {
            println(text)
        }
    }

}