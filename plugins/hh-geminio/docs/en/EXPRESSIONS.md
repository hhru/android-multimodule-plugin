## Expressions

Some settings of template parameters can be represented by «expressions» — these are lines of a specific
format that can be evaluated for the templates to work.

Expressions could evaluate values of two types: strings and boolean.

Examples of expressions for the `widgets` section:

```yaml
suggest: fragment_${className.classToResource().underscoreToCamelCase()}
visibility: ${includeModule}
availability: true
```

The text inside `${}` is considered the «dynamic» part of the expression, which needs to be
calculated depending on the content. Text outside the curly braces is the fixed part.
Inside `${}` you can use only those parameters that have already been declared ABOVE in the `widgets` (or `globals`) section text.

Additional extension functions can be used for text parameters:

- `activityToLayout`;
- `fragmentToLayout`;
- `classToResource`;
- `camelCaseToUnderlines`;
- `layoutToActivity`;
- `layoutToFragment`;
- `underscoreToCamelCase`.

There are special values for boolean expressions - `true` / `false`
+ you can use only boolean-parameters inside ${} for them

Examples of expressions for the recipe section:

```yaml
- open:
    file: ${srcOut}/di/${moduleName}.kt
    
- instantiate:
    from: root/build.gradle.ftl
    to: ${rootOut}/build.gradle

- instantiateAndOpen:
    from: root/res/layout/fragment_container.xml.ftl
    to: ${resOut}/layout/${fragmentName}.xml
    
- instantiateAndOpen:
    from: root/main/AndroidManifest.xml
    to: ${manifestOut}/AndroidManifest.xml
```

In the `recipe` section, we add several additional parameters for string values:

- `rootOut` - root folder of the module
- `manifestOut` - path to the `src/main` folder inside module
- `resOut` — path to the res folder inside the module where the template is running;
- `srcOut` — path to the src / main / <source-set> / <current-dir> folder in the module where the template is running.

---

[Return to MoC](/plugins/hh-geminio/README_EN.md)