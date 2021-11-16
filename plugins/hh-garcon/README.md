# Garcon

<!-- Plugin description -->
**Garcon** -- Plugin for generating Kakao's Page objects from Android's layout.xml files.
<!-- Plugin description end -->

Плагин для ускорения создания Page object-ов с использованием [Kakao DSL](https://github.com/agoda-com/Kakao).

## Содержание

1. [Мотивация](#motivation)
1. [Возможности плагина](#capabilities)
1. [Интеграция](#integration)
    * [Сборка архива плагина](#build_plugin)
    * [Подключение плагина к Android Studio](#connect_plugin)
    * [Настройка плагина](#setup_plugin)
        * [Описание .ftl-шаблонов](#freemarker_templates)
1. [Использование плагина](#using)
    * [Action-ы](#actions)
        * [Generate Page Object Action](#screen_page_object)
        * [Generate RecyclerItem Page Object](#recycler_item_page_object)
        * [Collect Kakao Views Into...](#collect_kakao_views)
    * [Live templates](#live-templates)
        * [Live template - _step_](#live-template---_step_)
        * [Live template - _item_](#live-template---_item_)

## [Мотивация](#motivation)

Рутинная часть написания UI тестов заключается в описании всех виджетов Page Object-ов, которые есть в том или ином 
файле верстки. При этом надо учитывать типы виджетов, давать описываемым свойствам понятные имена, не забывать про 
принятый в компании формат классов Page Object-ов. 

Мы в hh.ru решили ускорить процесс описания таких Page Object-ов при помощи собственного плагина для Android Studio.

## [Возможности плагина](#capabilities)

Плагин добавляет три специальных [Action](https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_actions.html)-а (элементы контекстного меню):

- Создание Page Object-а из файла XML-верстки
- Создание KRecyclerItem Page Object-а из файла XML-верстки
- Добавление Kakao-виджетов из XML-верстки в существующий Page Object

Также, плагин добавляет два [live template](https://www.jetbrains.com/help/idea/using-live-templates.html):

- `step` - для создания каркаса функции, описывающей шаг тестового кейса
- `item` - для добавления типа элементов в `KRecyclerView`

## [Интеграция](#integration)

### [Сборка архива плагина](#build_plugin)

Вы можете как скачать последнюю прикрепленную версию, так и собрать архив плагина самостоятельно.

Для этого:

- Скачайте репозиторий к себе и откройте его в `Intellij IDEA CE` (или `Ultimate`)
- Убедитесь, что у вас установлен плагин `Plugin Dev Kit`
- Открыв проект, дождитесь его синхронизации
- Найдите Gradle-таску `buildPlugin` (`Garcon -> Tasks -> intellij -> buildPlugin`) или запустите из терминала ./gradlew buildPlugin
- Собранный zip-архив плагина будет находиться в папке `./build/distributions`

### [Подключение плагина к Android Studio](#connect_plugin)

Собранный архив плагина можно подключить к Android Studio. Для этого:

- Откройте настройки студии (меню `Preferences`)
- Выберите вкладку `Plugins`
- Сверху найдите иконку с шестеренкой, нажмите на нее, и выберите пункт `Install from disk`
- Укажите путь до собранного .zip-архива
- Перезагрузите IDEA

### [Настройка плагина](#setup_plugin)

Для корректной работы плагину требуется небольшая настройка. 

`Garcon` добавляет собственную страницу конфигурации, которую можно найти в `Preferences -> Appearance & Behavior -> Garcon`.

На этой странице вы должны указать:

- Путь к папке с конфигурацией плагина, например, `/Users/user/Projects/hh-android/code-cookbook/templates/garcon` **(обязательно)** 
- Папку, в которую будут добавляться создаваемые Page Object-ы экрана, по умолчанию **(опционально)**

Плагин будет искать `.ftl`-шаблоны внутри папки `/templates` внутри конфигурационной папки. То есть нужно создать примерно 
следующее дерево файлов:

```
/garcon
    /templates
        screen_page_object.ftl
        rv_item_page_object.ftl
```

#### [Описание .ftl-шаблонов](#freemarker_templates)

Шаблон для Page Object-а экрана должен иметь имя `screen_page_object.ftl`. При генерации кода, 
плагин пробрасывает в него следующие параметры:

- `package_name` -- package name будущего класса, зависит от выбора конечной папки в диалоге создания Page Object-а
- `r_file_package_name` -- package name для `R`-класса файла верстки, из которого будет сгенерирован Page Object. Берется из `AndroidManifest`-файла модуля, в котором находится файл верстки
- `class_name` -- имя класса, которое ввел пользователь в диалоге создания Page Object-а
- `properties_declarations_list` -- список сгенерированных описаний Kakao-виджетов вида `private val titleTextView = KTextView { withId(R.id.activity_main_text_view_title) }`
- `import_classes_fqn_list` - список fully qualified names найденных Kakao-виджетов, например `com.agoda.kakao.text.KButton`, `com.agoda.kakao.text.KTextView` и т.д.

Шаблон для RecyclerItem Page Object-а должен иметь имя `rv_item_page_object.ftl`. При генерации кода, 
плагин пробрасывает в него следующие параметры:
 
- `r_file_package_name` -- package name для `R`-класса файла верстки, из которого будет сгенерирован Page Object. Берется из `AndroidManifest`-файла модуля, в котором находится файл верстки
- `class_name` -- имя класса, которое ввел пользователь в диалоге создания Page Object-а
- `properties_declarations_list` -- список сгенерированных описаний Kakao-виджетов вида `private val titleTextView = KTextView { withId(R.id.activity_main_text_view_title) }`
- `import_classes_fqn_list` - список fully qualified names найденных Kakao-виджетов, например `com.agoda.kakao.text.KButton`, `com.agoda.kakao.text.KTextView` и т.д. 

Примеры написанных шаблонов можно найти [вот здесь](https://github.com/hhru/android-style-guide/tree/master/tools/garcon/templates)

## [Использование плагина](#using)

### [Action-ы](#actions)

Все три добавленных Action-а доступны только из двух мест:

- Во вкладке навигации вы выбрали файл верстки (должен находиться в Android-модуле внутри папки `layout-*`) и 
нажали `Cmd + N`. 
- Находясь в коде XML-файла верстки (который находится в Android-модуле внутри папки `layout-*`), вы нажали `Cmd + N`. 

Каждый из Action-ов работает по следующей схеме:

- Показ диалога для настройки собственных параметров
- Сбор всех XML-тэгов разных View внутри XML-верстки, отмеченных идентификаторами (у View должен быть установлен аттрибут `android:id="@+id/....`)

*Важно!*

Плагин НЕ СОБИРАЕТ View, вложенные в верстку через `<include>`

- Преобразование собранных тэгов в описание Kakao-виджетов (`KTextView`, `KView`, и т.д.) в следующий вид:

```kotlin
private val myWidgetContainer = KView { withId(R.id.activity_main_container_my_widget) }
```

- Проброс сконвертированных виджетов для дальнейшей обработки.

#### [Generate Page Object Action](#screen_page_object)

Данный Action предназначен для генерации Page Object-а Kakao-экрана (наследника класса `com.agoda.kakao.screen.Screen`) 
из XML-файла с версткой. 

При нажатии на пункт меню `Generate Page Object` появится специальный диалог, в котором можно

- набрать название будушего класса
- выбрать package и папку, в которой будет сгенерирован класс Page Object-а 

После нажатия на кнопку `Ok` собранные плагином Kakao-виджеты будут проброшены в описанный вами 
`.ftl`-шаблон `screen_page_object.ftl`. Сгенерированный шаблоном код будет добавлен в качестве нового файла в 
указанную папку.

Пример описанного шаблона можно найти [здесь](https://github.com/hhru/android-style-guide/blob/master/tools/garcon/templates/screen_page_object.ftl).

Если в диалоге проставить галочку `Open in editor`, то сгенерированный файл откроется в редакторе кода.

#### [Generate RecyclerItem Page Object](#recycler_item_page_object)

Данный `Action` предназначен для генерации RecyclerItem Page Object-а Kakao (наследника класса `KRecyclerItem<>`) 
из XML-файла с версткой. 

При нажатии на пункт меню `Generate RecyclerItem Page Object` появится специальный диалог, в котором можно

- набрать название будушего класса
- выбрать класс-наследник `com.agoda.kakao.screen.Screen`, куда будет добавлен класс Page Object-а

После нажатия на кнопку `Ok` собранные плагином Kakao-виджеты будут проброшены в описанный вами 
`.ftl`-шаблон `rv_item_page_object.ftl`. Сгенерированный шаблоном код будет добавлен в качестве нового класса в 
указанный класс.

Пример описанного шаблона можно найти [здесь](https://github.com/hhru/android-style-guide/blob/master/tools/garcon/templates/rv_item_page_object.ftl).

Если в диалоге проставить галочку `Open in editor`, то в редакторе кода будет открыт модифицированный класс.

#### [Collect Kakao Views Into...](#collect_kakao_views)

Данный `Action` предназначен для добавления Kakao-виджетов, сгенерированных из XML-верстки, в существующий класс Page Object-а.

При нажатии на пункт меню `Collect Kakao Views Into...` появится диалог, в котором можно будет выбрать 
класс-наследник `com.agoda.kakao.screen.Screen`, куда будут добавлены собранные Kakao-виджеты.

После нажатия на кнопку `Ok` собранные плагином Kakao-виджеты будут проброшены в указанный вами класс Page Object-а.

Если в диалоге проставить галочку `Open in editor`, то в редакторе кода будет открыт модифицированный класс.

### [Live templates](#live-templates)

Помимо Action-ов плагин добавляет два `live template`–а для ускорения описания Page Object-ов. 
Генерируемый live template-ами код можно модифицировать внутри IDEA через меню:

`Preferences -> Editor -> Live templates -> Garcon`

#### [Live template - _step_](#live-template---_step_)

Данный `live template` можно вызвать внутри класса-наследника `com.agoda.kakao.screen.Screen` и 
внутри специального класса `ru.hh.android.core_tests.page.ScreenIntentions`, в котором мы описываем действия и проверки 
Page Object-ов.

После ввода ключевого слова `step` и нажатия `Tab`, будет подставлен шаблон для добавления новой функции описания 
тестового шага, согласно принятому внутри hh.ru соглашению:

```kotlin
fun TestContext<TestCase>.function_name() {
    step("step_description") {
        // TODO
    }
}
``` 

#### [Live template - _item_](#live-template---_item_)

Данный `live template` можно вызвать внутри описания типов элементов `KRecyclerView`:

```kotlin
private val recycler = KRecyclerView({ withId(R.id.fragment_autosearch_recycler) }) {
    itemType(AutosearchScreen::ItemAutosearchList)
    // Вот здесь можно вызывать описанный live template - `item`
}
```

После ввода ключевого слова `item` и нажатия `Tab`, будет подставлен шаблон для добавления нового типа элемента `KRecyclerView`:

```kotlin
itemType(class_name::item_type_description)
``` 

Здесь:

- `class_name` - название класса, внутри которого объявляется `KRecyclerView` 
- `item_type_description` - название класса, описывающего `KRecyclerItem`


## Лицензия

Плагин является open-source утилитой и распостраняется под лиценцией [MIT](https://github.com/hhru/Garcon/blob/master/LICENSE).