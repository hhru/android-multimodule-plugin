### Секция `predefinedFeatures`

Секция нужна для включения некоторых встроенных фич внутри Geminio. 

Возможные значения списка:

- `enableModuleCreationParams` - добавляет два текстовых поля для ввода имени нового модуля и его основного packageName-а. 
После добавления этой фичи, в [выражениях](/plugins/hh-geminio/docs/ru/EXPRESSIONS.md) 
  секций [`widgets`](/plugins/hh-geminio/docs/ru/recipe_content/WIDGETS.md) и [`globals`](/plugins/hh-geminio/docs/ru/recipe_content/GLOBALS.md), 
  а также в FTL-шаблонах можно использовать следующие параметры:
  * `__moduleName` - имя модуля 
  * `__packageName` - packageName модуля, который добавляется в `AndroidManifest.xml`
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

---

[Обратно к устройству "рецептов"](/plugins/hh-geminio/docs/ru/RECIPE_CONTENT.md)