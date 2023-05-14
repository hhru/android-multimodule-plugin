package ru.hh.android.plugin.actions.jira.merge_develop_to_portfolio

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.extensions.checkInsidePortfolioBranch
import ru.hh.android.plugin.extensions.getCurrentPortfolioBranchName
import ru.hh.android.plugin.services.git.GitService
import ru.hh.android.plugin.services.jira.JiraRestClientService

class JiraMergeDevelopToPortfolioAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = e.checkInsidePortfolioBranch()
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val portfolioBranchName = e.getCurrentPortfolioBranchName()
            val mobIssueKey = JiraRestClientService.getInstance(project)
                .createMergeDevelopToPortfolioIssue(portfolioBranchName)
            GitService.getInstance(project)
                .checkoutMobForMergeDevelopToPortfolio(mobIssueKey, portfolioBranchName)
        }
    }
}
