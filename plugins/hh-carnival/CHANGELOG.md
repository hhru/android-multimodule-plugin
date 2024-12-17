# Carnival

## [1.10.0]

Support for Android Studio Ladybug | 2024.2.1 Patch 2. Older versions are not supported.

## [1.9.0]

### Added

- Support for Android Studio Koala | 2024.1.1

### Changed

- New versions of Carnival could be installed **only since Android Studio Koala**.
  Previous versions support **was dropped**.
- Changed Carnival setup dialog UI internal implementation (thanks, @illarionov !) --
  from https://github.com/hhru/android-multimodule-plugin/pull/89 .

### Fixed

- Fixes several IDE-warnings about project level services.
- Fixes several IDE-warnings about AnAction (thanks, @illarionov !) --
  from https://github.com/hhru/android-multimodule-plugin/pull/95 .

## [1.8.0]

### Added

- Support for Android Studio Jellyfish | 2023.3.1

## [1.7.0]

### Added

- Support for Android Studio Iguana | 2023.2.1

## [1.6.0]

### Added

- Support for Android Studio Hedgehog | 2023.1.1

## [1.5.0]

### Added

- Support for Android Studio Giraffe | 2022.3.1

## [1.4.0]

### Added

- Support for Android Studio Flamingo | 2022.2.1

## [1.3.0]

### Added

- Support for Android Studio Electric Eel | 2022.1.1

## [1.2.0]

### Added

- Support compilation for Android Studio Dolphin | 2021.3.1

## [1.1.7]

### Added

- Support for Android Studio Chipmunk | 2021.2.1

## [1.1.6]

### Added

- Support for Android Studio Bumblebee | 2021.1.1

## [1.1.5]

### Added

- Support for Android Studio Arctic Fox | 2020.3.1

## [1.1.4]

### Added

- [Gradle Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) integration.
- Support for Android Studio 4.2

### Changed

- Now we configure plugin modules
  with [gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html);
- Upgrade Gradle Wrapper to `7.0`;

## [1.1.3]

### Fixed

- Fix bug with duplicated service.

## [1.1.2]

### Added

- Generate ViewModel for BaseFragment. All code was hardcoded inside plugin.

## [1.1.0]

### Changed

- Remove action for generating new module - this functionality will be available through Geminio
  plugin.

## [1.0.0]

### Added

- Initial project release;
- Action for generating new feature module;
- Small actions for quick code generation: EMPTY object generator, @SerializedName annotations
  generator
- Action for creating new JIRA issue for develop branch merge;
- Lint inspections for resources;
- Annotator for deprecated XML-resources.
