package ru.hh.android.plugin.extensions

import com.intellij.psi.PsiDirectory
import com.intellij.util.IncorrectOperationException
import ru.hh.android.plugin.actions.modules.copy_module.exceptions.CopyModuleActionException
import ru.hh.android.plugin.utils.PluginBundle.message
import ru.hh.android.plugin.utils.logDebug


fun PsiDirectory.canCreateSubdirectory(name: String): Boolean {
    return try {
        this.checkCreateSubdirectory(name)
        true
    } catch (ex: IncorrectOperationException) {
        false
    }
}

fun PsiDirectory.findSubdirectoryByPackageName(moduleName: String, packageName: String): PsiDirectory {
    val directoriesNames = packageName.split(Char.DOT)
    var result: PsiDirectory = this
    for (item in directoriesNames) {
        result = result.findSubdirectory(item)
            ?: throw CopyModuleActionException(message("geminio.errors.copy_module.cant_find_package_directory.0.1", moduleName, packageName))
    }
    return result
}

fun PsiDirectory.createSubdirectoriesForPackageName(packageName: String): PsiDirectory {
    val directoriesNames = packageName.split(Char.DOT)
    var result = this
    for (item in directoriesNames) {
        result = result.createSubdirectory(item)
    }
    return result
}

fun PsiDirectory.copyInto(another: PsiDirectory, textTransformation: (String) -> String = { it }) {
    project.logDebug("Start copying ${this.name} package...")
    files.forEach { psiFile ->
        project.logDebug("\tCopy ${psiFile.name} file...")
        another.add(psiFile.copyFile(textTransformation))
    }
    subdirectories.forEach { directory ->
        project.logDebug("\tFind subdirectory ${directory.name}...")
        val newDirectory = another.createSubdirectory(directory.name)
        directory.copyInto(newDirectory, textTransformation)
    }
    project.logDebug("End copying ${this.name} package")
}