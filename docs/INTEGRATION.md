# Интеграция igxtulGram в исходник Telegram

Мод работает **внутри форка**: исходники Telegram правится напрямую.
Это не Xposed-хук и не LSPosed-модуль — рантайм-хуки здесь не нужны.

## 0. Исходник

Возьмите официальный Android-исходник:
`https://github.com/TelegramMessenger/Telegram-android`.
Основной модуль называется `TMessagesProj`. Точные имена классов и методы
в нём **обфусцированы и меняются от версии к версии** — ищите по смыслу,
а не по этому документу вслепую.

## 1. Перенос кода

Скопируйте папки в `TMessagesProj/src/main/java/`:

```
IgxtulGram/core/src/main/java/com/igxtul/gram/core/     -> .../com/igxtul/gram/core/
IgxtulGram/android/src/main/java/com/igxtul/gram/android/ -> .../com/igxtul/gram/android/
```

Зависимостей нет. Если не хотите Gradle-модули — просто копируйте `.java`.
`AndroidManifest.xml` из `android/` нужен только для объявления экрана настроек;
в форке можно добавить одну строку activity в его манифест.

## 2. Инициализация

В `org.telegram.ui.LaunchActivity`, в `onCreate(...)` сразу после `super.onCreate(...)`:

```java
import com.igxtul.gram.android.IgxtulGram;
import com.igxtul.gram.android.AndroidTarget;

IgxtulGram.init(getApplicationContext(), new AndroidTarget(this));
```

В `onResume()` того же активити, чтобы подхватить изменения извне:

```java
if (IgxtulGram.get() != null) IgxtulGram.get().apply();
```

## 3. Пузыри сообщений

Найдите `org.telegram.ui.Cells.ChatMessageCell` и место, где задаётся фон
(поиск по `setBackgroundDrawable` / `backgroundDrawable` / `getResources().getDrawable`).
После установки фона добавьте:

```java
import com.igxtul.gram.android.IgxtulGram;

IgxtulGram.applyBubble(this, currentMessage != null && currentMessage.isOut());
```

Если хотите размечать другие вьюхи, используйте теги из `AndroidTarget`:

- `igxtul:bg:bubble_out` — фон берётся из ключа `bubble_out`;
- `igxtul:color:accent` — цвет текста из ключа `accent`;
- `igxtul:font` — шрифт из настроек;
- `igxtul:text` — размер текста из настроек.

## 4. Вход в настройки

Добавьте пункт меню в `LaunchActivity` или `ChatActivity`:

```java
IgxtulGram.openSettings(this);
```

И объявите активити в манифесте:

```xml
<activity android:name="com.igxtul.gram.android.ModSettingsActivity"
    android:label="igxtulGram" android:exported="false" />
```

## 5. Загрузка тем из assets

```java
String json = /* прочитать themes/neon.json из assets */;
Theme custom = Theme.fromJson(json);
if (IgxtulGram.get() != null) IgxtulGram.get().saveCustomTheme(custom);
```

## 6. Проверка

1. Запустить приложение, открыть `igxtulGram` из меню.
2. Выбрать тему Neon, verify радиус и цвет пузыря в чате.
3. Выключить модуль «Градиент пузырей» → входящие/исходящие стали однотонными.
4. Нажать «Экспорт», перезапустить, «Импорт» → настройки вернулись.
5. Self-test ядра: `java -cp out com.igxtul.gram.core.EngineSelfTest`.

## Важно

- Каждая версия Telegram отличается: после обновления исходника проверьте места
  интеграции заново — имена методов могут поменяться.
- Мод не читает и не отправляет сообщения: он только меняет внешний вид.
- При публикации не используйте официальные название и логотип Telegram.
