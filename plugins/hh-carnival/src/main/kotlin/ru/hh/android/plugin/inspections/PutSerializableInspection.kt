package ru.hh.android.plugin.inspections

class PutSerializableInspection : AutoRegisterAndroidLintInspection(
    displayName = PutSerializableDetector.ISSUE_ID,
    issue = PutSerializableDetector.ISSUE
)
