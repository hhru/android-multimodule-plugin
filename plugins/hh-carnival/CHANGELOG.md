# Carnival

## [Unreleased]
### Added
- [Gradle Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) integration.

### Changed
- Now we configure plugin modules with [gradle convention plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html);
- Upgrade Gradle Wrapper to `7.0`;

## [1.1.3]
### Fixed
- Fix bug with duplicated service.

## [1.1.2]
### Added
- Generate ViewModel for BaseFragment. All code was hardcoded inside plugin.

## [1.1.0]
### Changed
- Remove action for generating new module - this functionality will be available through Geminio plugin.

## [1.0.0]
### Added
- Initial project release;
- Action for generating new feature module;
- Small actions for quick code generation: EMPTY object generator, @SerializedName annotations generator
- Action for creating new JIRA issue for develop branch merge;
- Lint inspections for resources;
- Annotator for deprecated XML-resources.