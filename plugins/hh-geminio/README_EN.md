# Geminio

![Geminio](./img/Geminio.png)

Android Studio's plugin for generating code from FreeMarker templates

## Why?

Android Studio 4.1
has [disabled support for custom FreeMarker templates](https://issuetracker.google.com/issues/154531807). Previously,
you could create custom templates, put them into a specific folder, and then the Android Studio should use your
templates as 'Other'-templates.

Starting from Android Studio 4.1 you can add custom templates only from IDEA plugins. We at hh.ru are not satisfied with
this and we want to add and update templates independently from plugins.

That's what Geminio is for.

## MoC (Map of contents)

- [How the plugins works](./docs/en/HOW_IT_WORKS.md)
- [Plugin's configuration](./docs/en/PLUGIN_CONFIG.md)
- [Expressions](./docs/en/EXPRESSIONS.md)
- [`recipe.yaml` content](./docs/en/RECIPE_CONTENT.md)
- [Modules templates](./docs/en/MODULES_TEMPLATES.md)