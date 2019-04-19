# Android-multimodule-plugin

Плагин для Android Studio, предназначенный для быстрого создания нового feature-модуля. Этот плагин добавляет специальный пункт меню "New -> New feature module...", который откроет специальный визард для создания нового модуля.

### Что умеет плагин:

- Создавать нужную структуру папок и генерировать новые файлы для каркаса feature-модуля
- Добавлять выбранные существующие модули к создаваемому модулю
- Выбирать application модуль, к которому нужно подключить новый feature-модуль

### Что делает плагин после прохождения визарда:

- Создает структуру папок для feature-модуля
- Генерирует файлы, в соответствии с выбранными опциями

Плагин умеет генерировать презентеры, фрагменты, репозитории, интеракторы, DI модули, стандартные файлы нового модуля (AndroidManifest, build.gradle, proguard.pro, .gitignore)

- Донастраивает Toothpick в application-модуле

В build.gradle файле application-модуля есть блок 

```groovy
android {
	
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {

                arguments = [
                        toothpick_registry_package_name          : 'ru.hh.android',
                        toothpick_registry_children_package_names: [
                                // Core
                                'ru.hh.android.module_1',

                                // Features
                                'ru.hh.android.module_4'
                        ].join(",")
                ]
            }
        }
    }

}
```

Плагин добавляет package name нового feature-модуля в массив `toothpick_registry_children_package_names`.

- Донастраивает Moxy в application-модуле

Плагин находит класс, отмеченный аннотацией `@RegisterMoxyReflectorPackages`, и добавляет туда package name нового модуля 

- Добавляет модуль в зависимости application-модуля 

### Чтобы собрать плагин, нужно 

1. Убедитесь, что у вас есть Intellij IDEA (минимум CE) с подключенным Plugin Dev Kit
2. Склонировать проект и запустить его в Intellij IDEA
3. Запустить gradle-таску `buildPlugin`
4. Собранный zip-архив плагина нужно подключить в Android Studio через `Preferences -> Plugins -> Install from disk`

Готово, можно пользоваться!

## Внимание

Этот плагин был разработан с учетом специфики проекта Android-приложения HeadHunter, поэтому он может не работать для других проектов. Для внешнего использования это, скорее, пример того, что можно сделать при помощи плагина, и как это сделать правильно, чтобы все работало.

Что можно посмотреть и почитать про разработку плагинов:

- https://www.youtube.com/watch?v=S1u8uVjPjvc&list=PLTTdNLQSLhAIFZo6kTpcf8PiPABcyYkSi&index=3&t=0s
- https://www.youtube.com/watch?v=j2tvi4GbOr4
- https://www.youtube.com/watch?v=znDROg5CzZw
- https://proandroiddev.com/write-an-android-studio-plugin-part-1-creating-a-basic-plugin-af956c4f8b50
