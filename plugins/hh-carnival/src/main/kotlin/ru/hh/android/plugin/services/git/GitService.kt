package ru.hh.android.plugin.services.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepositoryManager
import ru.hh.android.plugin.PluginConstants.MAIN_REPOSITORY_NAME
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.logger.HHLogger

@Service(Service.Level.PROJECT)
class GitService(
    private val project: Project
) {

    companion object {
        private val PORTFOLIO_BRANCH_REGEX = "((^PORTFOLIO-[0-9]+)((_)*[0-9a-zA-Z\\-.]+)*)".toRegex()

        fun getInstance(project: Project): GitService = project.service()
    }

    private val repositoryManager: GitRepositoryManager
        get() = GitRepositoryManager.getInstance(project)

    fun checkoutMobForMergeDevelopToPortfolio(mobIssueKey: String, portfolioKey: String) {
        HHLogger.d("checkoutMobForMergeDevelopToPortfolio | mob issue: $mobIssueKey, portfolio: $portfolioKey")
        val newBranchName = "${mobIssueKey}__merge_develop_to_$portfolioKey"
        HHLogger.d("\tnewBranchName = $newBranchName")
        val repository = repositoryManager.repositories.find { it.presentableUrl.endsWith(MAIN_REPOSITORY_NAME) }
            ?: throw IllegalStateException("Can't find $MAIN_REPOSITORY_NAME repository in Git")

        HHLogger.d("\trepository = ${repository.presentableUrl}")
        GitBrancher.getInstance(project).checkoutNewBranch(newBranchName, listOf(repository))
    }

    fun extractPortfolioBranchName(): String {
        HHLogger.d("Try to extractPortfolioBranchName")
        val repositories = repositoryManager.repositories
        HHLogger.d("\tis repositories empty: ${repositories.isEmpty()}")
        val currentBranchName = repositories
            .firstOrNull { PORTFOLIO_BRANCH_REGEX.matches(it.currentBranch?.name.orEmpty()) }
            ?.currentBranchName
        val hasPortfolioBranch = currentBranchName != null
        HHLogger.d("\thasPortfolioBranch: $hasPortfolioBranch")

        return if (hasPortfolioBranch) {
            PORTFOLIO_BRANCH_REGEX.find(currentBranchName.orEmpty())?.groups?.get(2)?.value.orEmpty()
                .also { HHLogger.d("Extracted branch name: $it") }
        } else {
            String.EMPTY
        }
    }
}
