#### Команда `predicate`

Параметры:

- `validIf` -- булево [выражение](../../EXPRESSIONS.md) для вычисления предиката дя указанных команд
- `commands` -- набор команд, которые должны выполнится, если [выражение](../../EXPRESSIONS.md) `validIf`
  возвращает `true`.
- `elseCommands` -- набор команд, которые должны выполнится, если [выражение](../../EXPRESSIONS.md) `validIf`
  возвращает `false`.

Команда вычисляет [выражение](../../EXPRESSIONS.md), которое вы указываете в `validIf`.

Если результат равен `true`, то будет выполнен набор команд, которые вы
укажете в списке `commands`, если `false` - выполняются команды в `elseCommands`.

Списки команд поддерживают все перечисленные команды.

--- 

[Обратно к списку команд](../RECIPE.md)