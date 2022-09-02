package ru.hh.plugins.geminio.services

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.intellij.openapi.project.Project
import ru.hh.plugins.utils.notifications.Debug

/**
 * Special stub for [ProjectSyncInvoker] to skip synchronization process after files creation.
 */
internal class StubProjectSyncInvoker : ProjectSyncInvoker {
    override fun syncProject(project: Project) {
        // do nothing
        Debug.info("StubProjectSyncInvoker -> skip synchronization process after files creation")
    }
}