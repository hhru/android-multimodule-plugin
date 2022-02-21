### `predefinedFeatures` sections

The section is needed to enable some built-in features inside Geminio.
wThe section is needed to enable some built-in features inside Geminio.

Available list values:

- `enableModuleCreationParams` - adds two text fields for new module's name and its main package name. 
  After adding this feature, in [expressions](/plugins/hh-geminio/docs/en/EXPRESSIONS.md) of 
  [`widgets`](/plugins/hh-geminio/docs/en/recipe_content/WIDGETS.md) and [`globals`](/plugins/hh-geminio/docs/en/recipe_content/GLOBALS.md),
  sections and also in FTL templates you can use the following parameters:
    * `__moduleName` - module name
    * `__packageName` - the packageName of the module that is added to` AndroidManifest.xml`
    * `__formattedModuleName` is a formatted module name that can be used as a prefix for classes inside a new module.

Also you have ability to change base package name for new creating module with special property:

```yaml
predefinedFeatures:
  enableModuleCreationParams:
    defaultPackageNamePrefix: ru.hh.test
```

Here, `ru.hh.test` - new value of your custom package name.

---

[Back to `recipe.yaml` content](/plugins/hh-geminio/docs/en/RECIPE_CONTENT.md)