# igxtulGram (iGX)

Клиент-мод для Android-версии Telegram, заточенный под кастомизацию.
Название собрано из ника **igxtul** + `gram` (Telegram). Версия 1.0.0.

> Что это: готовый **движок кастомизации** + Android-слой + экран настроек + пресеты.
> Это не готовый APK: чтобы получить рабочий клиент, нужно взять исходник
> Telegram Android и встроить эти исходники по инструкции в `docs/INTEGRATION.md`.
> Сборка форка Telegram — тяжёлая, обычно делается на ПК с Android SDK/NDK.

## Что умеет

- **Темы**: 5 встроенных пресетов (Midnight, AMOLED, Nord, Paper, Neon) и свои темы в JSON.
- **Приоритет правок**: смена темы не стирает ручные настройки — хранится только то, что вы изменили.
- **Цвета**: акцент, фон, входящие/исходящие пузыри, текст, статус-бар.
- **Размеры**: радиус пузырей, размер текста, затемнение обоев.
- **Флаги**: круглые аватары, время, галочки, компактный список, градиент, размытие обоев.
- **Шрифт**: путь к TTF в `assets` (например `fonts/inter.ttf`).
- **Модули**: контрастный текст, цвет статус-бара, градиент — включаются отдельно.
- **Экспорт/импорт** всех настроек одной JSON-строкой.
- **Self-test** ядра без JUnit и без Android.

## Структура

```
IgxtulGram/
├── core/            чистая Java-логика, не зависит от Android
│   └── src/main/java/com/igxtul/gram/core/
│       ├── Json.java             мини-JSON без зависимостей
│       ├── ColorUtil.java        ARGB, контраст, затемнение
│       ├── ModConfig.java        ключ-значение + "грязные" ключи
│       ├── Theme.java            тема = конфиг + метаданные
│       ├── ThemeResolver.java    тема + правки пользователя
│       ├── PresetManager.java    встроенные и свои темы
│       ├── ConfigStore.java      абстракция хранилища
│       ├── CustomizationTarget   контракт с UI Telegram
│       ├── Feature.java          модули кастомизации
│       ├── CustomizationEngine   фасад мода
│       └── features/             3 готовых модуля
│   └── src/test/java/.../EngineSelfTest.java
├── android/        реализация Android-слоя
│   └── src/main/java/com/igxtul/gram/android/
│       ├── AndroidConfigStore    SharedPreferences
│       ├── AndroidTarget         применение к View, пузырям, шрифту
│       ├── ModSettingsActivity   экран настроек (кодом, без XML)
│       └── IgxtulGram            точка входа
├── themes/          готовые JSON-темы
└── docs/            интеграция и формат тем
```

## Быстрый старт

1. Проверить ядро (нужен JDK):
   ```
   javac -d out $(find core/src/main/java core/src/test/java -name '*.java')
   java -cp out com.igxtul.gram.core.EngineSelfTest
   ```
   Ожидаемо: `igxtulGram core: ALL OK (23 checks)`.
2. Встроить в форк Telegram: `docs/INTEGRATION.md`.
3. Собрать APK через Gradle/Android Studio в исходнике Telegram.

## Совместимость

- Java 8, Android minSdk 21.
- Без сторонних библиотек: свою JSON-логику можно использовать хоть в чистой Java.
- Не аффилирован с Telegram. При публикации форка не используйте официальное
  название/логотип и соблюдайте условия лицензии исходников.

Сделано под ник **igxtul**.
