package ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.ACTIVITY
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.APP_PACKAGE
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.CLASS
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.DRAWABLE
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.EXISTS
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.KOTLIN_FUNCTION
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.LAYOUT
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.MODULE
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.NAVIGATION
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.NONEMPTY
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.PACKAGE
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.SOURCE_SET_FOLDER
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.STRING
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.UNIQUE
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.URI_AUTHORITY
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint.VALUES
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameterConstraint


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameterConstraint].
 */
internal fun StringParameterConstraint.toAndroidStudioTemplateStringParameterConstraint(): AndroidStudioTemplateStringParameterConstraint {
    return when (this) {
        UNIQUE -> AndroidStudioTemplateStringParameterConstraint.UNIQUE
        EXISTS -> AndroidStudioTemplateStringParameterConstraint.EXISTS
        NONEMPTY -> AndroidStudioTemplateStringParameterConstraint.NONEMPTY
        ACTIVITY -> AndroidStudioTemplateStringParameterConstraint.ACTIVITY
        CLASS -> AndroidStudioTemplateStringParameterConstraint.CLASS
        PACKAGE -> AndroidStudioTemplateStringParameterConstraint.PACKAGE
        APP_PACKAGE -> AndroidStudioTemplateStringParameterConstraint.APP_PACKAGE
        MODULE -> AndroidStudioTemplateStringParameterConstraint.MODULE
        LAYOUT -> AndroidStudioTemplateStringParameterConstraint.LAYOUT
        DRAWABLE -> AndroidStudioTemplateStringParameterConstraint.DRAWABLE
        NAVIGATION -> AndroidStudioTemplateStringParameterConstraint.NAVIGATION
        VALUES -> AndroidStudioTemplateStringParameterConstraint.VALUES
        SOURCE_SET_FOLDER -> AndroidStudioTemplateStringParameterConstraint.SOURCE_SET_FOLDER
        STRING -> AndroidStudioTemplateStringParameterConstraint.STRING
        URI_AUTHORITY -> AndroidStudioTemplateStringParameterConstraint.URI_AUTHORITY
        KOTLIN_FUNCTION -> AndroidStudioTemplateStringParameterConstraint.KOTLIN_FUNCTION
    }
}