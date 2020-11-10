package ru.hh.android.plugins.garcon.extensions

import com.intellij.refactoring.MoveDestination


val MoveDestination.targetPackageName: String?
    get() {
        return targetPackage?.qualifiedName
    }