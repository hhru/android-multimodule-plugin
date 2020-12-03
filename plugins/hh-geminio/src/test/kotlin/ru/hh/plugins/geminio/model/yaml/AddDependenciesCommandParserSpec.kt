package ru.hh.plugins.geminio.model.yaml

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.yaml.snakeyaml.Yaml


class AddDependenciesCommandParserSpec : FreeSpec({

    fun String.prepareAddDependenciesCommand(): Map<String, Any> =
        (Yaml().load<Map<String, Any>>(this)["recipe"] as List<Map<String, Map<String, Any>>>)[0]["addDependencies"]!!



    "Some" {
        val addDependenciesCommand = """
        recipe:
            - addDependencies:
                typeForAll: compileOnly
                dependencies:
                    - mavenArtifact:
                        type: implementation
                        notation: org.company:artifact:1.0
                    - mavenArtifact: org.company:artifact:1.0
                    - project:
                        type: api
                        name: shared-core-model
                    - project: shared-core-model
                    - libsConstant:
                        type: compileOnly
                        value: Libs.jetpack.viewModel
                    - libsConstant: Libs.jetpack.viewModel
        """.prepareAddDependenciesCommand()

        val dependencies = addDependenciesCommand["dependencies"] as List<Map<String, Any>>

        addDependenciesCommand["typeForAll"] shouldBe "compileOnly"
        dependencies.size shouldBe 6

    }

})