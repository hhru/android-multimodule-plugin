### Секция `widgets`

Секция виджетов - это список параметров вашего шаблона, которые зависят от пользователя.
На данный момент секция поддерживает строковые, булевские и suggest-параметры с поиском.
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

#### `suggestParameter`

`suggestParameter` - описание строкового параметра, который на UI отображается как поле с поиском
и completion-подсказками.

Имеет те же базовые параметры, что и `stringParameter`, включая `suggest`, `visibility` и
`availability`, а также следующие поля:

- `sealed` - необязательный флаг. По умолчанию `false`.
  При `sealed: true` итоговое значение должно совпадать с одним из объявленных option.
- `options` - обязательный источник значений для подсказок. Можно задать в одном из двух видов:

    * прямо в recipe:

      ```yaml
      options:
        - value: compose
          label: Compose
        - value: views
      ```

    * через внешний CSV-файл относительно `recipe.yaml`:

      ```yaml
      options:
        source: options/ui_frameworks.csv
      ```

И inline-значения, и строки CSV поддерживают:

- `value` - фактическое значение, которое попадёт в выражения и FreeMarker-шаблоны
- `label` - необязательный текст, который будет показан пользователю на UI. Если его не задать,
  Geminio использует `value`. Значения `label` должны быть уникальными внутри одного `suggestParameter`

CSV-файлы читаются как UTF-8 и поддерживают строки вида `value` или `value,label`. Необязательный
заголовок `value,label` будет автоматически проигнорирован.

Дополнительно:

- `default` должен совпадать с одним из `options.value` только при `sealed: true`
- `suggest` вычисляется в терминах сохраняемого значения, а не `label`
- при `sealed: true`, если `suggest` вернул значение, которого нет в `options`, Geminio не будет
  применять его. Вместо этого он оставит текущее значение, если оно всё ещё валидно; иначе
  возьмёт `default`, а если и `default` отсутствует или невалиден - откатится к первому option
- при `sealed: false` можно ввести любую строку, а `options` используются только для completion.

---

[Обратно к устройству "рецептов"](../RECIPE_CONTENT.md)
