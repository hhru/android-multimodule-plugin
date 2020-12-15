#### Команда `mkDirs`

Команда предназначена для создания структуры папок в модуле, который запустил шаблон.

Параметрами этой команды являются иерархичные структуры названий нужных вам папок:

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

Каждый элемент списка может быть представлен [выражением](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md).

--- 

[Обратно к списку команд](/plugins/hh-geminio/docs/ru/recipe_content/RECIPE.md)