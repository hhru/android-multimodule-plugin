### `globals` section

It is often necessary to describe a number of "invisible" template parameters, 
which are usually not visible to the user, but are actively used in FTL templates. 
The `globals` section is for a quick description of such parameters.

The syntax is similar to `widgets`, but much shorter. 
Each element of the `globals` section will be transformed into an invisible widget.

Also, this section automatically adds a special checkbox `Show hidden globals values`, 
which will show all globals values at once.

```yaml
globals:
  - stringParameter:
      id: diModuleClassName
      value: ${__formattedModuleName}Module
      
  - booleanParameter:
      id: someFlag
      value: ${anotherFlag}
```

The section supports two types of widgets:

- `stringParameter` - text widget
- `booleanParameter` - boolean checkbox

Each widget has only two parameters:

- `id` - unique widget's identifier;
- `value` - [expression](/plugins/hh-geminio/docs/en/EXPRESSIONS.md) for values calculation.


---

[Back to `recipe.yaml` content](/plugins/hh-geminio/docs/en/RECIPE_CONTENT.md)