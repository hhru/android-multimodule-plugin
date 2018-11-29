package ru.hh.android.plugins.android_feature_module.actions

import com.google.common.base.Preconditions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.build_tasks.*
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import ru.hh.android.plugins.android_feature_module.wizard.AndroidFeatureModuleWizardDialog
import ru.hh.android.plugins.android_feature_module.wizard.AndroidFeatureModuleWizardModel
import kotlin.system.measureTimeMillis


class CreateAndroidFeatureModuleAction
    : AnAction("Create Android feature module"),
        AndroidFeatureModuleWizardDialog.OnFinishButtonClickListener {

    private lateinit var dataContext: DataContext


    override fun update(event: AnActionEvent) {
        super.update(event)

        this.dataContext = event.dataContext
        ProjectInfo.init(Preconditions.checkNotNull<Project>(CommonDataKeys.PROJECT.getData(dataContext)))
    }


    override fun actionPerformed(event: AnActionEvent) {
        val wizard = AndroidFeatureModuleWizardDialog(this)
        wizard.show()
    }


    override fun onFinishButtonClicked(model: AndroidFeatureModuleWizardModel) {
        println("============= CLICK ON FINISH_BUTTON ==============")
        val time = measureTimeMillis {
            val config = createBuildTasksConfig(model)

            val tasks = mutableListOf(
                    DirsStructureCreationTask(),
                    AndroidModuleBaseFileCreationTask(),
                    FeatureModuleFilesCreationTask(),
                    ChangeSettingsGradleTask(),
                    AddModuleIntoToothpickAnnotationsConfigTask(),
                    AddModuleIntoDependenciesBlockTask()
            ).apply {
                if (config.enableMoxy) {
                    this += ChangeMoxyReflectorStubTask()
                }
            }

            for (buildTask in tasks) {
                buildTask.performAction(config)
            }
        }
        println("============= END CLICK HANDLE [time: $time ms] ==============")
    }


    private fun createBuildTasksConfig(model: AndroidFeatureModuleWizardModel): BuildTasksConfig {
        val mainParams = model.mainParams ?: throw IllegalStateException("TODO") // todo.

        return BuildTasksConfig(
                libraryName = mainParams.libraryName,
                moduleName = mainParams.moduleName,
                packageName = mainParams.packageName,
                moduleType = mainParams.moduleType,
                enableMoxy = mainParams.enableMoxy,
                addUIModuleDependencies = mainParams.addUIModuleDependencies,
                needCreateAPIInterface = mainParams.needCreateAPIInterface,
                needCreateRepositoryWithInteractor = mainParams.needCreateRepositoryWithInteractor,
                librariesModules = model.enabledModules.toList(),
                applicationsModules = model.enabledApplications.toList()
        )
    }


}