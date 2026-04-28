## Modules templates

Templates for modules are slightly different from regular templates, and are designed for
creation of new modules with a specific folder structure, dependencies, content.
When creating a module, Geminio executes the commands specified in the recipe, and also
adds a description of the created module to the `settings.gradle` file,
and adds a dependency on the created module to the selected application modules.

### Differences from regular templates

- Module templates are available not only inside android modules, but also outside any modules, inside any folder.

- In addition to building a wizard page for filling in parameters from a recipe,
  the module template adds two more pages to the wizard: a page for selecting dependencies
  on existing modules, and a page for selecting application modules to which
  the created module will need to be connected.

- Additional parameters are available in FTL-templates of new modules:
    * `__applicationsModules` - a list of names of application-modules to which
      the created module will need to be connected. To disable this step set `enableChooseModulesStep` to `false`
  (default value is `true`)

---

[Return to MoC](../../README_EN.md)