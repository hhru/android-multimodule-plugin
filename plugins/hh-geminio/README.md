# Geminio

![Geminio](img/Geminio.png)

Плагин для генерации кода из freemarker-ных шаблонов

## Зачем

В Android Studio 4.1 отключили поддержку кастомных Freemarker-ных шаблонов. Раньше их 
можно было создать, положить в определённую папку внутри Android Studio, и студия 
самостоятельно подтягивала их в качестве 'Other'-шаблонов. 

Начиная с Android Studio 4.1, добавление шаблонов возможно только из IDEA-плагинов. Нас это
не устраивает и мы хотим добавлять / обновлять шаблоны независимо от плагинов. 

Для этого и нужен Geminio.

## Как работает плагин

При открытии проекта в Android Studio плагин проводит сканирование указанной ему папки в
поисках "рецептов" приготовления ваших шаблонов. Для каждого шаблона создаётся отдельный 
Action (пункт меню), который добавится:

- в меню Generate (Cmd + N - внутри редактора кода)
- в отдельную группу внутри меню New (Cmd + N - в Project View)

После выбора шаблона из меню плагин парсит связанный с Action-ом "рецепт" и выполняет его. 

## Конфигурация плагина

Внутри своего проекта создайте файл `geminio_config.yaml` со следующим содержимым:

```yaml
templatesRootDirPath: /android-style-guide/geminio/templates
groupsNames:
  forNewGroup: HH Template
```

- `templatesRootDirPath` - это относительный путь от папки вашего проекта до папки с шаблонами, которую 
должен будет прочитать плагин Geminio

- `groupNames` - названия групп, в которые будут добавлены action-ы.
    * `forNewGroup`      - название группы, которая отобразится в меню New (CMD+N на Project View) 


После создания файла, откройте страничку настроек Preferences -> Appearance & Behavior -> Geminio plugin.
Там выберите путь до файла-конфига, нажмите Apply. 

После этого - перезагружайте проект, и ваши шаблоны должны подтянуться. 
При добавлении новой папки шаблона, чтобы Android Studio увидела этот шаблон, придётся перезагрузить проект. 


## Устройство "рецепта"

Рецепт - набор инструкций и предварительных условий для выполнения вашего шаблона. 

Важно! Каждый файл рецепта должен называться `recipe.yaml`.

Каждый рецепт выглядит примерно так:

```yaml
requiredParams:
  name: HeadHunter BaseFragment
  description: Creates HeadHunter BaseFragment

optionalParams:
  revision: 1
  category: fragment
  formFactor: mobile
  constraints:
    - kotlin
  screens:
    - fragment_gallery
    - menu_entry
  minApi: 7
  minBuildApi: 8

widgets:
  - stringParameter:
      id: className
      name: Fragment Name
      help: The name of the fragment class to create
      constraints:
        - class
        - nonempty
        - unique
      default: BlankFragment

  - stringParameter:
      id: fragmentName
      name: Fragment Layout Name
      help: The name of the layout to create
      constraints:
        - layout
        - nonempty
        - unique
      default: fragment_blank
      suggest: fragment_${className.classToResource()}

  - booleanParameter:
      id: includeFactory
      name: Include fragment factory method?
      help: Generate static fragment factory method for easy instantiation
      default: true

  - booleanParameter:
      id: includeModule
      name: Include Toothpick Module class?
      help: Generate fragment Toothpick Module for easy instantiation
      default: true

  - stringParameter:
      id: moduleName
      name: Fragment Toothpick Module
      help: The name of the Fragment Toothpick Module to create
      constraints:
        - class
        - nonempty
        - unique
      default: BlankModule
      visibility: ${includeModule}
      suggest: ${className.classToResource().underlinesToCamelCase()}Module

recipe:
  - instantiateAndOpen:
      from: root/src/app_package/BlankFragment.kt.ftl
      to: ${srcOut}/${className}.kt
  - instantiate:
      from: root/res/layout/fragment_blank.xml.ftl
      to: ${resOut}/layout/${fragmentName}.xml
  - open:
      file: ${resOut}/layout/${fragmentName}.xml
  - predicate:
      validIf: ${includeModule}
      commands:
        - instantiate:
            from: root/src/app_package/BlankModule.kt.ftl
            to: ${srcOut}/di/${moduleName}.kt
        - open:
            file: ${srcOut}/di/${moduleName}.kt
  - addDependencies:
      - implementation: Libs.jetpack.compose
      - kapt: Libs.di.toothpick
      - compileOnly: com.github.stephanenicolas.toothpick:toothpick:3.1.0
      - testImplementation: :shared-core-test
      - androidTestImplementation: Libs.uiTests.kaspresso
```  

Рецепт состоит из 4-х секций

- `requiredParams` - обязательные параметры для вашего шаблона
- `optionalParams` - соответственно, необязательные параметры
- `widgets` - описание параметров шаблона - для использования доступны строковые и булевские параметр
- `recipe` - сам рецепт, набор инструкций, которые нужно выполнить

Если вам кажется, что это похоже на старые-добрые Freemarker-ные template.xml && recipe.xml, 
только в одном файле, то вам не кажется.

### Секция `requiredParams`

Обязательных параметров всего два: 

- `name` - название шаблона (оно должно быть уникальным среди ваших шаблонов и шаблонов, которые уже есть в Android Studio) 
- `description` - описание шаблона, что он делает

Без этих параметров работа по рецепту не начнётся. 

### Секция `optionalParams`

Необязательных параметров будет чуть больше. 
Они являются обязательными для работы движка по работе с шаблонами, но в контексте работы плагина они 
пока не нужны. Возможно, в будущем мы добавим возможность автоматического добавления шаблонов через 
существующие extension point-ы, но сейчас эти параметры **ни на что не влияют**.

-  `revision` - это число нужно для переопределения существующих шаблонов: 
допустим, вы хотите переопределить шаблон созданиия нового пустого фрагмента в Android Studio. Если ваше значениие 
revision будет выше стандартного, а имя шаблона будет совпадать, то Android Studio возьмёт именно ваш шаблон.

- `category` - категория шаблонов, куда должен попасть шаблон. Доступные значения:
    * `activity`
    * `fragment`
    * `application`
    * `folder`
    * `ui_component`
    * `automotive`
    * `xml`
    * `wear`
    * `aidl`
    * `widget`
    * `google`
    * `compose`
    * `other`
    
- `formFactor` - определяет, к какому форм-фактору принадлежит шаблон. 
    Это влияет на то, в каких проектах может показываться ваш шаблон.
    Доступные значения:
  * `mobile`
  * `wear`
  * `tv`
  * `automotive`
  * `things`
  * `generic`

- `constraints` - ограничения на проект, в котором может работать ваш шаблон. 
Например, ваш шаблон должен работать только в том проекте, в котором подключен Kotlin.
Доступные значения для списка:
  * `androidx`
  * `kotlin`
  
- `screens` - список wizard-ов галерей внутри Android Studio, в которых может появиться ваш шаблон.
Доступные значения:
  * `new_project`
  * `new_module`
  * `menu_entry`
  * `activity_gallery`
  * `fragment_gallery`

- `minApi` - значение minSdkVersion проекта, необходимое для работы вашего шаблона

- `minBuildApi` - значение compileSdkVersion проекта, необходимое для работы вашего шаблона 

### Секция `widgets`

Секция виджетов - это список параметров вашего шаблона, которые зависят от пользователя. 
В данный момент поддерживаются строковые и булевские параметры. Параметры поддерживают вычисление `выражений`. 

#### `выражения` (expressions)

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
to: ${resOut}/layout/${fragmentName}.xml
file: ${srcOut}/di/${moduleName}.kt
```

В секции `recipe` добавляются 2 встроенных параметра:

- `resOut` - путь до папки `res` внутри модуля, где запущен шаблон
- `srcOut` - путь до папки `src/main/<source-set>/<current-dir>` в модуле, где запущен шаблон


#### `stringParameter`

`stringParameter` - описание строкового параметра для вашего шаблона (например, название класса, названиие layout-а и т.д).
Будет преобразовано в текстовое поле ввода на UI.

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

- `default` - значение параметра по умолчанию
- `suggest` - текстовое `выражение` для автоматического изменения поля, которое может зависеть от других полей
- `visibility` - булево `выражение` для показа / скрытия поля в зависимости от условия
- `availability` - булево `выражение` для переключения поля в состояния enabled / disabled в зависимости от условия

#### `booleanParameter`

`booleanParameter` - описание булевого параметра для вашего шаблона (например, нужно ли сгенерировать модуль, какой-то доп. метод и так далее).
Будет преобразовано в чекбокс на UI. 

Имеет ровно такие же параметры, как и `stringParamter`, только у `booleanParameter` нет `suggest`.

### Секция `recipe`

Секция представляет собой список команд, которые нужно исполнить Action-у после того, как 
пользователь выберет все необходимые параметры в вашем шаблоне.

#### Команда `instantiate`

Параметры:

- `from` -- текстовое `выражение`, обычно - относительный путь от корня папки с рецептом до нужного ftl-шаблона
- `to` -- текстовое `выражение`, путь до целевого файла с кодом

Команда берёт freemarker-ный ftl-файл, путь до которого вычисляется из `выражения` `from`, 
пробрасывает в него параметры, генерирует код, и помещает результат в файл, путь которого указан
в `выражении` `to`.

#### Команда `open`

Параметры:

- `file` -- текстовое `выражение`, путь до целевого файла, который нужно открыть

Команда открывает в редакторе кода указанный файл

#### Команда `instantiateAndOpen`

Параметры:

- `from` -- текстовое `выражение`, обычно - относительный путь от корня папки с рецептом до нужного ftl-шаблона
- `to` -- текстовое `выражение`, путь до целевого файла с кодом

Команда сразу совмещает в себе команды `instantiate` и `open` - создаёт и открывает файл по пути `to`.

#### Команда `predicate`

Параметры:

- `validIf` -- булево выражение для вычисления предиката дя указанных команд
- `commands` -- набор команд, которые должны выполнится, если выражение `validIf` возвращает `true`.

Команда вычисляет выражение, которое вы указываете в `validIf`. 
Если результат равен `true`, то будет выполнен набор команд, которые вы 
укажете в списке `commands`. Список поддерживает все перечисленные выше команды.

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