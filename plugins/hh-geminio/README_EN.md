# Geminio 

![Geminio](img/Geminio.png)

Android Studio's plugin for generating code from FreeMarker templates

## Why?

Android Studio 4.1 has [disabled support for custom FreeMarker templates](https://issuetracker.google.com/issues/154531807). Previously, you could create custom templates, put them into a specific folder, and then the Android Studio should use your templates as 'Other'-templates.

Starting from Android Studio 4.1 you can add custom templates only from IDEA plugins. We at hh.ru are not satisfied with this and we want to add and update templates independently from plugins.

That's what Geminio is for.

## How the plugin works

When you open a project in Android Studio, the plugin will scan the specified folder in search of «recipes» for preparing your templates. For each template, a separate Action (menu item) is created. This Action will be added:

into the Generate menu (Cmd + N — inside a code editor);
into a separate group inside the New menu (Cmd + N — in the Project View).

After choosing a template from the menu, the plugin parses the «recipe.yaml» associated with the Action and executes it.

## Plugin config

Inside your project, create a `geminio_config.yaml` file with the following content:

```yaml
templatesRootDirPath: /android-style-guide/geminio/templates
groupsNames:
  forNewGroup: HH Template
```

- `templatesRootDirPath` — relative path from your project root folder to the templates folder that the Geminio plugin should read.

- `groupNames` — names of groups to which actions will be added.
  * `forNewGroup` —- name of the separate group that will appear in the New menu (Cmd+N in the Project View)

After creating this file, open the `Preferences -> Appearance & Behavior -> Geminio plugin`  settings page. Choose the path to your config file, click the `Apply` button – you should see that empty text fields on this page will be filled.

Then reload the project, and your templates should be parsed and converted into Actions. When adding a new template folder, you will need to reload the project for Android Studio could parse the new template.

## recipe.yaml

The "recipe" is a set of instructions and prerequisites for executing your template.

*Attention!* Each recipe file must be named `recipe.yaml`.

Each `recipe.yaml` file should look something like that:

```yaml
requiredParams:
  name: HeadHunter BaseFragment
  description: Creates HeadHunter BaseFragment

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
      suggest: ${className.classToResource().underlinesToCamelCase()}Module

recipe:
  - instantiateAndOpen:
      from: root/src/app_package/BlankFragment.kt.ftl
      to: ${srcOut}/${className}.kt
  - instantiate:
      from: root/res/layout/fragment_blank.xml.ftl
      to: ${resOut}/layout/${fragmentName}.xml
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
  - addDependencies:
      typeForAll: implementation
      dependencies:
      - mavenArtifact: org.company:artifact:2.0.1
      - project: shared-core-model
      - libsConstant: Libs.jetpack.compose
      - mavenArtifact:
          type: androidTestImplementation
          name: org.company:artifact:1.1
      - project:
          type: api
          name: shared-rx-core
      - libsConstant:
          type: testImpementation
          value: Libs.tests.mockito
```

The recipe consists of 4 sections:

- `requiredParams` — required parameters for your template;
- `optionalParams` — optional parameters;
- `widgets` — description of template parameters; only string and boolean parameters are available for use;
- `recipe` — a set of instructions that should be executed.

If you think that it looks like the old good FreeMarker's `template.xml` && `recipe.xml`, 
but only combined into a single file, — you are absolutely right.

### `requiredParams` section

There are only two required parameters for your templates:

- `name` — name of the template (it must be unique among your templates and templates \
that already exist in Android Studio);
- `description` — description of the template, its functions.

The recipe will not start execution without these parameters.

### `optionalParams` section

These parameters are required for the Template Api engine, but in the context of the plugin, 
they are not needed yet. Perhaps we will add the ability to automatically add templates through existing extension points, but for now, these parameters **do not affect anything** and you can skip them.

- `revision` — you need this value to override existing templates. Assume you want to override 
the template for creating a new empty fragment in Android Studio. 
If your revision value is higher than the standard, and the name of the template matches, 
then Android Studio should take your template.

- `category` — the category of templates, where the template should appear after adding through extension point. Available values:
    * `activity`;
    * `fragment`;
    * `application`;
    * `folder`;
    * `ui_component`;
    * `automotive`;
    * `xml`;
    * `wear`;
    * `aidl`;
    * `widget`;
    * `google`;
    * `compose`;
    * `other`.

- `formFactor` — determines which form-factor the template belongs to. 
This affects which projects your template can be displayed in. Available values:
  * `mobile`;
  * `wear`;
  * `tv`;
  * `automotive`;
  * `things`;
  * `generic`.

- `constraints` — constraints on the project in which your template can be run. 
For example, your template should only work in the project that uses Kotlin. 
Available values for the list:
    
   * `androidx`;
   * `kotlin`.

- `screens` — list of gallery wizards inside Android Studio where your template should appear. Available values for the list:

  * `new_project`;
  * `new_module`;
  * `menu_entry`;
  * `activity_gallery`;
  * `fragment_gallery`.

- `minApi` — the `minSdkVersion` value of the project required for your template.
- `minBuildApi` - the `compileSdkVersion` value of the project required for your template.

### `widgets` section

The `widget` section is a user's parameter list for your template. 
For now, we support only string and boolean parameters. Each parameter supports `expression` evaluation.

#### Expressions

Some settings of template parameters can be represented by «expressions» — these are lines of a specific 
format that can be evaluated for the templates to work.

Expressions could evaluate values of two types: strings and boolean.

Examples of expressions for the `widgets` section:

```yaml
suggest: fragment_${className.classToResource().underlinesToCamelCase()}
visibility: ${includeModule}
availability: true
```

The text inside `${}` is considered the «dynamic» part of the expression, which needs to be 
calculated depending on the content. Text outside the curly braces is the fixed part. 
Inside `${}` you can use only those parameters that have already been declared ABOVE in the `widgets` section text.

Additional extension functions can be used for text parameters:

- `activityToLayout`;
- `fragmentToLayout`;
- `classToResource`;
- `camelCaseToUnderlines`;
- `layoutToActivity`;
- `layoutToFragment`;
- `underlinesToCamelCase`.

There are special values for boolean expressions - `true` / `false` 
+ you can use only boolean-parameters inside ${} for them

Examples of expressions for the recipe section:

```
to: ${resOut}/layout/${fragmentName}.xml
file: ${srcOut}/di/${moduleName}.kt
```

In the `recipe` section, we add two additional parameters for string values:

- `resOut` — path to the res folder inside the module where the template is running;
- `srcOut` — path to the src / main / <source-set> / <current-dir> folder in the module where the template is running.

#### `stringParameter`

`stringParameter` — description of the string parameter for your template (for example, class name, layout name, etc.). 
Will be converted to a text input field on the UI.

The required values inside this block are:

- `id` — parameter identifier. This parameter can be used in `expressions` and 
in FreeMarker templates when generating code;

- `name` — the value will be indicated next to the text field when rendering the template.

Optional parameters:

- `help` — help for the text field;
- `constraints` — constraints for text field validation. Available values:

  * `unique`;
  * `exists`;
  * `nonempty`;
  * `activity`;
  * `class`;
  * `package`;
  * `app_package`;
  * `module`;
  * `layout`;
  * `drawable`;
  * `navigation`;
  * `values`;
  * `source_set_folder`;
  * `string`;
  * `uri_authority`;
  * `kotlin_function`.

- `default` — the default value of the parameter;
- `suggest` — a string expression to automatically change the field, which may depend on other fields;
- `visibility` — boolean expression to show / hide the field depending on the condition;
- `availability` — boolean expression to switch the field to enabled / disabled state depending on the condition.

#### `booleanParameter`

`booleanParameter` — description of the boolean parameter for your template (for example, whether you need to generate 
a module, some additional method, and so on). 
Will be converted to a checkbox on the UI.

Has exactly the same parameters as `stringParameter`, but `booleanParameter` has no `suggest`.

### `recipe` section

This section represents a list of commands that must be executed by the `Action` after the user enter 
all necessary parameters in your template.

#### `instantiate` command

Options:

- `from` — string expression, usually a relative path from the root of the recipe folder to the desired ftl-template;
- `to` — string expression, path to the target file with the code.

This command takes a FreeMarker ftl-file, the path to which is calculated from the `from` expression, 
passes parameters into it, generates the code and puts the result into the file, 
the path of which is specified in the `to` expression.

#### `open` command

Options:

- `file` — string expression, the path to the target file to open.

The command opens the specified file in the code editor

#### `instantiateAndOpen` command

Options:

- `from` — string expression, usually a relative path from the root of the recipe folder to the desired ftl-template;
- `to` — string expression, path to the target file with the code.

The command combines `instantiate` and `open` commands.

#### `predicate` command

Options:

- `validIf` — boolean expression for calculating the predicate for the specified commands;
- `commands` — a set of commands to be executed if `validIf` returns true.

The command calculates the expression that you specified in `validIf`. If the result is `true`, then the set of 
commands that you specify in the `commands` list will be executed. 
This list supports all of the above commands.


#### `addDependencies` command

This command will add specified dependencies into current module's build.gradle file.

Options:

- `typeForAll` -- connection type for every dependency in the list, could be one of these values:
  * `compileOnly`
  * `api`
  * `implementation`
  * `testImplementation`
  * `androidTestImplementation`
  * `kapt`

- `dependencies` - dependencies list to add into build.gradle

There are few types of dependencies:

- `mavenArtifact` -- maven artifact dependency, e.g. `"org.company:artifact:1.0"`
- `project` -- project dependency, e.g. `project(":shared-core-model")`
- `libsConstant` -- library dependency, e.g `"org.company:artifact:1.0"` or `Libs.jetpack.compose`

Two dependency declarations types allowed:

- The first one, when you don't need to override common connection type: 

```yaml
- mavenArtifact: org.company:artifact:1.1
- project: shared-core-model
- libsConstant: org.company:artifact:1.1
```

- The second one, when you need to override connection type:

```yaml
- mavenArtifact:
    notation: org.company:artifact:1.1
    type: testImplementation
- project: 
    name: shared-core-model
    type: implementation
- libsConstant: 
    value: Libs.jetpack.compose
    type: api
```
