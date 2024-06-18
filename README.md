# hh-android-plugins

Репозиторий с плагинами для Android Studio, разработанными в hh.ru .

### Список плагинов в репозитории

- [hh-carnival](./plugins/hh-carnival) - плагин для быстрого создания нового feature-модуля, который также добавляет ещё немного полезных вещей
- [hh-garcon](./plugins/hh-garcon) - плагин для быстрого создания Page Object-ов из XML-вёрстки
- [hh-geminio](./plugins/hh-geminio) - плагин, добавляющий возможность создавать свои шаблоны кода на основе FreeMarker-а

Готовые дистрибутивы плагинов можно скачать 
[на страничке релизов в Github](https://github.com/hhru/android-multimodule-plugin/releases/).

Чтобы установить готовый дистрибутив, открываем Android Studio и идём по пути: 
`Preferences -> Plugins -> иконка шестерёнки -> Install Plugin from disk`, выбираем скачанный zip-архив, дожидаемся 
установки, по необходимости перезагружаем Android Studio.

### Чтобы собрать любой из плагинов, нужно

1. Убедитесь, что у вас есть Intellij IDEA (минимум CE) с подключенным Plugin Dev Kit
2. Склонировать проект
3. Если вы скачивали Android Studio через Toolbox, или у вас не Mac OS, то вероятно вам потребуется изменить путь к локальной версии Android Studio, который указывается [вот здесь](/libraries/src/main/kotlin/ru/hh/plugins/ExternalLibrariesExtension.kt) - объект `Product.LOCAL`
4. Запустить gradle-таску `buildPlugin` в проекте нужного плагина
5. Собранный zip-архив плагина можно подключить в Android Studio через `Preferences -> Plugins -> Install from disk`

Готово, можно пользоваться!

## Полезные ссылки про разработку плагинов

- [How to Build Developer Tools on Top of IntelliJ Platform](https://www.youtube.com/watch?v=S1u8uVjPjvc&list=PLTTdNLQSLhAIFZo6kTpcf8PiPABcyYkSi&index=3&t=0s)
- [KotlinConf 2018 // Writing IntelliJ Plugins for Kotlin by Alec Strong & Egor Andreevici](https://www.youtube.com/watch?v=j2tvi4GbOr4)
- [Droidcon Italy 2017 // Make your Android Studio Plugin - Andrea Lucibello](https://www.youtube.com/watch?v=znDROg5CzZw)
- [Write an Android Studio Plugin Part 1: Creating a basic plugin](https://proandroiddev.com/write-an-android-studio-plugin-part-1-creating-a-basic-plugin-af956c4f8b50)

На русском языке:

- [Mobius 2019 Piter // Фантастические плагины и где они обитают](https://www.youtube.com/watch?v=MSMI85JMIwE&ab_channel=Mobius)
- [Mobius 2020 Piter // Служебный плагин](https://www.youtube.com/watch?v=QKervhpICjk&ab_channel=Mobius)
- [Joker 2019 // Краш-курс по IntelliJ IDEA Plugin DevKit](https://www.youtube.com/watch?v=xoTrgA2zOJI&ab_channel=JUG.ru)
