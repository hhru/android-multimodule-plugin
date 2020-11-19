rootProject.name = "hh-android-plugins"

// Core modules
include(":hh-plugins-core")

project(":hh-plugins-core").projectDir = File("$settingsDir/core/hh-plugins-core")


// Plugins
include(":hh-carnival")
include(":hh-garcon")
include(":hh-geminio")

project(":hh-carnival").projectDir = File("$settingsDir/plugins/hh-carnival")
project(":hh-garcon").projectDir = File("$settingsDir/plugins/hh-garcon")
project(":hh-geminio").projectDir = File("$settingsDir/plugins/hh-geminio")
