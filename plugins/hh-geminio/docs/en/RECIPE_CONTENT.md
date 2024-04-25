## `recipe.yaml` content

The "recipe" is a set of instructions and prerequisites for executing your template.

*Attention!* Each recipe file must be named `recipe.yaml`.

Each `recipe.yaml` file should look something like that:

```yaml
requiredParams:
  name: HeadHunter BaseFragment
  description: Creates HeadHunter BaseFragment

# optional
optionalParams:
  revision: 1
  category: fragment
  formFactor: mobile
  constraints:
    - kotlin
  screens:
    - fragment_gallery
    - menu_entry
  minApi: 7
  minBuildApi: 8

# required only for modules templates
predefinedFeatures:
  - enableModuleCreationParams:
      defaultPackageNamePrefix: ru.hh.test
      # optional
      defaultSourceCodeFolderName: java
      # optional
      defaultSourceSetName: main

widgets:
  - stringParameter:
      id: className
      name: Fragment Name
      help: The name of the fragment class to create
      constraints:
        - class
        - nonempty
        - unique
      default: BlankFragment

  - stringParameter:
      id: fragmentName
      name: Fragment Layout Name
      help: The name of the layout to create
      constraints:
        - layout
        - nonempty
        - unique
      default: fragment_blank
      suggest: fragment_${className.classToResource()}

  - booleanParameter:
      id: includeFactory
      name: Include fragment factory method?
      help: Generate static fragment factory method for easy instantiation
      default: true

  - booleanParameter:
      id: includeModule
      name: Include Toothpick Module class?
      help: Generate fragment Toothpick Module for easy instantiation
      default: true

  - stringParameter:
      id: moduleName
      name: Fragment Toothpick Module
      help: The name of the Fragment Toothpick Module to create
      constraints:
        - class
        - nonempty
        - unique
      default: BlankModule
      visibility: ${includeModule}
      suggest: ${className.classToResource().underscoreToCamelCase()}Module

globals:
  - stringParameter:
      id: reducerClassName
      value: ${moduleName}Reducer

  - booleanParameter:
      id: anotherBooleanFlag
      value: true

recipe:
  - mkDirs:
      - ${srcOut}:
          - api
          - di:
              - modules
              - outer
          - domain:
              - interactors
              - repositories
          - ui
      - ${resOut}:
          - layout
          - drawables
          - values
  - instantiateAndOpen:
      from: root/src/app_package/BlankFragment.kt.ftl
      to: ${srcOut}/${className}.kt
  - instantiate:
      from: root/res/layout/fragment_blank.xml.ftl
      to: ${resOut}/layout/${fragmentName}.xml
  - instantiate:
      from: root/src/app_package/BlankViewModel.kt.ftl
      to: ${currentDirOut}/presentation/${className}ViewModel.kt
  - open:
      file: ${resOut}/layout/${fragmentName}.xml
  - predicate:
      validIf: ${includeModule}
      commands:
        - instantiate:
            from: root/src/app_package/BlankModule.kt.ftl
            to: ${srcOut}/di/${moduleName}.kt
        - open:
            file: ${srcOut}/di/${moduleName}.kt
      elseCommands:
        - instantiateAndOpen:
            from: root/src/app_package/AnotherModule.kt.ftl
            to: ${srcOut}/di/${moduleName}.kt
  - addDependencies:
      - implementation: Libs.jetpack.compose
      - kapt: Libs.di.toothpick
      - ksp: Libs.jetpack.room
      - compileOnly: com.github.stephanenicolas.toothpick:toothpick:3.1.0
      - testImplementation: :shared-core-test
      - androidTestImplementation: Libs.uiTests.kaspresso
  - addGradlePlugins:
      - kotlinx.serialization
      - kotlin-kapt
```

The recipe consists of 6 sections:

- `requiredParams` — required parameters for your template;
- `optionalParams` — optional parameters;
- `predefinedFeatures` - a set of predefined features in Geminio, which can add new widgets to the template, supplement
  the functionality.
- `widgets` — description of template parameters; only string and boolean parameters are available for use;
- `globals` - description of invisible parameters for templates, some "global" variables.
- `recipe` — a set of instructions that should be executed.

If you think that it looks like the old good FreeMarker's `template.xml` && `recipe.xml`,
but only combined into a single file, — you are absolutely right.

### Recipe's sections

- [requiredParams](./recipe_content/REQURED_PARAMS.md)
- [optionalParams](./recipe_content/OPTIONAL_PARAMS.md)
- [predefinedFeatures](./recipe_content/PREDEFINED_FEATURES.md)
- [widgets](./recipe_content/WIDGETS.md)
- [globals](./recipe_content/GLOBALS.md)
- [recipe](./recipe_content/RECIPE.md)

### Additional info

- [Expressions](./EXPRESSIONS.md)

---

[Return to MoC](../../README_EN.md)
