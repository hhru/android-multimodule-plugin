package ru.hh.android.plugin.feature_module.component.build_module

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.component.templates_factory.TemplatesFactory

class BuildModuleComponent(
        private val project: Project,
        private val templatesFactory: TemplatesFactory
) : ProjectComponent {

    fun buildNewFeatureModule() {

    }

}