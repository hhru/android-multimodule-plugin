#### Команда `predicate`

Параметры:

- `validIf` -- булево [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md) для вычисления предиката дя указанных команд
- `commands` -- набор команд, которые должны выполнится, если [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md) `validIf` 
  возвращает `true`.
- `elseCommands` -- набор команд, которые должны выполнится, если [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md) `validIf`
  возвращает `false`.

Команда вычисляет [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md), которое вы указываете в `validIf`.

Если результат равен `true`, то будет выполнен набор команд, которые вы
укажете в списке `commands`, если `false` - выполняются команды в `elseCommands`. 

Списки команд поддерживают все перечисленные команды.

--- 

[Обратно к списку команд](/plugins/hh-geminio/docs/ru/recipe_content/RECIPE.md)