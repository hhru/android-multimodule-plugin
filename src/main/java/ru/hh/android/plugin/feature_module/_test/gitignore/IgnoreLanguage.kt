package ru.hh.android.plugin.feature_module._test.gitignore

import com.intellij.lang.InjectableLanguage
import com.intellij.lang.Language


class IgnoreLanguage private constructor() : Language("Ignore", "ignore", null), InjectableLanguage {


    companion object {
        val INSTANCE = IgnoreLanguage()
    }


    override fun getDisplayName(): String {
        return "Ignore() ($id)"
    }

}