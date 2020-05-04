package ru.hh.android.plugin.services.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.config.JiraSettingsConfig
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import java.net.URI


@Service
class JiraRestClientService(
    private val project: Project
) {

    companion object {
        fun newInstance(project: Project): JiraRestClientService = project.service()
    }


    private val jiraSettings get() = JiraSettingsConfig.newInstance(project).getJiraSettings()
    private val jiraIssueFactory by lazy { JiraIssueFactory.newInstance(project) }
    private val jiraLinkFactory by lazy { JiraLinkFactory.newInstance(project) }

    private val jiraRestClient: JiraRestClient
        get() {
            return AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(
                    URI.create(jiraSettings.hostName),
                    jiraSettings.username,
                    jiraSettings.password.toString()
                )
        }


    @Suppress("UnstableApiUsage")
    fun createMergeDevelopToPortfolioIssue(portfolioKey: String): String {
        val issueClient = jiraRestClient.issueClient
        val issueKey = issueClient.createIssue(
            jiraIssueFactory.mergeDevelopToPortfolioIssue(
                portfolioKey = portfolioKey,
                creatorName = jiraSettings.username,
                developmentTeam = JiraDevelopmentTeam.MOBILE_CORE
            )
        ).claim().key

        val linkIssueInput = jiraLinkFactory.issueConsistsInAnotherIssue(
            consistingIssueKey = issueKey,
            includingIssueKey = portfolioKey
        )
        issueClient.linkIssue(linkIssueInput).claim()

        return issueKey
    }

}