package ru.hh.android.plugin.generator.templates.gitignore


import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


class IgnoreFileType(language: Language) : LanguageFileType(language) {

    companion object {
        val INSTANCE = IgnoreFileType(IgnoreLanguage.INSTANCE)

        private const val FILE_TYPE_NAME = "gitignore file"
        private const val FILE_TYPE_DESCRIPTION = "gitignore files"
        private const val FILE_TYPE_DEFAULT_EXTENSION = "gitignore"
    }


    override fun getName(): String {
        return FILE_TYPE_NAME
    }

    override fun getDescription(): String {
        return FILE_TYPE_DESCRIPTION
    }

    override fun getDefaultExtension(): String {
        return FILE_TYPE_DEFAULT_EXTENSION
    }

    override fun getIcon(): Icon? {
        return null
    }

}