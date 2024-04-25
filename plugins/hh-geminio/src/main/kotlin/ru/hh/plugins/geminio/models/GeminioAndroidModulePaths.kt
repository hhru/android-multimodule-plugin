package ru.hh.plugins.geminio.models

import com.android.tools.idea.projectsystem.AndroidModulePaths
import ru.hh.plugins.extensions.toSlashedFilePath
import ru.hh.plugins.logger.HHLogger
import java.io.File

/**
 * Implementation of [com.android.tools.idea.projectsystem.AndroidModulePaths] interface
 * for correct propagation of different folders paths.
 */
class GeminioAndroidModulePaths(
    private val basePath: String,
    private val moduleName: String,
    sourceSet: String,
    sourceCodeFolderName: String,
) : AndroidModulePaths {

    private companion object {
        const val LOG_TAG = "GeminioAndroidModulePaths"
    }

    private val manifestFolderPath = "src/$sourceSet"
    private val uiTestsFolderPath = "src/androidTest"
    private val unitTestsFolderPath = "src/test"
    private val resFolderPath = "src/$sourceSet/res"
    private val aidlFolderPath = "src/$sourceSet/aidl"
    private val sourcesFolderPath = "src/$sourceSet/$sourceCodeFolderName"
    private val moduleRootPath: String get() = "$basePath/$moduleName"

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
        val unitTestDirectoryPath =
            "$moduleRootPath/$unitTestsFolderPath" + packageName?.toSlashedFilePath().orEmpty()

        log("getter for getUnitTestDirectory(packageName: $packageName) | path: $unitTestDirectoryPath")
        return File(unitTestDirectoryPath)
    }

    // Don't really know what it is.
    override val mlModelsDirectories: List<File>
        get() {
            log("getter for mlModelsDirectories | I don't know WTF")
            return emptyList()
        }

    private fun log(message: String) {
        HHLogger.d("[$LOG_TAG]: $message")
    }
}
