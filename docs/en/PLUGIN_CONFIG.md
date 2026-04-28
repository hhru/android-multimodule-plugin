## Plugin's configuration

Inside your project, create a `geminio_config.yaml` file with the following content:

```yaml
templatesRootDirPath: /android-style-guide/geminio/templates
modulesTemplatesRootDirPath: /android-style-guide/geminio/modules_templates

groupsNames:
  forNewGroup: HH Templates
  forNewModulesGroup: HH Modules
```

- `templatesRootDirPath` — relative path from your project's root folder to the templates folder that the Geminio plugin should read.

- `modulesTemplatesRootDirPath` - relative path from your project's root folder to the modules templates folder.

- `groupNames` — names of groups to which actions will be added.
    * `forNewGroup` —- name of the separate group that will appear in the `New` menu (Cmd+N in the Project View)
    * `forNewModulesGroup` -- name of the separate group with modules templates in the `New` menu (Cmd+N in the Project View)

After creating this file, open the `Preferences -> Appearance & Behavior -> Geminio plugin`  settings page. Choose the path to your config file, click the `Apply` button – you should see that empty text fields on this page will be filled.

Then reload the project, and your templates should be parsed and converted into Actions. 
When adding a new template folder (simple template or module template), 
you will need to reload the project for Android Studio could parse the new template.

---

[Return to MoC](../../README_EN.md)