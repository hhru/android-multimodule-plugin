#### `mkDirs` command

This command needs for creating folders structure inside your module.

Each list item of this command is the hierarchical structure of the names of the folders you need:

```yaml
mkDirs:
  - ${srcOut}:
      - api
      - di:
          - modules
          - outer
      - ui:
          - presenters

  - ${resOut}:
      - layout
      - drawables
      - values
```

Each list item can be [expression](/plugins/hh-geminio/docs/en/EXPRESSIONS.md).

--- 

[Back to the commands list](/plugins/hh-geminio/docs/en/recipe_content/RECIPE.md)