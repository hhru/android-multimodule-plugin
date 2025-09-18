package ru.hh.plugins.geminio.sdk.helpers

import com.android.ide.common.repository.AgpVersion
import com.android.tools.idea.wizard.template.ApiTemplateData
import com.android.tools.idea.wizard.template.ApiVersion
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.Language
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.ProjectTemplateData
import com.android.tools.idea.wizard.template.ThemesData
import com.android.tools.idea.wizard.template.ViewBindingSupport
import com.android.tools.idea.wizard.template.booleanParameter
import com.android.tools.idea.wizard.template.stringParameter
import com.intellij.mock.MockVirtualFile
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import java.io.File

internal object GeminioExpressionUtils {

    fun List<RecipeExpressionCommand>.toExpression(): RecipeExpression {
        return RecipeExpression(this)
    }

    fun RecipeExpression.evaluateString(
        moduleTemplateData: ModuleTemplateData,
        existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    ): String? = evaluateString(MockVirtualFile(""), moduleTemplateData, existingParametersMap)

    fun createParametersMap(
        includeModule: Boolean = true,
        className: String = "BlankFragment",
    ): Map<String, AndroidStudioTemplateParameter> {
        return mapOf(
            "className" to stringParameter {
                name = "Class name"
                default = className
            },
            "includeModule" to booleanParameter {
                name = "Include module?"
                default = includeModule
            }
        )
    }

    fun createModuleTemplateData(): ModuleTemplateData {
        return ModuleTemplateData(
            projectTemplateData = ProjectTemplateData(
                androidXSupport = true,
                agpVersion = AgpVersion(8, 0),
                sdkDir = File("/AndroidSdk"),
                language = Language.Kotlin,
                kotlinVersion = "1.4.10",
                rootDir = File("/Project"),
                applicationPackage = "com.example.myapplication",
                includedFormFactorNames = mapOf(
                    FormFactor.Mobile to listOf("mobile")
                ),
                debugKeystoreSha1 = null,
                overridePathCheck = false,
                isNewProject = false,
                additionalMavenRepos = listOf(),
            ),
            srcDir = File("/Project/src/main/kotlin/com/example/mylibrary/"),
            resDir = File("/Project/src/main/res/"),
            manifestDir = File("/Project/src/main/"),
            testDir = File("/Project/src/test/"),
            unitTestDir = File("/Project/src/test/kotlin/"),
            aidlDir = File("/Project/src/main/aidl/"),
            rootDir = File("/Project/"),
            isNewModule = false,
            name = "mylibrary",
            isLibrary = true,
            packageName = "com.example.mylibrary",
            formFactor = FormFactor.Mobile,
            themesData = ThemesData(
                appName = "MyApplication"
            ),
            baseFeature = null,
            apis = ApiTemplateData(
                buildApi = ApiVersion(29, "29"),
                targetApi = ApiVersion(29, "29"),
                minApi = ApiVersion(21, "21"),
                appCompatVersion = 21,
            ),
            viewBindingSupport = ViewBindingSupport.NOT_SUPPORTED,
            category = Category.Other,
            isMaterial3 = false,
            useGenericLocalTests = true,
            useGenericInstrumentedTests = true,
            isCompose = false,
        )
    }

    fun getEvaluatedValue(className: String, modifier: RecipeExpressionModifier): String? {
        val expression = listOf(
            RecipeExpressionCommand.Dynamic(
                parameterId = "className",
                modifiers = listOf(
                    modifier
                )
            )
        ).toExpression()

        return expression.evaluateString(createModuleTemplateData(), createParametersMap(className = className))
    }
}
