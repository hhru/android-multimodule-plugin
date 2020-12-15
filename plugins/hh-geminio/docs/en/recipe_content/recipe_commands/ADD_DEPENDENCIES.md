#### `addDependencies` command

This command will add specified dependencies list into `build.gradle` file of current module.
Each dependency has the following format:

```yaml
- <configurationType>: <dependencyNotation>
```

Here:

- `<configurationType>` - dependency configuration type, could be one of the following values:
  * `compileOnly`
  * `api`
  * `implementation`
  * `testImplementation`
  * `androidTestImplementation`
  * `kapt`

- `<dependencyNotation>` - dependency declaration notation. Has three different formats:
  * Maven's artifact notation, e.g, `org.company:artifact:version`
  * Project dependency, e.g. `:shared-core-model`
  * Library constant, e.g. `Libs.jetpack.compose`

--- 

[Back to the commands list](/plugins/hh-geminio/docs/en/recipe_content/RECIPE.md)