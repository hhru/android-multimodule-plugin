package ru.hh.android.plugin.inspections.hardcoded_dimen

import ru.hh.android.plugin.inspections.AutoRegisterAndroidLintInspection


class HardcodedDimensUsingInspection : AutoRegisterAndroidLintInspection(
    HardcodedDimensUsingDetector.ISSUE_ID,
    HardcodedDimensUsingDetector.ISSUE
)