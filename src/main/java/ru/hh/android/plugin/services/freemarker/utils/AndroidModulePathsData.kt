package ru.hh.android.plugin.services.freemarker.utils

import com.android.tools.idea.projectsystem.AndroidModulePaths
import java.io.File


class AndroidModulePathsData(
    moduleRootDirFile: File,
    modulePackageName: String
) : AndroidModulePaths {

    companion object {
        private const val SD_SRC = "src"
        private const val SD_FLAVOR = "main"
        private const val SD_SRC_JAVA = "java"
        private const val SD_SRC_KOTLIN = "kotlin"
        private const val SD_RES = "res"
        private const val SD_AIDL = "aidl"
        private const val SD_ANDROID_TEST = "androidTest"
        private const val SD_TEST = "test"
    }

    private val baseSrcDir = File(moduleRootDirFile, SD_SRC)
    private val baseFlavorDir = File(baseSrcDir, SD_FLAVOR)
    private val packagePath = modulePackageName.split(".").joinToString("/")

    override val manifestDirectory: File? = baseFlavorDir
    override val moduleRoot: File? = moduleRootDirFile
    override val resDirectories: List<File> = listOf(File(baseFlavorDir, SD_RES))

    override fun getAidlDirectory(packageName: String?): File? = File(baseFlavorDir, SD_AIDL)

    override fun getSrcDirectory(packageName: String?): File? {
        return File(baseFlavorDir, "$SD_SRC_JAVA/$packagePath").takeIf { it.exists() }
            ?: File(baseFlavorDir, "$SD_SRC_KOTLIN/$packagePath")
    }

    override fun getTestDirectory(packageName: String?): File? {
        return File(baseFlavorDir, "$SD_ANDROID_TEST/$SD_SRC_JAVA").takeIf { it.exists() }
            ?: File(baseFlavorDir, "$SD_ANDROID_TEST/$SD_SRC_KOTLIN")
    }

    override fun getUnitTestDirectory(packageName: String?): File? {
        return File(baseFlavorDir, "$SD_TEST/$SD_SRC_JAVA").takeIf { it.exists() }
            ?: File(baseFlavorDir, "$SD_TEST/$SD_SRC_KOTLIN")
    }

}