# Geminio

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
- Now we configure plugin modules with [gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html);
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
