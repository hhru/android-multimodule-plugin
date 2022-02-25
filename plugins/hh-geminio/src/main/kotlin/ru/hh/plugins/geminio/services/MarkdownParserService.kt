package ru.hh.plugins.geminio.services

import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.openapi.findPsiFileByName

@Service
class MarkdownParserService {

    companion object {
        private const val README_FILE_NAME = "README.md"

        fun getInstance(project: Project): MarkdownParserService = project.service()
    }

    private val options: MutableDataSet by lazy {
        MutableDataSet().apply {
            set(Parser.EXTENSIONS, listOf(TablesExtension.create(), StrikethroughExtension.create()))
            set(HtmlRenderer.SOFT_BREAK, "<br />\n")
        }
    }

    private val parser: Parser by lazy {
        Parser.builder(options).build()
    }

    private val htmlRenderer: HtmlRenderer by lazy {
        HtmlRenderer.builder(options).build()
    }

    fun parseReadmeFile(module: Module): String {
        val readmeFile = module.findPsiFileByName(README_FILE_NAME)?.virtualFile?.toIoFile()
            ?: return String.EMPTY

        val document = parser.parseReader(readmeFile.reader())
        return htmlRenderer.render(document)
    }
}
