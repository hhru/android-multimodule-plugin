package ru.hh.plugins.geminio.sdk.execution

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.form.GeminioForm
import ru.hh.plugins.geminio.sdk.form.GeminioFormField
import ru.hh.plugins.geminio.sdk.form.GeminioFormFieldOrigin
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession

internal class GeminioTemplateParametersFactorySpec : FreeSpec({

    "should merge session values with hardcoded Geminio parameters" {
        val parameters = GeminioTemplateParametersFactory.create(
            session = createSession().apply {
                setStringValue("moduleName", "payments")
                setBooleanValue("includeFactory", false)
            },
            packageName = "ru.hh.payments",
            applicationPackageName = "ru.hh.app",
            currentDirPath = "/workspace/src/main/kotlin/ru/hh/payments/presentation",
            additionalParameters = mapOf(
                "applicationModules" to listOf("app", "staff"),
            ),
        )

        parameters.shouldContainAll(mapOf(
            "moduleName" to "payments",
            "includeFactory" to false,
            "applicationModules" to listOf("app", "staff"),
            HardcodedParams.PACKAGE_NAME to "ru.hh.payments",
            HardcodedParams.APPLICATION_PACKAGE to "ru.hh.app",
            HardcodedParams.CURRENT_DIR_PACKAGE_NAME to "ru.hh.payments.presentation",
        ))
    }

    "should fallback to dotted current directory path when package name is not present in it" {
        val parameters = GeminioTemplateParametersFactory.create(
            session = createSession(),
            packageName = "ru.hh.payments",
            applicationPackageName = "ru.hh.app",
            currentDirPath = "/workspace/custom/location",
        )

        parameters[HardcodedParams.CURRENT_DIR_PACKAGE_NAME] shouldBe ".workspace.custom.location"
    }
})

private fun createSession(): GeminioFormSession {
    return GeminioFormSession(
        GeminioForm(
            fields = listOf(
                GeminioFormField.StringField(
                    id = "moduleName",
                    name = "Module name",
                    help = null,
                    origin = GeminioFormFieldOrigin.WIDGET,
                    defaultValue = "feature",
                ),
                GeminioFormField.BooleanField(
                    id = "includeFactory",
                    name = "Include factory",
                    help = null,
                    origin = GeminioFormFieldOrigin.WIDGET,
                    defaultValue = true,
                ),
            ),
        ),
    )
}
