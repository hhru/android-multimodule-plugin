package ru.hh.plugins.geminio.sdk.form

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint
import java.nio.file.Files

internal class GeminioStringConstraintValidatorSpec : FreeSpec({

    "should validate constraints that were previously ignored by custom dialog" {
        validate(
            field = stringField(
                name = "Application package",
                constraints = listOf(StringParameterConstraint.APP_PACKAGE),
            ),
            value = "ru.hh.-broken",
        ) shouldBe "'Application package' should be a valid package name"

        validate(
            field = stringField(
                name = "Layout name",
                constraints = listOf(StringParameterConstraint.LAYOUT),
            ),
            value = "FeatureScreen",
        ) shouldBe "'Layout name' should be a valid Android resource name"

        validate(
            field = stringField(
                name = "URI authority",
                constraints = listOf(StringParameterConstraint.URI_AUTHORITY),
            ),
            value = "bad authority",
        ) shouldBe "'URI authority' should be a valid URI authority"

        validate(
            field = stringField(
                name = "Factory function",
                constraints = listOf(StringParameterConstraint.KOTLIN_FUNCTION),
            ),
            value = "1factory",
        ) shouldBe "'Factory function' should be a valid Kotlin function name"
    }

    "should keep Android Studio module syntax compatibility" {
        val field = stringField(
            name = "Module name",
            constraints = listOf(StringParameterConstraint.MODULE),
        )

        validate(field, ":feature:auth") shouldBe null
    }

    "should enforce unique module names from exact project module names" {
        withTempDirectory { root ->
            root.resolve("payments").mkdirs()
            val context = GeminioStringConstraintValidationContext(
                newModuleParentDirectory = root,
                existingModuleNames = setOf("checkout", ":legacy"),
            )
            val field = stringField(
                name = "Module name",
                constraints = listOf(
                    StringParameterConstraint.MODULE,
                    StringParameterConstraint.UNIQUE,
                ),
            )

            validate(field, "payments", context) shouldBe null
            validate(field, "checkout", context) shouldBe "'Module name' should be unique"
            validate(field, ":legacy", context) shouldBe "'Module name' should be unique"
            validate(field, ":checkout", context) shouldBe null
            validate(field, "profile", context) shouldBe null
        }
    }

    "should enforce unique module names from normalized Gradle module root paths" {
        withTempDirectory { root ->
            val existingModuleNames = GeminioGradleModulePathNormalizer.normalize(
                projectBasePath = root,
                moduleRootPaths = listOf(
                    root.resolve("applicant/feature/notifications-list/src/unitTest"),
                    root.resolve("applicant/feature/part-time-job/src/customBenchmark"),
                    root.resolve("hr-mobile/feature/part-time"),
                ),
            )
            val context = GeminioStringConstraintValidationContext(
                existingModuleNames = existingModuleNames,
            )
            val field = stringField(
                name = "Module name",
                constraints = listOf(
                    StringParameterConstraint.MODULE,
                    StringParameterConstraint.UNIQUE,
                ),
            )

            existingModuleNames.contains("applicant:feature:notifications-list") shouldBe true
            existingModuleNames.contains("applicant:feature:notifications-list:src:unitTest") shouldBe false
            existingModuleNames.contains("applicant:feature:part-time-job") shouldBe true
            existingModuleNames.contains("hr-mobile:feature:part-time") shouldBe true
            validate(field, "applicant:feature:part-time-job", context) shouldBe "'Module name' should be unique"
            validate(field, "hr-mobile:feature:part-time", context) shouldBe "'Module name' should be unique"
            validate(field, "applicant:feature:new", context) shouldBe null
        }
    }

    "should enforce resource unique and exists constraints" {
        withTempDirectory { root ->
            val resDirectory = root.resolve("src/main/res")
            resDirectory.resolve("layout").mkdirs()
            resDirectory.resolve("layout/fragment_blank.xml").writeText("<FrameLayout />")

            val context = GeminioStringConstraintValidationContext(
                resourceDirectories = listOf(resDirectory),
            )
            val uniqueLayoutField = stringField(
                name = "Layout name",
                constraints = listOf(
                    StringParameterConstraint.LAYOUT,
                    StringParameterConstraint.UNIQUE,
                ),
            )
            val existingLayoutField = stringField(
                name = "Existing layout",
                constraints = listOf(
                    StringParameterConstraint.LAYOUT,
                    StringParameterConstraint.EXISTS,
                ),
            )

            validate(uniqueLayoutField, "fragment_blank", context) shouldBe "'Layout name' should be unique"
            validate(uniqueLayoutField, "new_fragment", context) shouldBe null
            validate(existingLayoutField, "missing", context) shouldBe "'Existing layout' should already exist"
            validate(existingLayoutField, "fragment_blank", context) shouldBe null
        }
    }

    "should enforce package uniqueness in new module source root" {
        withTempDirectory { root ->
            root.resolve("applicant/feature/auth/src/main/kotlin/ru/hh/feature").mkdirs()
            val context = GeminioStringConstraintValidationContext(
                newModuleParentDirectory = root,
                newModuleName = "applicant:feature:auth",
                sourceSet = "main",
                sourceCodeFolderName = "kotlin",
            )
            val field = stringField(
                name = "Package name",
                constraints = listOf(
                    StringParameterConstraint.PACKAGE,
                    StringParameterConstraint.UNIQUE,
                ),
            )

            validate(field, "ru.hh.feature", context) shouldBe "'Package name' should be unique"
            validate(field, "ru.hh.new_feature", context) shouldBe null
        }
    }

    "should enforce package existence constraints in existing source roots" {
        withTempDirectory { root ->
            val sourceRoot = root.resolve("src/main/kotlin")
            sourceRoot.resolve("ru/hh/existing").mkdirs()
            val context = GeminioStringConstraintValidationContext(
                sourceRoots = listOf(sourceRoot),
            )
            val uniquePackageField = stringField(
                name = "Package name",
                constraints = listOf(
                    StringParameterConstraint.PACKAGE,
                    StringParameterConstraint.UNIQUE,
                ),
            )
            val existingPackageField = stringField(
                name = "Existing package",
                constraints = listOf(
                    StringParameterConstraint.PACKAGE,
                    StringParameterConstraint.EXISTS,
                ),
            )

            validate(uniquePackageField, "ru.hh.existing", context) shouldBe "'Package name' should be unique"
            validate(uniquePackageField, "ru.hh.new_feature", context) shouldBe null
            validate(existingPackageField, "ru.hh.missing", context) shouldBe "'Existing package' should already exist"
            validate(existingPackageField, "ru.hh.existing", context) shouldBe null
        }
    }
})

private fun stringField(
    name: String,
    constraints: List<StringParameterConstraint>,
): GeminioFormField.StringField {
    return GeminioFormField.StringField(
        id = name,
        name = name,
        help = null,
        origin = GeminioFormFieldOrigin.WIDGET,
        constraints = constraints,
    )
}

private fun validate(
    field: GeminioFormField.StringField,
    value: String,
    context: GeminioStringConstraintValidationContext = GeminioStringConstraintValidationContext(),
): String? {
    return GeminioStringConstraintValidator.validate(
        field = field,
        value = value,
        context = context,
        isValidIdentifier = { candidate -> candidate.matches(identifierRegex) },
        isQualifiedName = { candidate ->
            candidate.split('.').all { segment -> segment.matches(identifierRegex) }
        },
    )
}

private fun withTempDirectory(action: (java.io.File) -> Unit) {
    val root = Files.createTempDirectory("geminio-validator").toFile()
    try {
        action(root)
    } finally {
        root.deleteRecursively()
    }
}

private val identifierRegex = Regex("[A-Za-z_][A-Za-z0-9_]*")
