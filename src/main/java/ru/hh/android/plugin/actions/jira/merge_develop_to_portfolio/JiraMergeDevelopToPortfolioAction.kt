package ru.hh.android.plugin.actions.jira.merge_develop_to_portfolio

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.extensions.checkInsidePortfolioBranch
import ru.hh.android.plugin.extensions.getCurrentPortfolioBranchName
import ru.hh.android.plugin.services.jira.JiraRestClientService
import ru.hh.android.plugin.utils.PluginBundle
import ru.hh.android.plugin.utils.notifyInfo


class JiraMergeDevelopToPortfolioAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = e.checkInsidePortfolioBranch()
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val issueKey = JiraRestClientService.newInstance(project)
                .createMergeDevelopToPortfolioIssue(e.getCurrentPortfolioBranchName())

            project.notifyInfo(PluginBundle.message("antiroutine.jira.merge_develop_into_portfolio.success.0", issueKey))
        }
    }

}