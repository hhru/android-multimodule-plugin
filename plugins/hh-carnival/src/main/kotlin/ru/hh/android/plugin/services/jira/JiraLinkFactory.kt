package ru.hh.android.plugin.services.jira

import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.core.model.jira.JiraLinkType

@Service(Service.Level.PROJECT)
class JiraLinkFactory {

    companion object {
        fun getInstance(project: Project): JiraLinkFactory = project.service()
    }

    fun issueConsistsInAnotherIssue(consistingIssueKey: String, includingIssueKey: String): LinkIssuesInput {
        return LinkIssuesInput(
            includingIssueKey,
            consistingIssueKey,
            JiraLinkType.INCLUSION.remoteName,
            null
        )
    }
}
