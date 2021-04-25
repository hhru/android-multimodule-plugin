rootProject.name = "hh-android-plugins"

// region Shared modules

// region Shared core modules
include(":shared:core:utils")
include(":shared:core:freemarker")
include(":shared:core:ui")
include(":shared:core:code-modification")
// endregion

// region Shared features
include(":shared:feature:geminio-sdk")
// endregion

// endregion

// Plugins
include(":plugins:hh-carnival")
include(":plugins:hh-garcon")
include(":plugins:hh-geminio")