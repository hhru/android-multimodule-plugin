## How the plugin works

When you open a project in Android Studio, the plugin will scan the specified
folders in search of «recipes» for preparing your templates or modules templates.
For each template, a separate Action (menu item) is created.

This Action will be added:

- into the `Generate` menu (Cmd + N — inside a code editor);
- into a separate group inside the `New` menu (Cmd + N — in the Project View).

After choosing a template from the menu, the plugin parses the «recipe.yaml» associated with the Action and executes it.

For module templates and regular templates, the appearing content of the wizard will be different,
but the templates themselves support all listed features.

---

[Return to MoC](../../README_EN.md)