package ru.hh.android.plugin.services.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepositoryManager
import ru.hh.android.plugin.PluginConstants.MAIN_REPOSITORY_NAME
import ru.hh.android.plugin.extensions.EMPTY


@Service
class GitService(
    private val project: Project
) {

    companion object {
        private val PORTFOLIO_BRANCH_REGEX = "((^PORTFOLIO-[0-9]+)((_)*[a-zA-Z]+)*)".toRegex()

        fun getInstance(project: Project): GitService = project.service()
    }

    private val repositoryManager: GitRepositoryManager
        get() = GitRepositoryManager.getInstance(project)


    fun checkoutMobForMergeDevelopToPortfolio(mobIssueKey: String, portfolioKey: String) {
        val newBranchName = "${mobIssueKey}__merge_develop_to_${portfolioKey}"
        val repository = repositoryManager.repositories.find { it.presentableUrl.endsWith(MAIN_REPOSITORY_NAME) }
            ?: throw IllegalStateException("Can't find ${MAIN_REPOSITORY_NAME} repository in Git")

        GitBrancher.getInstance(project).checkoutNewBranch(newBranchName, listOf(repository))
    }

    fun extractPortfolioBranchName(): String {
        val repositories = repositoryManager.repositories
        val currentBranchesNames = repositories.mapNotNull { it.currentBranch?.name }
        val hasPortfolioBranch = currentBranchesNames.any { PORTFOLIO_BRANCH_REGEX.matches(it) }

        return if (hasPortfolioBranch) {
            PORTFOLIO_BRANCH_REGEX.find(currentBranchesNames.firstOrNull().orEmpty())?.groups?.get(2)?.value.orEmpty()
        } else {
            String.EMPTY
        }
    }

}