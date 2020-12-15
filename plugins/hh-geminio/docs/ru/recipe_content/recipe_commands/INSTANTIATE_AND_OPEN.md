#### Команда `instantiateAndOpen`

Параметры:

- `from` -- текстовое [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md), 
  обычно - относительный путь от корня папки с рецептом до нужного ftl-шаблона
- `to` -- текстовое [выражение](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md), 
  путь до целевого файла с кодом

Команда сразу совмещает в себе команды [instantiate](/plugins/hh-geminio/docs/ru/recipe_content/recipe_commands/INSTANTIATE.md) и 
[open](/plugins/hh-geminio/docs/ru/recipe_content/recipe_commands/OPEN.md) - создаёт и открывает файл по пути `to`.

--- 

[Обратно к списку команд](/plugins/hh-geminio/docs/ru/recipe_content/RECIPE.md)