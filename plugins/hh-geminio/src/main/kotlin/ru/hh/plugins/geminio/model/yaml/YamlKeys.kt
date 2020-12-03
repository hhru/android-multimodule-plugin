package ru.hh.plugins.geminio.model.yaml


object YamlKeys {

    const val KEY_REQUIRED_PARAMS = "requiredParams"
    const val KEY_REQUIRED_PARAMS_NAME = "name"
    const val KEY_REQUIRED_PARAMS_DESCRIPTION = "description"

    const val KEY_OPTIONAL_PARAMS = "optionalParams"
    const val KEY_OPTIONAL_PARAMS_REVISION = "revision"
    const val KEY_OPTIONAL_PARAMS_CATEGORY = "category"
    const val KEY_OPTIONAL_PARAMS_FORM_FACTOR = "formFactor"
    const val KEY_OPTIONAL_PARAMS_CONSTRAINTS = "constraints"
    const val KEY_OPTIONAL_PARAMS_SCREENS = "screens"
    const val KEY_OPTIONAL_PARAMS_MIN_API = "minApi"
    const val KEY_OPTIONAL_PARAMS_MIN_BUILD_API = "minBuildApi"

    const val KEY_WIDGETS = "widgets"
    const val KEY_WIDGETS_STRING_PARAMETER = "stringParameter"
    const val KEY_WIDGETS_BOOLEAN_PARAMETER = "booleanParameter"

    const val KEY_PARAMETER_ID = "id"
    const val KEY_PARAMETER_NAME = "name"
    const val KEY_PARAMETER_HELP = "help"
    const val KEY_PARAMETER_CONSTRAINTS = "constraints"
    const val KEY_PARAMETER_DEFAULT = "default"
    const val KEY_PARAMETER_SUGGEST = "suggest"
    const val KEY_PARAMETER_VISIBILITY = "visibility"
    const val KEY_PARAMETER_AVAILABILITY = "availability"
    // const val KEY_WIDGETS_ENUM_PARAMETER = "enumParameter" // TODO: add support?

    const val KEY_RECIPE = "recipe"
    const val KEY_RECIPE_INSTANTIATE = "instantiate"
    const val KEY_RECIPE_OPEN = "open"
    const val KEY_RECIPE_INSTANTIATE_AND_OPEN = "instantiateAndOpen"
    const val KEY_RECIPE_PREDICATE = "predicate"
    const val KEY_RECIPE_ADD_DEPENDENCIES = "addDependencies"

    const val KEY_COMMAND_FROM = "from"
    const val KEY_COMMAND_TO = "to"
    const val KEY_COMMAND_FILE = "file"
    const val KEY_COMMAND_VALID_IF = "validIf"
    const val KEY_COMMAND_COMMANDS = "commands"

    const val KEY_COMMAND_TYPE_FOR_ALL = "typeForAll"
    const val KEY_COMMAND_DEPENDENCIES = "dependencies"
    const val KEY_DEPENDENCY_TYPE = "type"
    const val KEY_DEPENDENCY_PROJECT = "project"
    const val KEY_DEPENDENCY_LIBS_CONSTANT = "libsConstant"
    const val KEY_DEPENDENCY_NAME = "name"
    const val KEY_DEPENDENCY_VALUE = "value"

}