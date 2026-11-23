package com.igxtul.gram.core;

import java.util.HashMap;
import java.util.Map;

import com.igxtul.gram.core.features.ContrastTextFeature;

/** Простой self-test без JUnit: javac + java, работает даже на телефоне. */
public final class EngineSelfTest {
    private static int checks;

    private static final class Target implements CustomizationTarget {
        final Map<String, Integer> colors = new HashMap<String, Integer>();
        final Map<String, Float> metrics = new HashMap<String, Float>();
        final Map<String, Boolean> flags = new HashMap<String, Boolean>();
        String font = "";
        int refreshes;

        @Override public void setColor(String key, int argb) { colors.put(key, argb); }
        @Override public void setMetric(String key, float value) { metrics.put(key, value); }
        @Override public void setFlag(String key, boolean value) { flags.put(key, value); }
        @Override public void setFont(String path) { font = path; }
        @Override public void refresh() { refreshes++; }
    }

    public static void main(String[] args) {
        // 1. JSON + тема
        String json = "{\"id\":\"test\",\"name\":\"Te\\\"st\\n\",\"accent\":\"#123456\",\"corner_radius_dp\":22}";
        Theme t = Theme.fromJson(json);
        eq("test", t.id(), "id темы");
        eq("Te\"st\n", t.name(), "escape в JSON");
        eq(0xFF123456, t.color(ModConfig.K_ACCENT, 0), "цвет из строки");
        eq(22f, t.flt(ModConfig.K_CORNER_RADIUS, 0), "метрика из JSON");
        Object parsed = Json.parse(Json.stringify(t.toMap()));
        eq("test", Json.str(Json.object(parsed).get("id"), ""), "roundtrip JSON");

        // 2. Пресеты
        PresetManager presets = new PresetManager();
        ok(presets.get("midnight") != null, "встроенная тема midnight");
        ok(presets.get("no_such_theme") == null, "неизвестная тема");

        // 3. Приоритет ручных правок над темой
        Theme base = presets.get("nord");
        ModConfig user = new ModConfig();
        user.set(ModConfig.K_ACCENT, 0xFFFF0000);
        ModConfig eff = ThemeResolver.resolve(base, user);
        eq(0xFFFF0000, eff.color(ModConfig.K_ACCENT, 0), "правка пользователя важнее темы");
        eq(base.color(ModConfig.K_BACKGROUND, 0), eff.color(ModConfig.K_BACKGROUND, 0), "остальное из темы");

        // 4. Движок применяет значения и фичи
        MemoryStore store = new MemoryStore();
        CustomizationEngine engine = new CustomizationEngine(store);
        Target target = new Target();
        engine.attach(target);
        ok(target.refreshes == 1, "refresh после attach");
        eq(presets.get("midnight").color(ModConfig.K_BACKGROUND, 0),
                target.colors.get(ModConfig.K_BACKGROUND), "фон применён");
        ok(target.colors.containsKey("status_bar"), "фича статус-бара");
        ok(target.colors.containsKey("bubble_out_gradient"), "фича градиента");

        ContrastTextFeature contrast = null;
        for (Feature f : engine.features()) if (f instanceof ContrastTextFeature) contrast = (ContrastTextFeature) f;
        ok(contrast != null, "фича контраста зарегистрирована");
        eq(true, engine.isFeatureEnabled(contrast), "фича включена по умолчанию");
        engine.setFeatureEnabled("contrast_text", false);
        eq(false, engine.isFeatureEnabled(contrast), "фича выключена");

        // 5. Сохранение и восстановление настроек
        engine.setOption(ModConfig.K_CORNER_RADIUS, 999f);
        eq(40f, engine.effective().flt(ModConfig.K_CORNER_RADIUS, 0), "ограничение радиуса");
        engine.setOption(ModConfig.K_FONT_PATH, "fonts/inter.ttf");
        ok(store.load() != null && store.load().contains("igxtulGram"), "конфиг сохранён");
        CustomizationEngine restored = new CustomizationEngine(store);
        eq("fonts/inter.ttf", restored.effective().str(ModConfig.K_FONT_PATH, ""), "шрифт восстановлен");
        eq(40f, restored.effective().flt(ModConfig.K_CORNER_RADIUS, 0), "радиус восстановлен");

        // 6. Экспорт/импорт
        String dump = engine.exportSettings();
        CustomizationEngine imported = new CustomizationEngine(new MemoryStore());
        imported.importSettings(dump);
        eq("fonts/inter.ttf", imported.effective().str(ModConfig.K_FONT_PATH, ""), "импорт настроек");

        // 7. Пользовательская тема
        Theme custom = Theme.fromJson("{\"id\":\"my\",\"name\":\"My\",\"accent\":\"#00FF00\"}");
        engine.saveCustomTheme(custom);
        ok(engine.presets().get("my") != null, "своя тема сохранена");
        eq(0xFF00FF00, engine.effective().color(ModConfig.K_ACCENT, 0), "своя тема активна");

        System.out.println("igxtulGram core: ALL OK (" + checks + " checks)");
    }

    private static void ok(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError("FAIL: " + message);
    }

    private static void eq(Object expected, Object actual, String message) {
        checks++;
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("FAIL: " + message + " (expected " + expected + ", got " + actual + ")");
        }
    }
}
