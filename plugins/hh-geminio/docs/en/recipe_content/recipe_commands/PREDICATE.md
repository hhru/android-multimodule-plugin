#### `predicate` command

Options:

- `validIf` — boolean [expression](/plugins/hh-geminio/docs/en/EXPRESSIONS.md) for calculating the predicate for the specified commands;
- `commands` — a set of commands to be executed if `validIf` returns `true`.
- `elseCommands` -- a set of commands to be executed if `validIf` returns `false` 

The command calculates the [expression](/plugins/hh-geminio/docs/en/EXPRESSIONS.md) that you specified in `validIf`. 
If the result is `true`, then the set of commands that you specify in the `commands` list will be executed. 
If `validIf` [expression](/plugins/hh-geminio/docs/en/EXPRESSIONS.md) returns `false`, 
`elseCommands` list will be executed.

This list supports every other commands.

--- 

[Back to the commands list](/plugins/hh-geminio/docs/en/recipe_content/RECIPE.md)