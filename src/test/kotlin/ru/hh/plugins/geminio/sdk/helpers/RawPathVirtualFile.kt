package ru.hh.plugins.geminio.sdk.helpers

import com.intellij.mock.MockVirtualFile

internal class RawPathVirtualFile(
    private val rawPath: String,
) : MockVirtualFile(true, rawPath) {

    override fun getPath(): String {
        return rawPath
    }
}
