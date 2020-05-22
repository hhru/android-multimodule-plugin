package ru.hh.android.plugin.inspections

import com.android.tools.idea.lint.LintIdeIssueRegistry
import com.android.tools.lint.detector.api.Issue
import org.jetbrains.android.inspections.lint.AndroidLintInspectionBase


@Suppress("UnstableApiUsage")
abstract class AutoRegisterAndroidLintInspection(
    displayName: String,
    issue: Issue
) : AndroidLintInspectionBase(displayName, issue) {

    init {
        val registry = LintIdeIssueRegistry()

        val registryIssue = registry.getIssue(issue.id)
        if (registryIssue == null) {
            val issues = registry.issues as? MutableList<Issue>
            issues?.add(issue)
        }
    }

}