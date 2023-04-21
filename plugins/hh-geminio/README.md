# Geminio

![Geminio](./img/Geminio.png)

<!-- Plugin description -->
**Geminio** -- Android Studio Plugin for generating code from templates.
<!-- Plugin description end -->


Плагин для генерации кода из freemarker-ных шаблонов

## Зачем

В Android Studio 4.1 отключили поддержку кастомных Freemarker-ных шаблонов. Раньше их можно было создать, положить в
определённую папку внутри Android Studio, и студия самостоятельно подтягивала их в качестве 'Other'-шаблонов.

Начиная с Android Studio 4.1, добавление шаблонов возможно только из IDEA-плагинов. Нас это не устраивает и мы хотим
добавлять / обновлять шаблоны независимо от плагинов.

Для этого и нужен Geminio.

## Содержание

- [Как работает плагин](./docs/ru/HOW_IT_WORKS.md)
- [Конфигурация плагина](./docs/ru/PLUGIN_CONFIG.md)
- [Выражения](./docs/ru/EXPRESSIONS.md)
- [Устройство "рецепта"](./docs/ru/RECIPE_CONTENT.md)
- [Шаблоны модулей](./docs/ru/MODULES_TEMPLATES.md)
