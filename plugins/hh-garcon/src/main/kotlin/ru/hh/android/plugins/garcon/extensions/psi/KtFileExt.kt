package ru.hh.android.plugins.garcon.extensions.psi

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath
import ru.hh.android.plugins.garcon.extensions.logDebug


fun KtFile.addImportPackages(vararg packages: String) {
    val ktPsiFactory = KtPsiFactory(project)

    project.logDebug("Want to add the following packages: ${packages.joinToString(separator = "\n")}")

    importList?.let { importListElement ->
        val existingImports = importDirectives.mapNotNull { it.importedFqName?.asString() }.toMutableSet()
        project.logDebug("Existing imports: ${existingImports.joinToString(separator = "\n")}")

        packages.forEach { packageName ->
            if (existingImports.contains(packageName).not()) {
                existingImports += packageName
                importListElement.add(ktPsiFactory.createLineBreakElement())
                importListElement.add(ktPsiFactory.createImportDirective(ImportPath.fromString(packageName)))
            }
        }
    }
}