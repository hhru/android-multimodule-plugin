package ru.hh.android.plugin.feature_module._test.templates.gitignore


import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


class IgnoreFileType(language: Language) : LanguageFileType(language) {

    companion object {
        val INSTANCE = IgnoreFileType(IgnoreLanguage.INSTANCE)
    }


    override fun getName(): String {
        return "ignore file"
    }

    override fun getDescription(): String {
        return "gitignore files"
    }

    override fun getDefaultExtension(): String {
        return "gitignore"
    }

    override fun getIcon(): Icon? {
        return null
    }

}