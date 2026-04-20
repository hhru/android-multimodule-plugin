package ru.hh.plugins.geminio.sdk.recipe.parsers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.yaml.snakeyaml.Yaml
import ru.hh.plugins.geminio.sdk.helpers.createRecipeFixture
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.SuggestParameterOption
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.widgets.toWidgetsSection

internal class GeminioWidgetsSectionParserSpec : FreeSpec({
    "Should parse suggest widget with options and suggest" {
        val widgetsSectionYaml = $$"""
            widgets:
              - suggestParameter:
                  id: uiFramework
                  name: UI framework
                  help: Which stack should be used
                  default: compose
                  suggest: ${preferredUiFramework}
                  visibility: true
                  availability: ${includeModule}
                  options:
                    - value: compose
                      label: Compose
                    - value: views
                      label: Views
        """.trimIndent()

        val parsed: Map<String, Any> = Yaml().load(widgetsSectionYaml)
        val expected = WidgetsSection(
            parameters = listOf(
                RecipeParameter.SuggestParameter(
                    id = "uiFramework",
                    name = "UI framework",
                    help = "Which stack should be used",
                    visibilityExpression = RecipeExpressionCommand.ReturnTrue.asExpression(),
                    availabilityExpression = RecipeExpressionCommand.Dynamic(
                        parameterId = "includeModule",
                        modifiers = emptyList(),
                    ).asExpression(),
                    default = "compose",
                    isSealed = false,
                    suggestExpression = RecipeExpressionCommand.Dynamic(
                        parameterId = "preferredUiFramework",
                        modifiers = emptyList(),
                    ).asExpression(),
                    options = listOf(
                        SuggestParameterOption(value = "compose", label = "Compose"),
                        SuggestParameterOption(value = "views", label = "Views"),
                    ),
                )
            )
        )

        parsed.toWidgetsSection() shouldBe expected
    }

    "Should reject suggest widget with duplicate option values" {
        val widgetsSectionYaml = """
            widgets:
              - suggestParameter:
                  id: uiFramework
                  name: UI framework
                  options:
                    - value: compose
                      label: Compose
                    - value: compose
                      label: Duplicate Compose
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(widgetsSectionYaml)

        val ex = shouldThrow<IllegalArgumentException> {
            parsed.toWidgetsSection()
        }

        ex.message shouldBe
                "'widgets:suggestParameter' section: Suggest parameter options should have unique " +
                "'value' values [values: [compose, compose]]."
    }

    "Should reject sealed suggest widget when default is not present in options" {
        val widgetsSectionYaml = """
            widgets:
              - suggestParameter:
                  id: uiFramework
                  name: UI framework
                  sealed: true
                  default: xml
                  options:
                    - value: compose
                      label: Compose
                    - value: views
                      label: Views
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(widgetsSectionYaml)

        val ex = shouldThrow<IllegalArgumentException> {
            parsed.toWidgetsSection()
        }

        ex.message shouldBe
            "'widgets:suggestParameter' section: Sealed suggest parameter default should match one of " +
                "'options.value' [default: xml]."
    }

    "Should allow non-sealed suggest widget default outside declared options" {
        val widgetsSectionYaml = """
            widgets:
              - suggestParameter:
                  id: targetModule
                  name: Target module
                  default: feature-manual
                  options:
                    - value: app
                    - value: feature-feed
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(widgetsSectionYaml)

        parsed.toWidgetsSection() shouldBe WidgetsSection(
            parameters = listOf(
                RecipeParameter.SuggestParameter(
                    id = "targetModule",
                    name = "Target module",
                    help = null,
                    visibilityExpression = null,
                    availabilityExpression = null,
                    default = "feature-manual",
                    isSealed = false,
                    suggestExpression = null,
                    options = listOf(
                        SuggestParameterOption(value = "app", label = "app"),
                        SuggestParameterOption(value = "feature-feed", label = "feature-feed"),
                    ),
                )
            )
        )
    }

    "Should use option value as label when label is omitted" {
        val widgetsSectionYaml = """
            widgets:
              - suggestParameter:
                  id: uiFramework
                  name: UI framework
                  options:
                    - value: compose
                    - value: views
                      label: Classic Views
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(widgetsSectionYaml)
        val expected = WidgetsSection(
            parameters = listOf(
                RecipeParameter.SuggestParameter(
                    id = "uiFramework",
                    name = "UI framework",
                    help = null,
                    visibilityExpression = null,
                    availabilityExpression = null,
                    default = null,
                    isSealed = false,
                    suggestExpression = null,
                    options = listOf(
                        SuggestParameterOption(value = "compose", label = "compose"),
                        SuggestParameterOption(value = "views", label = "Classic Views"),
                    ),
                )
            )
        )

        parsed.toWidgetsSection() shouldBe expected
    }

    "Should load suggest options from csv source file" {
        val fixture = createRecipeFixture(
            recipeYaml = """
                requiredParams:
                  name: Geminio suggest parser test
                  description: Covers CSV-based suggest options

                widgets:
                  - suggestParameter:
                      id: targetModule
                      name: Target module
                      sealed: true
                      options:
                        source: options/modules.csv

                recipe: []
            """,
            templates = mapOf(
                "options/modules.csv" to """
                    value,label
                    app,App module
                    feature-auth,Authentication
                """
            ),
        )

        fixture.recipe.widgetsSection shouldBe WidgetsSection(
            parameters = listOf(
                RecipeParameter.SuggestParameter(
                    id = "targetModule",
                    name = "Target module",
                    help = null,
                    visibilityExpression = null,
                    availabilityExpression = null,
                    default = null,
                    isSealed = true,
                    suggestExpression = null,
                    options = listOf(
                        SuggestParameterOption(value = "app", label = "App module"),
                        SuggestParameterOption(value = "feature-auth", label = "Authentication"),
                    ),
                )
            )
        )
    }

    "Should expand included widgets in declared order" {
        val fixture = createRecipeFixture(
            recipeYaml = """
                requiredParams:
                  name: Geminio include parser test
                  description: Covers widgets include order

                widgets:
                  - stringParameter:
                      id: screenName
                      name: Screen name
                      default: FeedScreen

                  - include:
                      file: shared/codeowners.widgets.yaml

                  - booleanParameter:
                      id: generateDi
                      name: Generate DI
                      default: true

                recipe: []
            """,
            templates = mapOf(
                "shared/codeowners.widgets.yaml" to """
                    widgets:
                      - stringParameter:
                          id: codeOwnerTeam
                          name: Codeowners team
                          default: team-example

                      - stringParameter:
                          id: codeOwnerPath
                          name: Codeowners path
                          default: /feature
                """
            ),
        )

        fixture.recipe.widgetsSection.parameters.map(RecipeParameter::id) shouldBe listOf(
            "screenName",
            "codeOwnerTeam",
            "codeOwnerPath",
            "generateDi",
        )
    }

    "Should resolve nested widgets includes" {
        val fixture = createRecipeFixture(
            recipeYaml = """
                requiredParams:
                  name: Geminio nested include test
                  description: Covers nested widgets includes

                widgets:
                  - include:
                      file: shared/base.widgets.yaml

                recipe: []
            """,
            templates = mapOf(
                "shared/base.widgets.yaml" to """
                    widgets:
                      - stringParameter:
                          id: moduleName
                          name: Module name
                          default: feature-feed

                      - include:
                          file: nested/codeowners.widgets.yaml
                """,
                "shared/nested/codeowners.widgets.yaml" to """
                    widgets:
                      - stringParameter:
                          id: codeOwnerTeam
                          name: Codeowners team
                          default: team-example
                """
            ),
        )

        fixture.recipe.widgetsSection.parameters.map(RecipeParameter::id) shouldBe listOf(
            "moduleName",
            "codeOwnerTeam",
        )
    }

    "Should detect circular widgets includes" {
        val exception = shouldThrow<IllegalArgumentException> {
            createRecipeFixture(
                recipeYaml = """
                    requiredParams:
                      name: Geminio include cycle test
                      description: Covers circular widgets includes

                    widgets:
                      - include:
                          file: shared/a.widgets.yaml

                    recipe: []
                """,
                templates = mapOf(
                    "shared/a.widgets.yaml" to """
                        widgets:
                          - include:
                              file: b.widgets.yaml
                    """,
                    "shared/b.widgets.yaml" to """
                        widgets:
                          - include:
                              file: a.widgets.yaml
                    """
                ),
            )
        }

        exception.message?.contains("Circular widgets include detected") shouldBe true
    }

    "Should reject duplicate widget ids after include expansion" {
        val exception = shouldThrow<IllegalArgumentException> {
            createRecipeFixture(
                recipeYaml = """
                    requiredParams:
                      name: Geminio duplicate widgets test
                      description: Covers duplicate widget ids after include

                    widgets:
                      - stringParameter:
                          id: codeOwnerTeam
                          name: Codeowners team
                          default: local-team

                      - include:
                          file: shared/codeowners.widgets.yaml

                    recipe: []
                """,
                templates = mapOf(
                    "shared/codeowners.widgets.yaml" to """
                        widgets:
                          - stringParameter:
                              id: codeOwnerTeam
                              name: Included codeowners team
                              default: included-team
                    """
                ),
            )
        }

        exception.message?.contains("Widget parameter ids should be unique") shouldBe true
    }

    "Should resolve suggest options source relative to included widgets file" {
        val fixture = createRecipeFixture(
            recipeYaml = """
                requiredParams:
                  name: Geminio include source test
                  description: Covers options.source inside included widgets file

                widgets:
                  - include:
                      file: shared/codeowners.widgets.yaml

                recipe: []
            """,
            templates = mapOf(
                "shared/codeowners.widgets.yaml" to """
                    widgets:
                      - suggestParameter:
                          id: codeOwnerPath
                          name: Codeowners path
                          sealed: true
                          options:
                            source: options/codeowners_paths.csv
                """,
                "shared/options/codeowners_paths.csv" to """
                    value,label
                    /feature,Feature
                    /service,Service
                """
            ),
        )

        fixture.recipe.widgetsSection shouldBe WidgetsSection(
            parameters = listOf(
                RecipeParameter.SuggestParameter(
                    id = "codeOwnerPath",
                    name = "Codeowners path",
                    help = null,
                    visibilityExpression = null,
                    availabilityExpression = null,
                    default = null,
                    isSealed = true,
                    suggestExpression = null,
                    options = listOf(
                        SuggestParameterOption(value = "/feature", label = "Feature"),
                        SuggestParameterOption(value = "/service", label = "Service"),
                    ),
                )
            )
        )
    }
})

private fun RecipeExpressionCommand.asExpression() =
    RecipeExpression(listOf(this))
