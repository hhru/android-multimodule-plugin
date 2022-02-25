package ru.hh.android.plugin.inspections.wrong_view_id

import ru.hh.android.plugin.inspections.AutoRegisterAndroidLintInspection

/**
 * Inspection which can highlight wrong View's identifiers for hh.ru code style.
 */
class WrongViewIdCodeStyleFormattingInspection : AutoRegisterAndroidLintInspection(
    WrongViewIdCodeStyleFormattingDetector.ISSUE_ID,
    WrongViewIdCodeStyleFormattingDetector.ISSUE
)
