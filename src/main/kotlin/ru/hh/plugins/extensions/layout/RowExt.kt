package ru.hh.plugins.extensions.layout

import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.COLUMNS_SHORT
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.columns

/**
 * Backported version of [Row.passwordField] available in IntelliJ Platform since 2023.1
 */
fun Row.passwordFieldCompat(): Cell<JBPasswordField> = cell(JBPasswordField())
    .columns(COLUMNS_SHORT)
