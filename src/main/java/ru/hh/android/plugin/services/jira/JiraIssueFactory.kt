package ru.hh.android.plugin.services.jira

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.intellij.openapi.components.Service
import ru.hh.android.plugin.core.model.jira.*
import ru.hh.android.plugin.utils.PluginBundle.message


@Service
class JiraIssueFactory {

    companion object {
        private const val CUSTOM_FIELD_ID_KEY = "id"
    }


    fun mergeDevelopToPortfolioIssue(
        portfolioKey: String,
        creatorName: String,
        developmentTeam: JiraDevelopmentTeam
    ): IssueInput {
        return createJiraIssueInput(
            projectKey = JiraProjectKey.MOB,
            issueType = JiraIssueType.TASK,
            summary = message("antiroutine.jira.merge_develop_into_portfolio.summary.0", portfolioKey),
            description = message("antiroutine.jira.merge_develop_into_portfolio.description.0", portfolioKey),
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
                        CUSTOM_FIELD_ID_KEY to developmentTeam.value
                    )
                )
            )
            setFieldValue(JiraCustomField.EPIC_LINK.remoteKey, epicLink.value)
        }.build()
    }

}