## Конфигурация плагина

Внутри своего проекта создайте файл `geminio_config.yaml` со следующим содержимым:

```yaml
templatesRootDirPath: /android-style-guide/geminio/templates
modulesTemplatesRootDirPath: /android-style-guide/geminio/modules_templates

groupsNames:
  forNewGroup: HH Templates
  forNewModulesGroup: HH Modules
```

- `templatesRootDirPath` - это относительный путь от папки вашего проекта до папки с шаблонами, которую
  должен будет прочитать плагин Geminio

- `modulesTemplatesRootDirPath` - это относительный путь от папки вашего проекта до папки с шаблонами модулей.

- `groupNames` - названия групп, в которые будут добавлены action-ы.
    * `forNewGroup`      - название группы, которая отобразится в меню New (Cmd + N на `Project View`)
    * `forNewModulesGroup`      - название группы с шаблонами модулей, которая отобразится в меню New (Cmd + N
      на `Project View`)

После создания файла, откройте страничку настроек `Preferences` -> `Appearance & Behavior` -> `Geminio plugin`.
Там выберите путь до файла-конфига, нажмите `Apply`.

После этого - перезагружайте проект, и ваши шаблоны должны подтянуться.
При добавлении новой папки шаблона (неважно, модулей или обычных), чтобы Android Studio увидела этот шаблон, придётся
перезагрузить проект.

---

[Обратно к содержанию](../../README.md#Содержание)