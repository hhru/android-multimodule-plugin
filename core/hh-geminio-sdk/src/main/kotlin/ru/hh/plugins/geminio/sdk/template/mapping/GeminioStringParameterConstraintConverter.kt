package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.Constraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioStringParameterConstraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioStringParameterConstraint.*


fun GeminioStringParameterConstraint.toAndroidStudioStringParameterConstraint(): Constraint {
    return when (this) {
        UNIQUE -> Constraint.UNIQUE
        EXISTS -> Constraint.EXISTS
        NONEMPTY -> Constraint.NONEMPTY
        ACTIVITY -> Constraint.ACTIVITY
        CLASS -> Constraint.CLASS
        PACKAGE -> Constraint.PACKAGE
        APP_PACKAGE -> Constraint.APP_PACKAGE
        MODULE -> Constraint.MODULE
        LAYOUT -> Constraint.LAYOUT
        DRAWABLE -> Constraint.DRAWABLE
        NAVIGATION -> Constraint.NAVIGATION
        VALUES -> Constraint.VALUES
        SOURCE_SET_FOLDER -> Constraint.SOURCE_SET_FOLDER
        STRING -> Constraint.STRING
        URI_AUTHORITY -> Constraint.URI_AUTHORITY
        KOTLIN_FUNCTION -> Constraint.KOTLIN_FUNCTION
    }
}