package ru.hh.android.plugin.extensions.psi.kotlin

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath


fun KtFile.addImportPackages(vararg packages: String) {
    val ktPsiElementFactory = KtPsiFactory(project)

    val existingImports = importDirectives.mapTo(mutableSetOf()) { it.importPath?.fqName?.asString().orEmpty() }
    importList?.let { importListPsiElement ->
        packages.forEach { packageName ->
            if (existingImports.contains(packageName).not()) {
                importListPsiElement.add(ktPsiElementFactory.getBreakLineElement())
                importListPsiElement.add(ktPsiElementFactory.createImportDirective(ImportPath.fromString(packageName)))
            }
        }
    }
}