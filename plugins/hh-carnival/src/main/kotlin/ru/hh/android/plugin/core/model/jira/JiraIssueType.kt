package ru.hh.android.plugin.core.model.jira

enum class JiraIssueType(
    val id: Long
) {

    BUG(id = 1),
    NEW_FUNCTION(id = 2),
    TASK(id = 3),
    IMPROVEMENT(id = 4),
    PROCESS_ORGANIZATION(id = 98)
}
