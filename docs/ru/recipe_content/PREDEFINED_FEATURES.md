### Секция `predefinedFeatures`

Секция нужна для включения некоторых встроенных фич внутри Geminio.

Возможные значения списка:

- `enableModuleCreationParams` - добавляет два текстовых поля для ввода имени нового модуля и его основного
  packageName-а. После добавления этой фичи, в [выражениях](../EXPRESSIONS.md) секций [`widgets`](WIDGETS.md)
  и [`globals`](GLOBALS.md), а также в FTL-шаблонах можно использовать следующие параметры:
    * `__moduleName` - Gradle path модуля без ведущего `:`, например `applicant:core`; при генерации файлов
      сегменты path становятся вложенными директориями
    * `__packageName` - packageName модуля, который добавляется в `AndroidManifest.xml`; при автоподстановке
      из `__moduleName` символы `:` заменяются на `.`
    * `__formattedModuleName` - отформатированное имя модуля, которое можно использовать
      в качестве префикса для классов внутри нового модуля.

Также есть возможность изменить значение базового package name-а, подставляемого в шаблон модуля, с
помощью свойства:

```yaml
predefinedFeatures:
  enableModuleCreationParams:
    defaultPackageNamePrefix: ru.hh.test
```

Здесь `ru.hh.test` - значение нужного вам custom-пакета.

Аналогично можно задать `sourceSet` и имя папки (обычно `kotlin`/ `java`) внутри `sourceSet`:

```yaml
predefinedFeatures:
  - enableModuleCreationParams:
      defaultPackageNamePrefix: ru.hh.test
      defaultSourceSetName: main
      defaultSourceCodeFolderName: java
```

---

[Обратно к устройству "рецептов"](../RECIPE_CONTENT.md)
