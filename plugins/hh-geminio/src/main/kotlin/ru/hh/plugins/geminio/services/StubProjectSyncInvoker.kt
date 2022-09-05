package ru.hh.plugins.geminio.services

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.intellij.openapi.project.Project
import ru.hh.plugins.logger.HHLogger

/**
 * Special stub for [ProjectSyncInvoker] to skip synchronization process after files creation.
 */
internal class StubProjectSyncInvoker : ProjectSyncInvoker {
    override fun syncProject(project: Project) {
        // do nothing
        HHLogger.d("StubProjectSyncInvoker -> skip synchronization process after files creation")
    }
}