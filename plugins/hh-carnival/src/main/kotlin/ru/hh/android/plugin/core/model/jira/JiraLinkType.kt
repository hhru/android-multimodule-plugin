package ru.hh.android.plugin.core.model.jira

/**
 * Available link types in JIRA
 *
 * (from GET /rest/api/2/issueLinkType)
 */
enum class JiraLinkType(val remoteName: String) {

    /**
     * inward: git based by
     * outward: git based on
     */
    BASED("Based"),

    /**
     * inward: is cancelled by
     * outward: cancels
     */
    CANCELLATION("Cancellation"),

    /**
     * inward: is cloned by
     * outward: clones
     */
    CLONERS("Cloners"),

    /**
     * inward: defect
     * outward: defect
     */
    DEFECT("Defect"),

    /**
     * inward: blocked by
     * outward: blocks
     */
    DEPENDENCE("Dependence"),

    /**
     * inward: is duplicated by
     * outward: duplicates
     */
    DUPLICATE("Duplicate"),

    /**
     * inward: is FF-depended by
     * outward: FF-depends on
     */
    FINISH_TO_FINISH_DEPENDENCY("Finish-to-Finish Dependency"),

    /**
     * inward: is FS-depended by
     * outward: FS-depends on
     */
    FINISH_TO_START_DEPENDENCY("Finish-to-Start Dependency"),

    /**
     * inward: is fixed by
     * outward: is a fix for
     */
    FIX("Fix"),

    /**
     * inward: consists in
     * outward: includes
     */
    INCLUSION("Inclusion"),

    /**
     * inward: is related by
     * outward: relates
     */
    Relation("Relation"),

    /**
     * inward: is SF-depended by
     * outward: SF-depends on
     */
    START_TO_FINISH_DEPENDENCY("Start-to-Finish Dependency"),

    /**
     * inward: is SS-depended by
     * outward: SS-depends on
     */
    START_TO_START_DEPENDENCY("Start-to-Start Dependency"),

    /**
     * inward: разделить от
     * outward: разделить на
     */
    PROBLEM_DIVIDED("Проблема, разделенная")
}
