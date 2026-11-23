# Формат темы igxtulGram

Тема — это JSON. Минимальный пример:

```json
{
  "id": "mytheme",
  "name": "Моя тема",
  "author": "igxtul",
  "description": "Описание",
  "accent": "#FF7B5CFF",
  "background": "#FF0E1015",
  "bubble_in": "#FF1C202B",
  "bubble_out": "#FF4A3AA8",
  "corner_radius_dp": 14,
  "font_size_sp": 15
}
```

## Ключи

| Ключ | Тип | Диапазон | Что меняет |
|---|---|---|---|
| `accent` | color | — | Акцент, статус-бар (через модуль) |
| `background` | color | — | Фон экрана |
| `bubble_in` | color | — | Входящий пузырь |
| `bubble_out` | color | — | Исходящий пузырь |
| `bubble_in_text` | color | — | Текст входящего |
| `bubble_out_text` | color | — | Текст исходящего |
| `text_primary` | color | — | Основной текст |
| `text_secondary` | color | — | Второстепенный текст |
| `corner_radius_dp` | float | 0–40 | Скругление пузырей |
| `bubble_spacing_dp` | float | 0–16 | Отступ между пузырями |
| `font_size_sp` | int | 11–30 | Размер текста |
| `font_path` | string | — | Путь к TTF в `assets` |
| `wallpaper_dim` | int | 0–100 | Затемнение обоев, % |
| `rounded_avatars` | bool | — | Круглые аватары |
| `show_timestamps` | bool | — | Время в сообщениях |
| `show_read_marks` | bool | — | Галочки прочтения |
| `compact_chat_list` | bool | — | Компактный список |
| `gradient_bubbles` | bool | — | Градиент исходящих |
| `blur_wallpaper` | bool | — | Размытие обоев |

Цвет принимается как `#RRGGBB` или `#AARRGGBB`. Если альфа не указана,
ставится `FF`.
Значения вне диапазона автоматически ограничиваются (`sanitize()`).

## Приоритет

1. Значения по умолчанию ядра.
2. Выбранная тема.
3. Только те ключи, которые пользователь изменил вручную.
4. Производные значения модулей (контраст текста, статус-бар, градиент).

Поэтому смена темы сохраняет ваши правки.

## Импорт

Через экран настроек: вставьте JSON в поле «Настройки JSON» и нажмите «Импорт».
Или из кода:

```java
Theme t = Theme.fromJson(json);
IgxtulGram.get().saveCustomTheme(t);
```

Готовые примеры лежат в `themes/`.
