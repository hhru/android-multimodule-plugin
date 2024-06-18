package ru.hh.android.plugin.services.jira

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.core.model.jira.JiraCustomField
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.android.plugin.core.model.jira.JiraEpicLink
import ru.hh.android.plugin.core.model.jira.JiraIssueLabel
import ru.hh.android.plugin.core.model.jira.JiraIssueType
import ru.hh.android.plugin.core.model.jira.JiraProjectKey
import ru.hh.android.plugin.core.model.jira.JiraStoryPoints

@Service(Service.Level.PROJECT)
class JiraIssueFactory {

    companion object {
        private const val OPTION_VALUE_ID_KEY = "id"

        fun getInstance(project: Project): JiraIssueFactory = project.service()
    }

    fun mergeDevelopToPortfolioIssue(
        portfolioKey: String,
        creatorName: String,
        developmentTeam: JiraDevelopmentTeam
    ): IssueInput {
        return createJiraIssueInput(
            projectKey = JiraProjectKey.MOB,
            issueType = JiraIssueType.TASK,
            summary = "[An] Merge develop to $portfolioKey",
            description = "Очередной merge develop-ветки в портфельную ветку $portfolioKey",
            assigneeName = creatorName,
            reporterName = creatorName,
            developmentTeam = developmentTeam,
            epicLink = JiraEpicLink.ANDROID_APPLICANT,
            storyPoints = JiraStoryPoints.HALF,
            labels = listOf(JiraIssueLabel.ANDROID, JiraIssueLabel.ANDROID_APP)
        )
    }

    private fun createJiraIssueInput(
        projectKey: JiraProjectKey,
        issueType: JiraIssueType,
        summary: String,
        description: String,
        assigneeName: String,
        reporterName: String,
        developmentTeam: JiraDevelopmentTeam,
        epicLink: JiraEpicLink,
        storyPoints: JiraStoryPoints,
        labels: List<JiraIssueLabel>
    ): IssueInput {
        return IssueInputBuilder().apply {
            setProjectKey(projectKey.key)
            setIssueTypeId(issueType.id)
            setSummary(summary)
            setDescription(description)
            setAssigneeName(assigneeName)
            setReporterName(reporterName)
            if (labels.isNotEmpty()) {
                setFieldValue(JiraCustomField.LABELS.remoteKey, labels.map { it.text })
            }
            setFieldValue(JiraCustomField.STORY_POINTS.remoteKey, storyPoints.value)
            setFieldValue(
                JiraCustomField.DEVELOPMENT_TEAM.remoteKey,
                ComplexIssueInputFieldValue(
                    mapOf(
                        OPTION_VALUE_ID_KEY to developmentTeam.value
                    )
                )
            )
            setFieldValue(JiraCustomField.EPIC_LINK.remoteKey, epicLink.value)
        }.build()
    }
}
