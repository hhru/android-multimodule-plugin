package ru.hh.plugins.geminio.sdk.form

import ru.hh.plugins.extensions.SLASH
import ru.hh.plugins.extensions.toFilePathFromGradleModulePath
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint
import java.io.File
import java.net.URI

/**
 * Validates Geminio string parameter constraints outside Swing code so custom dialogs and tests
 * share the same behavior.
 */
internal object GeminioStringConstraintValidator {

    private val androidResourceNameRegex = Regex("[a-z][a-z0-9_]*")
    private val classLikeConstraints = setOf(
        StringParameterConstraint.CLASS,
        StringParameterConstraint.ACTIVITY,
    )
    private val packageLikeConstraints = setOf(
        StringParameterConstraint.PACKAGE,
        StringParameterConstraint.APP_PACKAGE,
    )
    private val androidResourceConstraints = setOf(
        StringParameterConstraint.LAYOUT,
        StringParameterConstraint.DRAWABLE,
        StringParameterConstraint.NAVIGATION,
        StringParameterConstraint.VALUES,
        StringParameterConstraint.STRING,
    )

    fun validate(
        field: GeminioFormField.StringField,
        value: String,
        context: GeminioStringConstraintValidationContext = GeminioStringConstraintValidationContext(),
        isValidIdentifier: (String) -> Boolean,
        isQualifiedName: (String) -> Boolean,
    ): String? {
        val constraints = field.constraints.toSet()

        field.constraints
            .asSequence()
            .filterNot { it == StringParameterConstraint.UNIQUE || it == StringParameterConstraint.EXISTS }
            .firstNotNullOfOrNull { constraint ->
                validateSyntax(
                    field = field,
                    value = value,
                    constraint = constraint,
                    isValidIdentifier = isValidIdentifier,
                    isQualifiedName = isQualifiedName,
                )
            }
            ?.let { return it }

        return validateExistence(field, value, constraints, context)
    }

    private fun validateSyntax(
        field: GeminioFormField.StringField,
        value: String,
        constraint: StringParameterConstraint,
        isValidIdentifier: (String) -> Boolean,
        isQualifiedName: (String) -> Boolean,
    ): String? {
        return when (constraint) {
            StringParameterConstraint.NONEMPTY -> validateNonEmpty(field, value)
            in classLikeConstraints -> validateClassLike(field, value, isValidIdentifier, isQualifiedName)
            StringParameterConstraint.KOTLIN_FUNCTION -> {
                validateKotlinFunction(field, value, isValidIdentifier)
            }

            in packageLikeConstraints -> validatePackageLike(field, value, isQualifiedName)
            // Android Studio's wizard treats MODULE as an existence constraint, not as
            // a syntax constraint. Keep that behavior for compatibility with existing recipes.
            StringParameterConstraint.MODULE -> null
            StringParameterConstraint.SOURCE_SET_FOLDER -> validateSourceSetName(field, value)
            in androidResourceConstraints -> validateAndroidResourceName(field, value)
            StringParameterConstraint.URI_AUTHORITY -> validateUriAuthority(field, value)
            else -> null
        }
    }

    private fun validateNonEmpty(
        field: GeminioFormField.StringField,
        value: String,
    ): String? = validationMessageOrNull(value.isBlank(), "'${field.name}' should not be empty")

    private fun validateClassLike(
        field: GeminioFormField.StringField,
        value: String,
        isValidIdentifier: (String) -> Boolean,
        isQualifiedName: (String) -> Boolean,
    ): String? {
        return validationMessageOrNull(
            value.isNotBlank() && value.isValidClassName(isValidIdentifier, isQualifiedName).not(),
            "'${field.name}' should be a valid class name",
        )
    }

    private fun validateKotlinFunction(
        field: GeminioFormField.StringField,
        value: String,
        isValidIdentifier: (String) -> Boolean,
    ): String? {
        return validationMessageOrNull(
            value.isNotBlank() && isValidIdentifier(value).not(),
            "'${field.name}' should be a valid Kotlin function name",
        )
    }

    private fun validatePackageLike(
        field: GeminioFormField.StringField,
        value: String,
        isQualifiedName: (String) -> Boolean,
    ): String? {
        return validationMessageOrNull(
            value.isNotBlank() && isQualifiedName(value).not(),
            "'${field.name}' should be a valid package name",
        )
    }

    private fun validateSourceSetName(
        field: GeminioFormField.StringField,
        value: String,
    ): String? {
        return validationMessageOrNull(
            isInvalidSourceSetName(value),
            "'${field.name}' should be a valid source set name",
        )
    }

    private fun validateAndroidResourceName(
        field: GeminioFormField.StringField,
        value: String,
    ): String? {
        return validationMessageOrNull(
            value.isNotBlank() && androidResourceNameRegex.matches(value).not(),
            "'${field.name}' should be a valid Android resource name",
        )
    }

    private fun validateUriAuthority(
        field: GeminioFormField.StringField,
        value: String,
    ): String? {
        return validationMessageOrNull(
            value.isNotBlank() && value.isValidUriAuthority().not(),
            "'${field.name}' should be a valid URI authority",
        )
    }

    private fun validateExistence(
        field: GeminioFormField.StringField,
        value: String,
        constraints: Set<StringParameterConstraint>,
        context: GeminioStringConstraintValidationContext,
    ): String? {
        val exists = context.exists(value, constraints) ?: return null

        return when {
            StringParameterConstraint.UNIQUE in constraints && exists -> {
                "'${field.name}' should be unique"
            }

            StringParameterConstraint.EXISTS in constraints && exists.not() -> {
                "'${field.name}' should already exist"
            }

            else -> null
        }
    }

    private fun String.isValidClassName(
        isValidIdentifier: (String) -> Boolean,
        isQualifiedName: (String) -> Boolean,
    ): Boolean {
        return if (contains('.')) {
            isQualifiedName(this)
        } else {
            isValidIdentifier(this)
        }
    }

    private fun validationMessageOrNull(shouldFail: Boolean, message: String): String? {
        return if (shouldFail) message else null
    }

    private fun isInvalidSourceSetName(value: String): Boolean {
        return value.isBlank() ||
                value.any { it == '/' || it == '\\' || it.isWhitespace() }
    }

    private fun String.isValidUriAuthority(): Boolean {
        if (isBlank() || any(Char::isWhitespace)) {
            return false
        }

        return runCatching {
            URI("geminio://$this").rawAuthority == this
        }.getOrDefault(false)
    }
}

internal data class GeminioStringConstraintValidationContext(
    val newModuleParentDirectory: File? = null,
    val newModuleName: String? = null,
    val sourceSet: String? = null,
    val sourceCodeFolderName: String? = null,
    val existingModuleNames: Set<String> = emptySet(),
    val sourceRoots: List<File> = emptyList(),
    val resourceDirectories: List<File> = emptyList(),
) {

    fun exists(
        value: String,
        constraints: Set<StringParameterConstraint>,
    ): Boolean? {
        return when {
            StringParameterConstraint.MODULE in constraints -> moduleExists(value)
            StringParameterConstraint.PACKAGE in constraints ||
                    StringParameterConstraint.APP_PACKAGE in constraints -> packageExists(value)

            StringParameterConstraint.SOURCE_SET_FOLDER in constraints -> sourceSetExists(value)
            StringParameterConstraint.LAYOUT in constraints -> resourceExists("layout", value)
            StringParameterConstraint.DRAWABLE in constraints -> resourceExists("drawable", value)
            StringParameterConstraint.NAVIGATION in constraints -> resourceExists("navigation", value)
            StringParameterConstraint.VALUES in constraints -> resourceExists("values", value)
            else -> customExists(value, constraints)
        }
    }

    private fun moduleExists(moduleName: String): Boolean? {
        return when {
            moduleName.isBlank() -> null
            else -> moduleName in existingModuleNames
        }
    }

    private fun packageExists(packageName: String): Boolean? {
        val roots = sourceRoots + listOfNotNull(newModuleSourceRoot())

        return when {
            packageName.isBlank() || roots.isEmpty() -> null
            else -> {
                val packagePath = packageName.replace('.', File.separatorChar)
                roots.any { root -> root.resolve(packagePath).exists() }
            }
        }
    }

    private fun sourceSetExists(sourceSetName: String): Boolean? {
        val moduleRoot = newModuleRoot()

        return when {
            sourceSetName.isBlank() || moduleRoot == null -> null
            else -> moduleRoot.resolve("src").resolve(sourceSetName).exists()
        }
    }

    private fun resourceExists(resourceFolderName: String, resourceName: String): Boolean? {
        if (resourceName.isBlank() || resourceDirectories.isEmpty()) {
            return null
        }

        return resourceDirectories.any { resDirectory ->
            resDirectory.resolve(resourceFolderName)
                .listFiles()
                ?.any { file -> file.nameWithoutExtension == resourceName }
                ?: false
        }
    }

    private fun customExists(
        value: String,
        constraints: Set<StringParameterConstraint>,
    ): Boolean? {
        return when {
            StringParameterConstraint.STRING in constraints -> stringResourceExists(value)
            else -> null
        }
    }

    private fun stringResourceExists(resourceName: String): Boolean? {
        if (resourceName.isBlank() || resourceDirectories.isEmpty()) {
            return null
        }

        return resourceDirectories
            .map { resDirectory -> resDirectory.resolve("values") }
            .flatMap { valuesDirectory -> valuesDirectory.listFiles()?.toList().orEmpty() }
            .any { file -> file.isFile && file.readText().contains("name=\"$resourceName\"") }
    }

    private fun newModuleSourceRoot(): File? {
        val moduleRoot = newModuleRoot()
        val sourceSetName = sourceSet?.takeIf(String::isNotBlank)
        val sourceFolderName = sourceCodeFolderName?.takeIf(String::isNotBlank)

        return if (moduleRoot == null || sourceSetName == null || sourceFolderName == null) {
            null
        } else {
            moduleRoot.resolve("src")
                .resolve(sourceSetName)
                .resolve(sourceFolderName)
        }
    }

    private fun newModuleRoot(): File? {
        val moduleName = newModuleName?.takeIf(String::isNotBlank) ?: return null
        return newModuleParentDirectory?.resolve(moduleName.toFilePathFromGradleModulePath())
    }
}

internal object GeminioGradleModulePathNormalizer {

    fun normalize(
        projectBasePath: File,
        moduleRootPaths: Iterable<File>,
    ): Set<String> {
        val normalizedProjectBasePath = projectBasePath.normalize()

        return moduleRootPaths
            .map(File::normalize)
            .mapNotNull { moduleRootPath ->
                moduleRootPath.relativeToOrNull(normalizedProjectBasePath)
                    ?.path
                    ?.toGradleModulePath()
            }
            .toSet()
    }

    private fun String.toGradleModulePath(): String? {
        val normalizedPath = replace(File.separatorChar, Char.SLASH)
            .replace('\\', Char.SLASH)
            .trim(Char.SLASH)
        val moduleRootPath = normalizedPath
            .substringBefore("/src/", missingDelimiterValue = normalizedPath)
            .removeSuffix("/src")

        return moduleRootPath
            .takeIf(String::isNotBlank)
            ?.replace(Char.SLASH, ':')
    }
}
