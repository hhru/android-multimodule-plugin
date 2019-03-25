package ru.hh.android.plugin.feature_module.generator.templates.gitignore

import com.intellij.lang.InjectableLanguage
import com.intellij.lang.Language


class IgnoreLanguage private constructor() : Language(LANGUAGE_ID, LANGUAGE_FILE_MIME_TYPE, null), InjectableLanguage {


    companion object {
        private const val LANGUAGE_ID = "ru.hh.android.plugin.feature_module.Ignore"
        private const val LANGUAGE_FILE_MIME_TYPE = "ignore"

        val INSTANCE = IgnoreLanguage()
    }


    override fun getDisplayName(): String {
        return "Ignore() ($id)"
    }

}