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
