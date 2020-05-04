package ru.hh.android.plugin.services.git

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import ru.hh.android.plugin.extensions.EMPTY


@Service
class GitService(
    private val project: Project
) {

    companion object {
        private val PORTFOLIO_BRANCH_REGEX = "((^PORTFOLIO-[0-9]+)((_)*[a-zA-Z]+)*)".toRegex()

        fun newInstance(project: Project): GitService = project.service()
    }


    fun extractPortfolioBranchName(): String {
        val repositories = GitRepositoryManager.getInstance(project).repositories
        val currentRepository = if (repositories.isNotEmpty()) repositories[0] else null
        val currentBranchName = currentRepository?.currentBranch?.name ?: String.EMPTY

        return PORTFOLIO_BRANCH_REGEX.find(currentBranchName)?.groups?.get(2)?.value ?: String.EMPTY
    }

}