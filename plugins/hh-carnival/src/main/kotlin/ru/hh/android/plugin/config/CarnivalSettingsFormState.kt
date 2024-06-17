package ru.hh.android.plugin.config

import com.intellij.util.text.CharArrayCharSequence
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.android.plugin.core.model.jira.JiraSettings

internal class CarnivalSettingsFormState(
    var pluginFolderDirPath: String = "",
    var enableDebugMode: Boolean = false,
    var jiraHostname: String = "",
    var jiraUsername: String = "",
    var jiraPassword: ClearableCharArray,
    var jiraDevelopmentTeam: JiraDevelopmentTeam = JiraDevelopmentTeam.MOBILE_CORE
) {

    constructor(
        pluginConfig: CarnivalPluginConfig,
        jiraSettings: JiraSettings
    ) : this(
        pluginFolderDirPath = pluginConfig.pluginFolderDirPath,
        enableDebugMode = pluginConfig.isDebugEnabled,
        jiraHostname = jiraSettings.hostName,
        jiraUsername = jiraSettings.username,
        jiraPassword = ClearableCharArray(jiraSettings.password),
        jiraDevelopmentTeam = pluginConfig.jiraDevelopmentTeam
    )

    class ClearableCharArray(vararg chars: Char) : CharArrayCharSequence(*chars) {

        constructor(chars: CharSequence) : this(
            *CharArray(chars.length, chars::get)
        )

        fun clear() {
            myChars.fill('\u0000', myStart, myEnd)
        }

        fun charsNewCopy(): CharArray = myChars.copyOf()

    }
}
