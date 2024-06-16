package ru.hh.android.plugin.core.model.jira

/**
 * Development teams for creating issues in JIRA
 */
enum class JiraDevelopmentTeam(
    val value: String,
    val comboBoxLabel: String
) {

    MOBILE_CORE(value = "34193", comboBoxLabel = "Mobile Core"),
    MOBILE_PRODUCTS(value = "32682", comboBoxLabel = "Mobile Products"),
    M1(value = "33112", comboBoxLabel = "Mobile First (M1)")

}
