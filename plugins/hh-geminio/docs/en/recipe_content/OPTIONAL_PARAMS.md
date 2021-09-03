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

---

[Back to `recipe.yaml` content](/plugins/hh-geminio/docs/en/RECIPE_CONTENT.md)