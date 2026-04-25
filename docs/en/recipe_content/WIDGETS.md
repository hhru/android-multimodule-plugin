### `widgets` section

The `widget` section is a user's parameter list for your template. For now, we support only string and boolean
parameters. Each parameter supports [expression](../EXPRESSIONS.md) evaluation.

#### `stringParameter`

`stringParameter` — description of the string parameter for your template (for example, class name, layout name, etc.).
Will be converted to a text input field on the UI.

The required values inside this block are:

- `id` — parameter identifier. This parameter can be used in [expression](../EXPRESSIONS.md) and in FreeMarker templates
  when generating code;

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

  `nonempty` checks that the value is not blank. `class`, `activity`, `package`, `app_package`,
  `source_set_folder`, `layout`, `drawable`, `navigation`, `values`, `string`, `uri_authority`, and
  `kotlin_function` validate the value format. `module` stays compatible with Android Studio wizard behavior:
  the constraint itself does not reject value syntax, including `:`.

  `unique` and `exists` are applied together with typed constraints when the UI can map the value to the current
  template's file context:
    * `module` checks the Gradle path of an already connected project module. The path is derived from the module
      content root relative to the project root: `applicant/feature/auth` becomes `applicant:feature:auth`, and the
      `/src/<sourceSet>` suffix is stripped without depending on specific source set names;
    * `package` / `app_package` checks the package path in the new module source root when it is known;
    * `layout`, `drawable`, `navigation`, `values` checks the resource in the corresponding `res` directory;
    * `string` checks a string resource in `res/values`.

- `default` — the default value of the parameter;
- `suggest` — a string [expression](../EXPRESSIONS.md) to automatically change the field, which may depend on other
  fields;
- `visibility` — boolean [expression](../EXPRESSIONS.md) to show / hide the field depending on the condition;
- `availability` — boolean [expression](../EXPRESSIONS.md) to switch the field to enabled / disabled state depending on
  the condition.

#### `booleanParameter`

`booleanParameter` — description of the boolean parameter for your template (for example, whether you need to generate
a module, some additional method, and so on). Will be converted to a checkbox on the UI.

Has exactly the same parameters as `stringParameter`, but `booleanParameter` has no `suggest`.

---

[Back to `recipe.yaml` content](../RECIPE_CONTENT.md)
