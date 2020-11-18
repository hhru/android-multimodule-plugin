package ru.hh.android.plugin.model.enums


enum class PredefinedFeature(
        val uiText: String,
        val freeMarkerParamToken: String,
        val defaultValue: Boolean
) {

    ENABLE_MOXY(
            uiText = "Enable Moxy",
            freeMarkerParamToken = "enable_moxy",
            defaultValue = false
    ),
    ADD_UI_MODULES_DEPENDENCIES(
            uiText = "Add UI modules dependencies",
            freeMarkerParamToken = "need_add_ui_modules_dependencies",
            defaultValue = false
    ),
    NEED_CREATE_API_INTERFACE(
            uiText = "Need create API interface",
            freeMarkerParamToken = "need_create_api_interface",
            defaultValue = false
    ),
    NEED_CREATE_REPOSITORY_WITH_INTERACTOR(
            uiText = "Need create repository with interactor",
            freeMarkerParamToken = "need_create_repository_with_interactor",
            defaultValue = false
    ),
    NEED_CREATE_INTERFACE_FOR_REPOSITORY(
            uiText = "Need create interface for repository",
            freeMarkerParamToken = "need_create_interface_for_repository",
            defaultValue = false
    ),
    NEED_CREATE_PRESENTATION_LAYER(
            uiText = "Need create presentation layer",
            freeMarkerParamToken = "need_create_presentation_layer",
            defaultValue = false
    ),
    USE_TOOTHPICK_3_SUPPORT(
            uiText = "Use Toothpick 3.0 modules",
            freeMarkerParamToken = "use_toothpick_3_support",
            defaultValue = false
    )

}