package ru.hh.android.plugin.services.freemarker.utils

import com.android.tools.idea.npw.template.TemplateValueInjector
import com.android.tools.idea.templates.TemplateAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.android.plugin.extensions.EMPTY
import java.io.File


class DryRunValuesInjector(
    private val project: Project,
    private val androidFacet: AndroidFacet,
    private val currentModuleRootDir: File,
    private val currentModulePackageName: String
) {

    companion object {
        private const val ATTR_PROJECT_LOCATION = "projectLocation" // "Master" Project root
    }

    fun injectValues(templateParametersMap: MutableMap<String, Any>) {
        injectModuleRoot(templateParametersMap)

        TemplateValueInjector(templateParametersMap)
            .setFacet(androidFacet)
    }


    /**
     * Kotlin version of [com.android.tools.idea.npw.template.TemplateValueInjector.setModuleRoots]. We need it
     * because of android plugin incompatibility.
     */
    private fun injectModuleRoot(templateParametersMap: MutableMap<String, Any>) {
        val androidModulePathsData = AndroidModulePathsData(currentModuleRootDir, currentModulePackageName)

        val moduleRootDir = androidModulePathsData.moduleRoot
        moduleRootDir?.let { moduleRoot ->
            templateParametersMap[TemplateAttributes.ATTR_PROJECT_OUT] = FileUtil.toSystemIndependentName(moduleRoot.absolutePath)

            androidModulePathsData.getSrcDirectory(currentModulePackageName)?.let { srcDir ->
                templateParametersMap[TemplateAttributes.ATTR_SRC_DIR] = getRelativePath(moduleRoot, srcDir)
                templateParametersMap[TemplateAttributes.ATTR_SRC_OUT] = FileUtil.toSystemIndependentName(srcDir.absolutePath)
            }

            androidModulePathsData.getTestDirectory(currentModulePackageName)?.let { testDir ->
                templateParametersMap[TemplateAttributes.ATTR_TEST_DIR] = getRelativePath(moduleRoot, testDir)
                templateParametersMap[TemplateAttributes.ATTR_TEST_OUT] = FileUtil.toSystemIndependentName(testDir.absolutePath)
            }

            androidModulePathsData.resDirectories.firstOrNull()?.let { resDir ->
                templateParametersMap[TemplateAttributes.ATTR_RES_DIR] = getRelativePath(moduleRoot, resDir)
                templateParametersMap[TemplateAttributes.ATTR_RES_OUT] = FileUtil.toSystemIndependentName(resDir.path)
            }

            androidModulePathsData.manifestDirectory?.let { manifestDir ->
                templateParametersMap[TemplateAttributes.ATTR_MANIFEST_DIR] = getRelativePath(moduleRoot, manifestDir)
                templateParametersMap[TemplateAttributes.ATTR_MANIFEST_OUT] = FileUtil.toSystemIndependentName(manifestDir.path)
            }

            androidModulePathsData.getAidlDirectory(currentModulePackageName)?.let { aidlDir ->
                templateParametersMap[TemplateAttributes.ATTR_AIDL_DIR] = getRelativePath(moduleRoot, aidlDir)
                templateParametersMap[TemplateAttributes.ATTR_AIDL_OUT] = FileUtil.toSystemIndependentName(aidlDir.path)
            }

            templateParametersMap[TemplateAttributes.ATTR_PACKAGE_NAME] = currentModulePackageName
            templateParametersMap[TemplateAttributes.ATTR_MODULE_NAME] = moduleRoot.name

            templateParametersMap[ATTR_PROJECT_LOCATION] = project.basePath ?: String.EMPTY
        }
    }

    private fun getRelativePath(base: File, target: File): String {
        return FileUtil.getRelativePath(
            FileUtil.toSystemIndependentName(base.path),
            FileUtil.toSystemIndependentName(target.path), '/'
        ) as String
    }
}