package ru.hh.android.plugin.config

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.model.jira.JiraSettings
import ru.hh.plugins.extensions.EMPTY
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@State(name = "ru.hh.android.plugins.antiroutine.JiraSettingsConfig")
class JiraSettingsConfig : PersistentStateComponent<Credentials> {

    companion object {
        private const val KEY_FILE_NAME = "jiraHostName.txt"

        fun getInstance(project: Project): JiraSettingsConfig = project.service()
    }

    private var credentials: Credentials? = null

    var key: String? = null
        get() {
            val keyFile = File(KEY_FILE_NAME)

            if (!keyFile.exists()) {
                keyFile.createNewFile()
            }

            val reader = FileReader(KEY_FILE_NAME)
            val hostname = reader.readText()
            return field ?: hostname
        }

    fun writeHostname(hostname: String?) {
        key = hostname
        val file = FileWriter(KEY_FILE_NAME)

        file.apply {
            flush()

            if (hostname != null) {
                write(hostname)
            }
            close()
        }
    }

    override fun getState(): Credentials? {
        if (credentials == null) {
            val credentialAttributes = createCredentialAttributes()
            credentials = PasswordSafe.instance.get(credentialAttributes)
        }
        return credentials
    }

    override fun loadState(state: Credentials) {
        credentials = state
        val credentialAttributes = createCredentialAttributes()
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    fun getJiraSettings(): JiraSettings {
        val configData = state

        return JiraSettings(
            hostName = key ?: String.EMPTY,
            username = configData?.userName ?: String.EMPTY,
            password = configData?.password ?: String.EMPTY
        )
    }

    private fun createCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes(generateServiceName(PluginConstants.SUBSYSTEM_NAME, key!!))
    }
}
