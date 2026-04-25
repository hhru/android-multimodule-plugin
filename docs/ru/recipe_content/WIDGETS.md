### Секция `widgets`

Секция виджетов - это список параметров вашего шаблона, которые зависят от пользователя.
В данный момент поддерживаются строковые и булевские параметры.
Параметры поддерживают вычисление [выражений](../EXPRESSIONS.md).

#### `stringParameter`

`stringParameter` - описание строкового параметра для вашего шаблона (например, название класса, названиие layout-а и
т.д). Будет преобразовано в текстовое поле ввода на UI.

Обязательными значениями внутри этого блока являются:

- `id` -- идентификатор параметра, этот параметр можно будет использовать в `выражениях`
  и в шаблонах Freemarker-а при генерации кода

- `name` -- Значение будет указано возле текстового поля при рендере шаблона

Необязательные параметры:

- `help` -- Подсказка для текстового поля

- `constraints` - Ограничения для валидации текстового поля.
  Доступные значения:
    * `unique`
    * `exists`
    * `nonempty`
    * `activity`
    * `class`
    * `package`
    * `app_package`
    * `module`
    * `layout`
    * `drawable`
    * `navigation`
    * `values`
    * `source_set_folder`
    * `string`
    * `uri_authority`
    * `kotlin_function`

  `nonempty` проверяет, что значение не пустое. `class`, `activity`, `package`, `app_package`,
  `source_set_folder`, `layout`, `drawable`, `navigation`, `values`, `string`, `uri_authority` и
  `kotlin_function` проверяют формат значения. `module` оставлен совместимым с Android Studio wizard:
  сам constraint не запрещает синтаксис значения, включая `:`.

  `unique` и `exists` применяются вместе с типовыми constraints, когда UI может сопоставить значение с
  файловым контекстом текущего шаблона:
    * `module` - проверяют Gradle path уже подключенного модуля проекта. Path вычисляется из content root модуля
      относительно корня проекта: `applicant/feature/auth` превращается в `applicant:feature:auth`, а suffix
      `/src/<sourceSet>` отбрасывается без привязки к конкретным именам source set;
    * `package` / `app_package` - проверяют package path в source root создаваемого модуля, если он известен;
    * `layout`, `drawable`, `navigation`, `values` - проверяют ресурс в соответствующей папке `res`;
    * `string` - проверяет наличие string resource в `res/values`.

- `default` - значение параметра по умолчанию
- `suggest` - текстовое [выражение](../EXPRESSIONS.md) для автоматического изменения поля, которое может зависеть от
  других полей
- `visibility` - булево [выражение](../EXPRESSIONS.md) для показа / скрытия поля в зависимости от условия
- `availability` - булево [выражение](../EXPRESSIONS.md) для переключения поля в состояния enabled / disabled в
  зависимости от условия

#### `booleanParameter`

`booleanParameter` - описание булевого параметра для вашего шаблона (например, нужно ли сгенерировать модуль, какой-то
доп. метод и так далее). Будет преобразовано в чекбокс на UI.

Имеет ровно такие же параметры, как и `stringParamter`, только у `booleanParameter` нет `suggest`.

---

[Обратно к устройству "рецептов"](../RECIPE_CONTENT.md)
