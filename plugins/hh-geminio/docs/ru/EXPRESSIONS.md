### Expressions - выражения

Некоторые настройки параметров шаблона могут быть представлены "выражениями" -
это строчки определённого формата, которые могут быть вычислены для работы шаблонов.

Выражения бывают двух типов - текстовые и булевы.

Примеры выражений для секции `widgets`:

```yaml
suggest: fragment_${className.classToResource().underlinesToCamelCase()}
visibility: ${includeModule}
availability: true
```

Текст внутри ${} считается за "динамическую" часть выражения, которую требуется вычислять в зависимости от содержания,
текст вне фигурных скобок - фиксированная часть.
Внутри ${} можно использовать только те параметры, которые уже были объявлены ВЫШЕ по тексту рецепта.

Для текстовых параметров можно использовать дополнительные extension-функции:

- `activityToLayout`
- `fragmentToLayout`
- `classToResource`
- `camelCaseToUnderlines`
- `layoutToActivity`
- `layoutToFragment`
- `underlinesToCamelCase`

Для булевских выражений есть специальные значения - `true` / `false`
+ для них возможно использование только только boolean-параметров

Примеры выражений для секции `recipe`:

```yaml
- open:
    file: ${srcOut}/di/${moduleName}.kt
    
- instantiate:
    from: root/build.gradle.ftl
    to: ${rootOut}/build.gradle

- instantiateAndOpen:
    from: root/res/layout/fragment_container.xml.ftl
    to: ${resOut}/layout/${fragmentName}.xml
    
- instantiateAndOpen:
    from: root/main/AndroidManifest.xml
    to: ${manifestOut}/AndroidManifest.xml
```

В секции `recipe` добавляются 4 встроенных параметра:

- `rootOut` - рутовая папка модуля
- `manifestOut` - путь до папки `src/main` в модуле
- `resOut` - путь до папки `res` внутри модуля
- `srcOut` - путь до папки `src/<source-set>/<current-dir>` в модуле

---

[Обратно к содержанию](/plugins/hh-geminio/README.md#Содержание)