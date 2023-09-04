#### `instantiate` command

Options:

- `from` — string [expression](../../EXPRESSIONS.md), usually a relative path from the root of the recipe folder to the
  desired ftl-template;
- `to` — string [expression](../../EXPRESSIONS.md), path to the target file with the code.

This command takes a FreeMarker ftl-file, the path to which is calculated from
the `from` [expression](../../EXPRESSIONS.md), passes parameters into it, generates the code and puts the result into
the file, the path of which is specified in the `to` [expression](../../EXPRESSIONS.md).

--- 

[Back to the commands list](../RECIPE.md)