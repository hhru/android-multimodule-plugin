package ru.hh.android.plugin.core.model.jira


enum class JiraCustomField(val remoteKey: String) {

    LABELS("labels"),
    DEVELOPMENT_TEAM("customfield_10961"),
    STORY_POINTS("customfield_11212"),
    EPIC_LINK("customfield_12311")

}