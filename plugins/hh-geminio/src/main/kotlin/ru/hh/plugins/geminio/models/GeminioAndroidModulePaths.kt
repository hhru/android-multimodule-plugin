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
    private val moduleName: String
) : AndroidModulePaths {

    private companion object {
        const val LOG_TAG = "GeminioAndroidModulePaths"

        const val MANIFEST_FOLDER_PATH = "src/main"
        const val UI_TESTS_FOLDER_PATH = "src/androidTest"
        const val UNIT_TESTS_FOLDER_PATH = "src/test"
        const val RES_FOLDER_PATH = "src/main/res"
        const val AIDL_FOLDER_PATH = "src/main/aidl"
        const val SOURCES_FOLDER_PATH = "src/main/java"
    }

    private val moduleRootPath: String get() = "$basePath/$moduleName"

    override val manifestDirectory: File
        get() {
            val manifestDirectoryPath = "$moduleRootPath/$MANIFEST_FOLDER_PATH"

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
            val resDirectoryPath = "$moduleRootPath/$RES_FOLDER_PATH"

            log("getter for resDirectories | path: $resDirectoryPath")
            return listOf(File(resDirectoryPath))
        }

    override fun getAidlDirectory(packageName: String?): File {
        val aidlDirectoryPath = "$moduleRootPath/$AIDL_FOLDER_PATH" + packageName?.toSlashedFilePath().orEmpty()

        log("getter for getAidlDirectory(packageName: $packageName) | path: $aidlDirectoryPath")
        return File(aidlDirectoryPath)
    }

    override fun getSrcDirectory(packageName: String?): File {
        val srcDirectoryPath = "$moduleRootPath/$SOURCES_FOLDER_PATH" + packageName?.toSlashedFilePath().orEmpty()

        log("getter for getSrcDirectory(packageName: $packageName) | path: $srcDirectoryPath")
        return File(srcDirectoryPath)
    }

    override fun getTestDirectory(packageName: String?): File {
        val testDirectoryPath = "$moduleRootPath/$UI_TESTS_FOLDER_PATH" + packageName?.toSlashedFilePath().orEmpty()

        log("getter for getTestDirectory(packageName: $packageName) | path: $testDirectoryPath")
        return File(testDirectoryPath)
    }

    override fun getUnitTestDirectory(packageName: String?): File {
        val unitTestDirectoryPath =
            "$moduleRootPath/$UNIT_TESTS_FOLDER_PATH" + packageName?.toSlashedFilePath().orEmpty()

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
