package ru.hh.plugins.geminio.services.android

import com.android.tools.idea.projectsystem.AndroidModulePaths
import ru.hh.plugins.geminio.common.extensions.toFilePathFromGradleModulePath
import ru.hh.plugins.geminio.common.extensions.toSlashedFilePath
import ru.hh.plugins.geminio.logger.HHLogger
import java.io.File

/**
 * Android-specific module path adapter for Geminio module generation.
 */
internal class GeminioAndroidModulePaths(
    private val basePath: String,
    private val moduleName: String,
    sourceSetConfig: GeminioSourceSetConfig,
) : AndroidModulePaths {

    private companion object {
        const val LOG_TAG = "GeminioAndroidModulePaths"
    }

    private val sourceSet = sourceSetConfig.sourceSet
    private val sourceCodeFolderName = sourceSetConfig.sourceCodeFolderName

    private val manifestFolderPath = "src/$sourceSet"
    private val uiTestsFolderPath = "src/androidTest"
    private val unitTestsFolderPath = "src/test"
    private val resFolderPath = "src/$sourceSet/res"
    private val aidlFolderPath = "src/$sourceSet/aidl"
    private val sourcesFolderPath = "src/$sourceSet/$sourceCodeFolderName"
    private val moduleRootPath: String get() = "$basePath/${moduleName.toFilePathFromGradleModulePath()}"

    override val manifestDirectory: File
        get() {
            val manifestDirectoryPath = "$moduleRootPath/$manifestFolderPath"
            log("getter for manifestDirectory | path: $manifestDirectoryPath")
            return File(manifestDirectoryPath)
        }

    override val moduleRoot: File
        get() {
            val moduleRootDirPath = moduleRootPath
            log("getter for moduleRoot | path: $moduleRootDirPath")
            return File(moduleRootDirPath)
        }

    override val resDirectories: List<File>
        get() {
            val resDirectoryPath = "$moduleRootPath/$resFolderPath"
            log("getter for resDirectories | path: $resDirectoryPath")
            return listOf(File(resDirectoryPath))
        }

    override fun getAidlDirectory(packageName: String?): File {
        val aidlDirectoryPath = "$moduleRootPath/$aidlFolderPath" + packageName?.toSlashedFilePath().orEmpty()
        log("getter for getAidlDirectory(packageName: $packageName) | path: $aidlDirectoryPath")
        return File(aidlDirectoryPath)
    }

    override fun getSrcDirectory(packageName: String?): File {
        val srcDirectoryPath = "$moduleRootPath/$sourcesFolderPath" + packageName?.toSlashedFilePath().orEmpty()
        log("getter for getSrcDirectory(packageName: $packageName) | path: $srcDirectoryPath")
        return File(srcDirectoryPath)
    }

    override fun getTestDirectory(packageName: String?): File {
        val testDirectoryPath = "$moduleRootPath/$uiTestsFolderPath" + packageName?.toSlashedFilePath().orEmpty()
        log("getter for getTestDirectory(packageName: $packageName) | path: $testDirectoryPath")
        return File(testDirectoryPath)
    }

    override fun getUnitTestDirectory(packageName: String?): File {
        val unitTestDirectoryPath = "$moduleRootPath/$unitTestsFolderPath" + packageName?.toSlashedFilePath().orEmpty()
        log("getter for getUnitTestDirectory(packageName: $packageName) | path: $unitTestDirectoryPath")
        return File(unitTestDirectoryPath)
    }

    override val mlModelsDirectories: List<File>
        get() {
            log("getter for mlModelsDirectories | not used")
            return emptyList()
        }

    private fun log(message: String) {
        HHLogger.d("[$LOG_TAG]: $message")
    }
}
