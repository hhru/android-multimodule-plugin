# Geminio

## [1.7.0]
### Added
- Support for Android Studio Hedgehog | 2023.1.1

## [1.6.1]

### Fixed

- Fixed files creation in `androidTest` directory.

## [1.6.0]

### Added

- Support for Android Studio Giraffe | 2022.3.1

## [1.5.0]

### Added

- Support for Android Studio Flamingo | 2022.2.1

## [1.4.2]

### Added

- New `currentDirPackageName` parameter in FTL - add package name from selected folder in specified module.
- New `currentDirOut` parameter in recipes - path to selected directory that launched Geminio's action.
- Improvements for docs.

## [1.4.1]

### Added

- Support `ksp` dependency configuration in Geminio's recipes.

## [1.4.0]

### Added

- Support for Android Studio Electric Eel | 2022.1.1

## [1.3.0]

### Added

- Support for Android Studio Dolphin | 2021.3.1 (thanks to @IlyaGulya)
- Added "Enable debug mode" checkbox in settings
- Optional Gradle sync after creating files from templates

### Fixed

- Fixed some problems on Android Studio Chipmunk Patch 2 | 2021.2.1
- Fixed too many templates rescan

## [1.2.0]

### Added

- Sync dialog after creating templates
- Display templates folders with recipes only
- Ability to rescan templates folders without restarting
- `help` filed for all widgets not required
- Multi-window support with different projects

### Fixed

- Fixed duplication templates items after reopen project
- Fixed 'addGradlePlugin' and other modification commands in files

## [1.1.11]

### Added

- Support for Android Studio Chipmunk | 2021.2.1

## [1.1.10]

### Added

- Ability to change predefined package name for modules templates through
  `enableModuleCreationParams.defaultPackageNamePrefix` property in `recipe.xml`.

## [1.1.9]

### Added

- Support for Android Studio Bumblebee | 2021.1.1

## [1.1.8]

### Fixed

- Fixed duplication of projects in dependencies after recipe execution

## [1.1.7]

### Added

- Support for Android Studio Arctic Fox | 2020.3.1

## [1.1.6]

### Added

- Support for Android Studio 4.2

### Changed

- **Breaking change**!!! In Android Studio 4.2 `underlinesToCamelCase` function was removed,
  so we renamed `underlinesToCamelCase` modifier in templates into `underscoreToCamelCase`.

### Fixed

- Don't generate actions if there is no Geminio config

## [1.1.5]

### Fixed

- Fixed adding dependencies into build.gradle / build.gradle.kts files

## [1.1.4]

### Added

- [Gradle Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) integration.

### Changed

- Now we configure plugin modules
  with [gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html);
- Upgrade Gradle Wrapper to `7.0`;
  BuildGradleModificationService.kt

## [1.1.3]

### Changed

- Remove `'Choose module'` step from modules actions.

## [1.1.2]

### Changed

- Moved templates actions to the top of `'New'` action group.

### Fixed

- Made `'help'` parameter optional in widgets section.

## [1.1.1]

### Fixed

- Fix bug with duplicated project service.

## [1.1.0]

### Added

- Support for modules creation;
- A lot of validation messages for recipes to help you properly create new recipes;
- New command for recipe: `mkDirs` for creating directories structure;
- Predefined variables in `recipe` section: `{manifestOut}` and `{rootOut}`.

## [1.0.1]

### Added

- Add new command for recipe: `addDependencies` for adding dependencies into build.gradle file;
- New hardcoded parameter in FTL-files: `applicationPackage` - it is package name from AndroidManifest.xml file.

## [1.0.0]

### Added

- Initial project release.
