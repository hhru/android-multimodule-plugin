package ru.hh.android.plugins.garcon.live_templates

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider


class GarconTemplateProvider : DefaultLiveTemplatesProvider {

    companion object {
        private const val LIVE_TEMPLATE_PATH = "liveTemplates/Garcon"
    }


    override fun getDefaultLiveTemplateFiles(): Array<String> {
        return arrayOf(LIVE_TEMPLATE_PATH)
    }

    override fun getHiddenLiveTemplateFiles(): Array<String>? {
        return null
    }
}