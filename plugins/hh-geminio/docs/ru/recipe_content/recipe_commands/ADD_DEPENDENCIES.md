#### Команда `addDependencies`

Команда добавляет список зависимостей в `build.gradle` файл текущего модуля.
Каждая зависимость имеет следующий формат:

```yaml
- <configurationType>: <dependencyNotation>
```

Здесь:

- `<configurationType>` - тип конфигурации зависимости, может иметь одно из значений:
    * `compileOnly`
    * `api`
    * `implementation`
    * `testImplementation`
    * `androidTestImplementation`
    * `kapt`

- `<dependencyNotation>` - нотация объявления зависимости. Имеет три поддерживаемых формата:
    * Нотация Maven-артефакта, например, `org.company:artifact:version`
    * Зависимость от проекта, например, `:shared-core-model`
    * Константа библиотеки, например, `Libs.jetpack.compose`

--- 

[Обратно к списку команд](/docs/ru/recipe_content/RECIPE.md)