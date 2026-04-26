/*
    Copyright (c) AtomicPickle 2026
    Inferno Advanced - Custom Drawing Utilities
    License: GPL-3.0 (same as original Drawm)
*/

package infernoadvanced.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.Shaders;

import static arc.graphics.g2d.Lines.*;
import static mindustry.Vars.*;
import static mindustry.content.Fx.rand;

/**
 * 🔥 IADraw - Уникальный рисовщик для Inferno Advanced
 * Все цвета в формате HEX (#RRGGBB) - БЕЗ Pal.* для стабильности меню
 */
public class IADraw {
    private static final Vec2 vec1 = new Vec2(), vec2 = new Vec2(), vec3 = new Vec2(), vec4 = new Vec2();

    // ━━━ ЦВЕТА (только HEX!) ━━━
    public static final Color
            VOID_CORE = Color.valueOf("#000000"),    // Абсолютно чёрный
            VOID_GLOW = Color.valueOf("#ff00ff"),     // Фиолетовое свечение
            VOID_AURA = Color.valueOf("#4a004a"),     // Тёмная аура
            PLASMA_HOT = Color.valueOf("#ffd37f"),    // Золотая плазма
            PLASMA_BRIGHT = Color.valueOf("#ffffff"), // Яркое ядро
            RADIOACTIVE = Color.valueOf("#a8e6cf"),   // Радиоактивный зелёный
            SHOCK_YELLOW = Color.valueOf("#ffff00"),  // Электрический жёлтый
            HOLE_CORE = Color.valueOf("#000000"),
            HOLE_INNER = Color.valueOf("#4a004a"),
            HOLE_MID = Color.valueOf("#aa44ff"),
            HOLE_OUTER = Color.valueOf("#ff66cc"),
            HOLE_ACC_RED = Color.valueOf("#ff3366"),
            HOLE_ACC_BLUE = Color.valueOf("#33ccff");


    // ━━━ БАЗОВЫЕ МЕТОДЫ ( адаптированные) ━━━

    public static void construct(Building t, TextureRegion region, float rotation, float progress, float speed, float time, Color color){
        Shaders.build.region = region;
        Shaders.build.progress = progress;
        Shaders.build.color.set(color);
        Shaders.build.color.a = speed;
        Shaders.build.time = -time / 20f;

        Draw.shader(Shaders.build);
        Draw.rect(region, t.x, t.y, rotation);
        Draw.shader();

        Draw.color(color);
        Draw.alpha(speed);
        Lines.lineAngleCenter(t.x + Mathf.sin(time, 20f, Vars.tilesize / 2f * t.block.size - 2f), t.y, 90, t.block.size * Vars.tilesize - 4f);
        Draw.reset();
    }

    public static void shaderRect(float x, float y, float z, TextureRegion region, float r, Shader shader){
        Draw.draw(z, () -> {
            Draw.shader(shader);
            Draw.rect(region, x, y, r);
            Draw.shader();
            Draw.reset();
        });
    }

    public static void spark(float x, float y, float size, float width, float r){
        Drawf.tri(x, y, width, size, r);
        Drawf.tri(x, y, width, size, r+180f);
        Drawf.tri(x, y, width, size, r+90f);
        Drawf.tri(x, y, width, size, r+270f);
    }

    public static void ellipse(float x, float y, float rad, float wScl, float hScl, float rot){
        float sides = Lines.circleVertices(rad);
        float space = 360 / sides;
        float r1 = rad - getStroke() / 2f, r2 = rad + getStroke() / 2f;

        for(int i = 0; i < sides; i++){
            float a = space * i;
            vec1.trns(rot, r1 * wScl * Mathf.cosDeg(a), r1 * hScl * Mathf.sinDeg(a));
            vec2.trns(rot, r1 * wScl * Mathf.cosDeg(a + space), r1 * hScl * Mathf.sinDeg(a + space));
            vec3.trns(rot, r2 * wScl * Mathf.cosDeg(a + space), r2 * hScl * Mathf.sinDeg(a + space));
            vec4.trns(rot, r2 * wScl * Mathf.cosDeg(a), r2 * hScl * Mathf.sinDeg(a));
            Fill.quad(x + vec1.x, y + vec1.y, x + vec2.x, y + vec2.y, x + vec3.x, y + vec3.y, x + vec4.x, y + vec4.y);
        }
    }

    public static void portal(float x, float y, float radius, Color color1, Color color2, float sizeScl, int branches, int dust, float offset){
        Draw.z(Layer.groundUnit - 1f);
        Draw.color(VOID_CORE);
        Fill.circle(x, y, radius);
        Draw.z(Layer.effect);

        int n = branches;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f + offset, radius - 2f * sizeScl).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 12f * sizeScl), 120f * sizeScl, i * 360f / n - Time.globalTime / 2f + 100f + offset);
        }

        n = branches / 3 + 3;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f + offset, radius - 4f * sizeScl).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 16f * sizeScl), 160f * sizeScl, i * 360f / n - Time.globalTime / 3f + 110f + offset);
        }

        n = 4;
        for(int i = 0; i < n; i++){
            Lines.stroke(Math.min(radius, 8.2f * sizeScl));
            Draw.alpha(1f - ((float)i) / n);
            Lines.circle(x, y, Math.max(1f, radius - i * 8f * sizeScl));
        }

        Fill.light(x, y, Lines.circleVertices(radius), radius, Color.clear, color1);

        n = dust;
        float m = 11f * sizeScl;
        Draw.alpha(0.7f);
        for(int i = 0; i < n; i++){
            float s = Mathf.randomSeed(n + 17 * i) * m;
            float speed = (Mathf.randomSeed(i * n) + 0.5f) * (m + 1f - s) * 0.1f;
            Draw.color(color1, color2, Mathf.randomSeed(i - 3 * n));
            Tmp.v1.trns(i * 360f / n - Time.globalTime * speed + Mathf.randomSeedRange(n + i, 180f / n) + offset, radius + 14f * sizeScl - s * 1.5f + Mathf.randomSeedRange(n - i, 3f)).add(x, y);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, Math.min(radius, s));
        }
        Draw.color();
    }

    public static void lightningOrb(float x, float y, float radius, Color color1, Color color2){
        Draw.z(Layer.effect - 0.001f);
        Draw.color(color1);
        Fill.circle(x, y, radius);

        int n = 3;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f, radius - 5f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 4f, i * 360f / n - Time.globalTime / 3f + 110f);
        }

        n = 4;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f, radius - 3f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 5f, i * 360f / n - Time.globalTime / 2f + 100f);
        }

        Draw.z(Layer.effect + 0.002f);
        Draw.color();
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f);
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f + 180f);
        Fill.circle(x, y, radius * 0.8f);

        Draw.blend(Blending.additive);
        Lines.stroke(Math.min(1.5f, radius));
        Draw.color(color1);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 1.8f, Mathf.random(360f));
        Lines.stroke(Math.min(1f, radius));
        Draw.color(color2);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 2.2f, Mathf.random(360f));
        Draw.color();
        Draw.blend();

        if(renderer.lights.enabled()) Drawf.light(x, y, radius * 9f, color2, 1f);
    }

    public static TextureRegion animSprites(TextureRegion a, TextureRegion b, float f, String name){
        PixmapRegion r1 = Core.atlas.getPixmap(a);
        PixmapRegion r2 = Core.atlas.getPixmap(b);

        Pixmap out = new Pixmap(r1.width, r1.height);
        Color color1 = new Color();
        Color color2 = new Color();

        for(int x = 0; x < r1.width; x++){
            for(int y = 0; y < r1.height; y++){
                out.setRaw(x, y, color1.set(r1.getRaw(x, y)).lerp(color2.set(r2.getRaw(x, y)), f).rgba());
            }
        }

        Texture texture  = new Texture(out);
        return Core.atlas.addRegion(name + "-blended-" + (int)(f * 100), new TextureRegion(texture));
    }

    // ━━━ 🔥 НОВЫЕ МЕТОДЫ ДЛЯ INFERNO ADVANCED ━━━

    /** 🔥 Пульсирующее свечение нагрева */
    public static void heatGlow(float x, float y, float radius, Color glowColor, float warmup){
        Draw.z(Layer.effect + 0.01f);
        Draw.blend(Blending.additive);
        float pulse = Mathf.absin(Time.time, 8f, 0.2f);
        float alpha = warmup * (0.4f + pulse);
        Fill.light(x, y, Lines.circleVertices(radius), radius * 0.6f, glowColor.cpy().a(alpha * 0.8f), glowColor.cpy().a(alpha));
        Draw.color(glowColor, alpha * 0.6f);
        Lines.stroke(2f * warmup);
        Lines.circle(x, y, radius * (0.9f + pulse * 0.1f));
        Draw.blend();
        Draw.reset();
    }

    /** ☢️ Радиоактивные частицы */
    public static void radioactivePulse(float x, float y, float radius, Color particleColor, float intensity){
        Draw.z(Layer.effect);
        Draw.color(particleColor);
        int particles = 8 + (int)(intensity * 12);
        float time = Time.time * 0.5f;
        for(int i = 0; i < particles; i++){
            float angle = i * 360f / particles + time * (i % 2 == 0 ? 1f : -1f);
            float dist = radius * (0.3f + Mathf.absin(time + i * 17f, 3f, 0.7f));
            float size = 1f + Mathf.absin(time * 2f + i * 13f, 2f, 1.5f) * intensity;
            Tmp.v1.trns(angle, dist).add(x, y);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, size);
        }
        Draw.color(particleColor, intensity * 0.7f);
        Fill.circle(x, y, radius * 0.3f * intensity);
        Draw.reset();
    }

    /** ⚡ Плазменный трейл */
    public static void plasmaTrail(float x, float y, float angle, float length, float width, Color trailColor, float warmup){
        Draw.z(Layer.bullet - 0.01f);
        Color start = trailColor.cpy().a(0.8f * warmup);
        Color end = trailColor.cpy().a(0.2f * warmup);
        int segments = 8;
        for(int i = 0; i < segments; i++){
            float t = (float)i / segments;
            float segX = x - Mathf.cosDeg(angle) * length * t;
            float segY = y - Mathf.sinDeg(angle) * length * t;
            float segWidth = width * (1f - t * 0.7f);
            Draw.color(start.lerp(end, t));
            Fill.circle(segX, segY, segWidth);
        }
        if(renderer.lights.enabled()){
            Drawf.light(x, y, x - Mathf.cosDeg(angle) * length, y - Mathf.sinDeg(angle) * length, width * 3f, trailColor, 0.5f * warmup);
        }
        Draw.reset();
    }

    /** ⚡ Зигзагообразная молния */
    public static void zigzagLightning(float x1, float y1, float x2, float y2, Color color, float width, float segments){
        Draw.z(Layer.effect + 0.001f);
        Draw.color(color);
        Lines.stroke(width);
        float dx = x2 - x1, dy = y2 - y1;
        float dist = Mathf.len(dx, dy);
        float step = dist / segments;
        float px = x1, py = y1;
        Lines.beginLine();
        Lines.linePoint(px, py);
        for(int i = 1; i < segments; i++){
            float t = (float)i / segments;
            float nx = x1 + dx * t, ny = y1 + dy * t;
            float offset = Mathf.randomSeedRange(i, width * 3f);
            float perpX = -dy / dist * offset, perpY = dx / dist * offset;
            px = nx + perpX; py = ny + perpY;
            Lines.linePoint(px, py);
        }
        Lines.linePoint(x2, y2);
        Lines.endLine();
        Draw.color(Color.white, color, 0.5f);
        for(int i = 0; i < segments / 2; i++){
            float t = Mathf.random();
            float ix = x1 + dx * t + Mathf.randomSeedRange(i + 100, width);
            float iy = y1 + dy * t + Mathf.randomSeedRange(i + 200, width);
            Fill.circle(ix, iy, width * 0.5f);
        }
        Draw.reset();
    }

    /** 🎯 Визуальный прицел/радиус */
    public static void drawRange(float x, float y, float radius, Color rangeColor, float alpha){
        Draw.z(Layer.effect - 0.1f);
        Draw.color(rangeColor, alpha * 0.3f);
        Fill.circle(x, y, radius);
        Draw.color(rangeColor, alpha);
        Lines.stroke(1.5f);
        Lines.circle(x, y, radius);
        Lines.stroke(1f);
        Lines.line(x - radius * 0.1f, y, x + radius * 0.1f, y);
        Lines.line(x, y - radius * 0.1f, x, y + radius * 0.1f);
        Draw.reset();
    }

    /** ✨ Эффект "искры" при попадании */
    public static void impactSpark(float x, float y, float size, Color sparkColor, float rotation){
        Draw.z(Layer.effect + 0.002f);
        Draw.color(sparkColor);
        for(int i = 0; i < 4; i++){
            float angle = rotation + i * 90f;
            Drawf.tri(x, y, size * 0.3f, size, angle);
        }
        Draw.color(Color.white, sparkColor, 0.7f);
        for(int i = 0; i < 8; i++){
            float angle = rotation + i * 45f + Mathf.random(10f);
            float len = size * Mathf.random(0.5f, 1f);
            Tmp.v1.trns(angle, len).add(x, y);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, size * 0.15f);
        }
        Draw.reset();
    }

    // ━━━ 🕳️ ЧЁРНАЯ ДЫРА - УНИКАЛЬНЫЕ МЕТОДЫ ━━━

    /** 🕳️ Ядро чёрной дыры с вращающимися кольцами */
    public static void drawBlackHole(float x, float y, float radius, float rotation, float alpha){
        Draw.z(Layer.effect + 0.1f);

        // Ядро (абсолютно чёрное)
        Draw.color(HOLE_CORE, alpha);
        Fill.circle(x, y, radius * 0.3f);

        // Внутреннее кольцо (фиолетовое)
        Draw.color(HOLE_INNER, alpha * 0.8f);
        Lines.stroke(2f * alpha);
        Lines.circle(x, y, radius * 0.6f);

        // Среднее кольцо (фиолетово-розовое)
        Draw.color(HOLE_MID, alpha * 0.6f);
        Lines.stroke(1.5f * alpha);
        Lines.circle(x, y, radius * 0.85f);

        // Внешнее кольцо (розовое, вращается)
        Draw.color(HOLE_OUTER, alpha * 0.4f);
        Lines.stroke(1.2f * alpha);
        Lines.poly(x, y, 12, radius * 1.1f, rotation);

        // Аккреционный диск (частицы)
        Draw.color(HOLE_ACC_RED, HOLE_ACC_BLUE, alpha);
        for (int i = 0; i < 12; i++) {
            float angle = rotation + i * 30f + Time.time * 8f;
            float dist = radius * (0.8f + Mathf.sin(angle * 2, 0.2f));
            float size = 2f + Mathf.sin(Time.time * 5f + i, 1.5f);
            Tmp.v1.trns(angle, dist);
            Fill.circle(x + Tmp.v1.x, y + Tmp.v1.y, size * alpha);
        }

        Draw.reset();
    }

    /** 🌌 Горизонт событий (пульсирующая аура) */
    public static void drawEventHorizon(float x, float y, float radius, float time){
        Draw.z(Layer.effect);
        float pulse = Mathf.absin(time, 10f, 0.3f) + 0.7f;
        Draw.color(VOID_CORE, VOID_AURA, pulse);
        Fill.circle(x, y, radius * pulse);
        if(renderer.lights.enabled()){
            Drawf.light(x, y, radius * 2f, Color.valueOf("#550055"), pulse * 0.5f);
        }
        Draw.reset();
    }

    /** ✨ Частицы при схлопывании (для despawnEffect) */
    public static final Effect blackHoleCollapse = new Effect(40f, e -> {
        Draw.z(Layer.effect + 0.2f);
        Color core = VOID_CORE;
        Color glow = VOID_GLOW;

        for(int i = 0; i < 8; i++){
            float angle = e.rotation + i * 45f + e.fin() * 180f;
            float len = e.fin() * 60f;
            float size = (1f - e.fin()) * 4f;
            Tmp.v1.trns(angle, len);
            Draw.color(core);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, size * 0.5f);
            Draw.color(glow, e.fout());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, size);
        }

        if(e.fin() > 0.9f){
            Draw.color(Color.white, glow, e.fout() * 2f);
            Fill.circle(e.x, e.y, e.fout() * 30f);
        }
        Draw.reset();
    });

    /** 🌀 Искажение пространства (кольца для шейдера) */
    public static final Effect blackHoleDistortion = new Effect(60f, e -> {
        Color baseColor = e.color != null ? e.color : Color.valueOf("#6a0dad");
        float fout = e.fout();
        float fin = e.fin();

        // Кольца искажения
        Draw.color(baseColor, fout * 0.4f);
        Lines.stroke(2f * fout);
        for(int i = 0; i < 5; i++){
            float radius = 20f * fin + i * 15f;
            Lines.circle(e.x, e.y, radius);
        }

        // Частицы аккреционного диска
        Draw.color(Color.valueOf("#9d4edd"), fout * 0.6f);
        Rand rand = new Rand(e.id);
        for(int i = 0; i < 12; i++){
            float angle = rand.random(360f);
            float dist = rand.random(30f, 80f) * fin;
            float size = rand.random(2f, 5f) * fout;
            float px = e.x + Angles.trnsx(angle, dist);
            float py = e.y + Angles.trnsy(angle, dist);
            Fill.circle(px, py, size);
        }

        if(renderer.lights.enabled()){
            Drawf.light(e.x, e.y, 100f * fout, baseColor, 0.7f);
        }
        Draw.reset();
    });

    // ━━━ 🚀 ГОТОВЫЕ ЭФФЕКТЫ ДЛЯ СНАРЯДОВ ━━━

    /** 🔥 Плазменный трейл для энергетических снарядов */
    public static final Effect plasmaTrailEffect = new Effect(40f, 80f, e -> {
        float width = 4f * e.fout();
        float length = 30f * e.fin();
        Draw.color(e.color);
        Draw.alpha(e.fout() * 0.7f);
        for(int i = 0; i < 3; i++){
            float angle = e.rotation + 180f + Mathf.randomSeedRange(e.id + i, 15f);
            Tmp.v1.trns(angle, length * (0.5f + i * 0.25f));
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, width * (1f - i * 0.2f));
        }
        Drawf.light(e.x, e.y, 30f, e.color, e.fout());
    }).layer(Layer.bullet - 0.01f);

    /** ⚡ Фазовый трейл (золотой) */
    public static final Effect phaseTrailEffect = new Effect(50f, 100f, e -> {
        Color c1 = PLASMA_HOT, c2 = PLASMA_BRIGHT;
        float fout = e.fout(), fin = e.fin();
        Draw.color(c1, c2, fout);
        Fill.circle(e.x, e.y, 3f * fout + 1f);
        Draw.color(c1, fout * 0.6f);
        rand.setSeed(e.id);
        for(int i = 0; i < 4; i++){
            float angle = e.rotation + 180f + rand.range(20f);
            float len = 15f * fin + rand.random(5f);
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 1.5f * fout + 0.5f);
        }
        Drawf.light(e.x, e.y, 25f, c1, fout);
    }).layer(Layer.bullet - 0.01f);

    /** ☢️ Радиоактивный трейл (для стронция/бария) */
    public static final Effect radioactiveTrailEffect = new Effect(45f, 90f, e -> {
        Color c1 = RADIOACTIVE, c2 = Color.valueOf("#4ecdc4");
        float fout = e.fout();
        Draw.color(c1, c2, fout);
        Fill.circle(e.x, e.y, 2.5f * fout + 1f);
        Draw.color(c1, fout * 0.5f);
        rand.setSeed(e.id);
        for(int i = 0; i < 3; i++){
            float angle = rand.random(360f);
            float len = 10f * fout + rand.random(5f);
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 1f * fout + 0.3f);
        }
    }).layer(Layer.bullet - 0.01f);

    /** 💥 Взрыв фазовой ракеты */
    public static final Effect phaseExplosionEffect = new Effect(50f, 150f, e -> {
        Color c1 = PLASMA_HOT, c2 = PLASMA_BRIGHT;
        float fout = e.fout(), fin = e.fin();
        Draw.color(c2, c1, fout);
        Fill.circle(e.x, e.y, 20f * fout + 10f);
        Draw.color(c1, fout * 0.7f);
        Lines.stroke(4f * fout);
        Lines.circle(e.x, e.y, 30f * fin + 15f);
        Draw.color(c2, fout * 0.5f);
        rand.setSeed(e.id);
        for(int i = 0; i < 12; i++){
            float angle = rand.random(360f);
            float len = 40f * fin + rand.random(20f);
            float size = 2f * fout + 0.5f;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, size);
        }
        Drawf.light(e.x, e.y, 80f, c1, fout);
    });

    /** 💥 Взрыв стронция (красный, с дымом) */
    public static final Effect strontiumExplosionEffect = new Effect(60f, 150f, e -> {
        Color c1 = Color.valueOf("#ff6644"), c2 = Color.valueOf("#ff3300");
        float fout = e.fout(), fin = e.fin();
        Draw.color(c2, c1, fout);
        Fill.circle(e.x, e.y, 25f * fout + 8f);
        Draw.color(Color.gray, c1, fout * 0.4f);
        for(int i = 0; i < 5; i++){
            float angle = e.rotation + i * 72f + Mathf.randomSeedRange(e.id + i, 30f);
            float len = 30f * fin + Mathf.randomSeedRange(e.id + i + 5, 20f);
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 5f * fout + 1f);
        }
        Draw.color(c1, fout * 0.6f);
        Lines.stroke(3f * fout);
        Lines.circle(e.x, e.y, 35f * fin + 10f);
    });

    /** ⚡ Молния при ударе (шок-боеприпасы) */
    public static final Effect shockHitEffect = new Effect(25f, 80f, e -> {
        Color c1 = SHOCK_YELLOW, c2 = Color.white;
        float fout = e.fout();
        Draw.color(c1, c2, fout);
        for(int i = 0; i < 6; i++){
            float angle = i * 60f + e.rotation;
            Drawf.tri(e.x, e.y, 2f * fout + 1f, 20f * fout + 5f, angle);
        }
        Drawf.light(e.x, e.y, 40f, c1, fout);
    });

    /** 💫 Импульс при запуске (турели) */
    public static final Effect launchPulseEffect = new Effect(30f, 100f, e -> {
        Color c = e.color;
        float fout = e.fout(), fin = e.fin();
        Draw.color(c, fout);
        Lines.stroke(3f * fout);
        Lines.circle(e.x, e.y, 20f * fin + 5f);
        Draw.color(Color.white, c, fin);
        for(int i = 0; i < 4; i++){
            float angle = i * 90f + e.rotation;
            Drawf.tri(e.x, e.y, 2f, 15f * fin, angle);
        }
    });

    /** 🌌 Орбитальные частицы вокруг турели */
    public static void drawOrbitParticles(float x, float y, float radius, float rotation, Color color, float alpha){
        Draw.z(Layer.effect + 0.05f);
        Draw.color(color, alpha);

        for(int i = 0; i < 3; i++){
            float angle = rotation + i * 120f;
            Tmp.v1.trns(angle, radius);
            Fill.circle(x + Tmp.v1.x, y + Tmp.v1.y, 3f * alpha);
        }

        Draw.reset();
    }

    /** ⚡ Визуализация цепной молнии */
    public static Effect chainLightning = new Effect(40f, e -> {
        Draw.color(Color.valueOf("#c77dff"), e.fout());
        Lines.stroke(2f * e.fout());

        // Зигзагообразная линия
        float segments = 8;
        float px = e.x, py = e.y;
        Lines.beginLine();
        Lines.linePoint(px, py);

        for(int i = 1; i < segments; i++){
            float t = (float)i / segments;
            float nx = e.x + (e.data instanceof Vec2 ? ((Vec2)e.data).x * t : 50f * t);
            float ny = e.y + (e.data instanceof Vec2 ? ((Vec2)e.data).y * t : 50f * t);
            float offset = Mathf.randomSeedRange(i, 8f);
            px = nx + offset;
            py = ny + offset;
            Lines.linePoint(px, py);
        }

        Lines.endLine();
        Draw.reset();
    });
    // ━━━ 🎯 НОВЫЕ ЭФФЕКТЫ ДЛЯ РЕЙЛГАНА (Void Annihilator) ━━━

    /** ⚡ Выстрел рейлгана (длинная линия с искрами) */
    public static final Effect railShootEffect = new Effect(35f, e -> {
        Color base = Color.valueOf("#9d4edd");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();
        float len = e.fin() * 60f;

        // Основная линия
        Draw.color(bright, fout);
        Lines.stroke(4f * fout);
        Lines.lineAngle(e.x, e.y, e.rotation, len);

        // Боковые искры
        Draw.color(base, fout * 0.7f);
        for(int i = 0; i < 5; i++){
            float angle = e.rotation + Mathf.randomSeedRange(e.id + i, 25f);
            float offset = Mathf.randomSeed(e.id + i + 1, len * 0.8f);
            Tmp.v1.trns(e.rotation, offset);
            float ix = e.x + Tmp.v1.x;
            float iy = e.y + Tmp.v1.y;
            float sparkLen = Mathf.random(10f, 25f) * fout;
            Drawf.tri(ix, iy, 2f * fout, sparkLen, angle);
        }

        // Точки вдоль линии
        Draw.color(Color.white, fout);
        for(int i = 0; i < 8; i++){
            float t = (float)i / 8f;
            Tmp.v1.trns(e.rotation, len * t);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * fout * (1f - t));
        }

        Draw.reset();
    }).layer(Layer.effect + 0.1f);

    /** 💥 Попадание рейлгана (взрыв + молнии) */
    public static final Effect railHitEffect = new Effect(45f, e -> {
        Color base = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#c77dff");
        Color white = Color.valueOf("#ffffff");

        float fout = e.fout();
        float fin = e.fin();

        // Центральная вспышка
        Draw.color(white, bright, fout);
        Fill.circle(e.x, e.y, 25f * fout + 10f);

        // Ударная волна
        Draw.color(bright, fout * 0.8f);
        Lines.stroke(5f * fout);
        Lines.circle(e.x, e.y, 35f * fin + 15f);

        // Молнии
        Draw.color(bright, fout);
        for(int i = 0; i < 6; i++){
            float angle = Mathf.randomSeed(e.id + i, 360f);
            float len = Mathf.randomSeed(e.id + i + 1, 40f, 70f) * fin;
            Tmp.v1.trns(angle, len);
            Lines.line(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3f * fout);
        }

        // Осколки
        Draw.color(base, fout * 0.6f);
        for(int i = 0; i < 12; i++){
            float angle = i * 30f + e.rotation;
            float len = 50f * fin + Mathf.randomSeed(e.id + i, 0f, 20f);
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2.5f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.2f);

    /** 🌌 Исчезновение снаряда (портал + воронка) */
    public static final Effect railDespawnEffect = new Effect(50f, e -> {
        Color dark = Color.valueOf("#0a0a0a");
        Color purple = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#9d4edd");

        float fout = e.fout();
        float fin = e.fin();

        // Чёрное ядро
        Draw.color(dark, fout);
        Fill.circle(e.x, e.y, 20f * fout + 8f);

        // Вращающиеся кольца
        Draw.color(purple, fout * 0.7f);
        Lines.stroke(3f * fout);
        for(int i = 0; i < 3; i++){
            float rot = e.rotation + i * 60f + e.time * 2f;
            Lines.poly(e.x, e.y, 6, 30f * fin + 20f + i * 10f, rot);
        }

        // Внешнее свечение
        Draw.color(bright, fout * 0.5f);
        Lines.stroke(2f * fout);
        Lines.circle(e.x, e.y, 50f * fin + 30f);

        // Частицы воронки
        Draw.color(bright, fout * 0.6f);
        for(int i = 0; i < 16; i++){
            float angle = i * 22.5f + e.time * 3f;
            float dist = 40f * fin + Mathf.randomSeed(e.id + i, 10f, 30f);
            Tmp.v1.trns(angle, dist);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.15f);

    /** ✨ Трейл рейлгана (фиолетовая линия с частицами) */
    public static final Effect railTrailEffect = new Effect(25f, e -> {
        Color base = Color.valueOf("#9d4edd");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();

        // Основная линия
        Draw.color(bright, fout);
        Lines.stroke(3f * fout);
        Lines.lineAngle(e.x, e.y, e.rotation, 20f * fout);

        // Частицы
        Draw.color(base, fout * 0.8f);
        for(int i = 0; i < 4; i++){
            float angle = e.rotation + Mathf.randomSeedRange(e.id + i, 15f);
            float len = Mathf.randomSeed(e.id + i + 1, 10f, 25f) * fout;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * fout);
        }

        Draw.reset();
    }).layer(Layer.bullet - 0.01f);

    /** 🌀 Зарядка перед выстрелом (накапливающаяся аура) */
    public static final Effect railChargeEffect = new Effect(60f, e -> {
        Color dark = Color.valueOf("#4a004a");
        Color purple = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();
        float fin = e.fin();
        float pulse = Mathf.absin(e.time, 8f, 0.3f) + 0.7f;

        // Внутреннее свечение
        Draw.color(dark, purple, pulse * fout);
        Fill.circle(e.x, e.y, 35f * fin * pulse);

        // Вращающиеся сегменты
        Draw.color(purple, fout * 0.6f);
        Lines.stroke(4f * fout);
        for(int i = 0; i < 6; i++){
            float angle = i * 60f + e.time * 2f;
            float len = 40f * fin + Mathf.absin(i, 0.2f) * 10f;
            Tmp.v1.trns(angle, len);
            Lines.line(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y);
        }

        // Внешние кольца
        Draw.color(bright, fout * 0.4f);
        Lines.stroke(2f * fout);
        Lines.circle(e.x, e.y, 50f * fin + 20f);
        Lines.circle(e.x, e.y, 60f * fin + 25f);

        // Искры по краям
        Draw.color(Color.white, fout * 0.5f);
        for(int i = 0; i < 8; i++){
            float angle = Mathf.randomSeed(e.id + i, 360f);
            float dist = 55f * fin + 20f;
            Tmp.v1.trns(angle, dist);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect);

    // ━━━ 🎯 ЭФФЕКТЫ ДЛЯ РЕЙЛГАНА (VOID ANNIHILATOR) ━━━

    // ━━━ 🎯 ЭФФЕКТЫ ДЛЯ VOID ANNIHILATOR ━━━

    /** ⚡ Вспышка выстрела (как у Foreshadow + фиолетовые частицы) */
// ━━━ 🎯 ЭФФЕКТЫ ДЛЯ VOID ANNIHILATOR (Украшения для RailBulletType) ━━━

    /** ⚡ Вспышка из дула (когда вылетает луч) */
    public static final Effect railMuzzle = new Effect(20f, e -> {
        Color base = Color.valueOf("#9d4edd");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();

        // Центральная вспышка
        Draw.color(Color.white, bright, fout);
        Fill.circle(e.x, e.y, 8f * fout);

        // Искры по сторонам
        Draw.color(base, fout * 0.8f);
        for(int i = 0; i < 6; i++){
            float angle = i * 60f + e.rotation;
            float len = 15f * fout;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.1f);

    /** 💥 Взрыв при попадании (луч исчезает) */
    public static final Effect railImpact = new Effect(35f, e -> {
        Color base = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();
        float fin = e.fin();

        // Ударная волна
        Draw.color(bright, fout * 0.7f);
        Lines.stroke(4f * fout);
        Lines.circle(e.x, e.y, 25f * fin + 10f);

        // Молнии
        Draw.color(Color.white, bright, fout);
        for(int i = 0; i < 8; i++){
            float angle = Mathf.randomSeed(e.id + i, 360f);
            float len = Mathf.randomSeed(e.id + i + 1, 20f, 40f) * fin;
            Tmp.v1.trns(angle, len);
            Lines.line(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y);
        }

        // Частицы
        Draw.color(base, fout * 0.6f);
        for(int i = 0; i < 12; i++){
            float angle = i * 30f + e.rotation;
            float len = 35f * fin;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2.5f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.2f);

    /** ✨ Искры вдоль луча (RailBulletType сам рисует луч, это просто украшение) */
    public static final Effect railBeamSpark = new Effect(15f, e -> {
        Color base = Color.valueOf("#9d4edd");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();

        // Светящиеся точки
        Draw.color(bright, fout);
        Fill.circle(e.x, e.y, 3f * fout);

        // Боковые искры
        Draw.color(base, fout * 0.7f);
        for(int i = 0; i < 4; i++){
            float angle = e.rotation + Mathf.randomSeedRange(e.id + i, 20f);
            float len = Mathf.randomSeed(e.id + i + 1, 5f, 12f) * fout;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 1.5f * fout);
        }

        Draw.reset();
    }).layer(Layer.bullet + 0.1f);

    // ━━━ 🟣 ФИОЛЕТОВЫЕ ЭФФЕКТЫ ДЛЯ RAILGUN ━━━

    /** ⚡ Выстрел рейлгана (ФИОЛЕТОВЫЙ, не жёлтый!) */
    public static final Effect railShootPurple = new Effect(25f, e -> {
        Color base = Color.valueOf("#9d4edd");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();

        // Центральная вспышка
        Draw.color(Color.white, bright, fout);
        Fill.circle(e.x, e.y, 10f * fout);

        // Искры по сторонам
        Draw.color(base, fout * 0.8f);
        for(int i = 0; i < 8; i++){
            float angle = i * 45f + e.rotation;
            float len = 20f * fout;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2.5f * fout);
        }

        // Ударная волна
        Draw.color(bright, fout * 0.5f);
        Lines.stroke(3f * fout);
        Lines.circle(e.x, e.y, 25f * e.fin());

        Draw.reset();
    }).layer(Layer.effect + 0.1f);

    /** 💥 Попадание рейлгана (ФИОЛЕТОВОЕ) */
    public static final Effect railHitPurple = new Effect(35f, e -> {
        Color base = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();
        float fin = e.fin();

        // Вспышка
        Draw.color(Color.white, bright, fout);
        Fill.circle(e.x, e.y, 15f * fout + 8f);

        // Ударная волна
        Draw.color(bright, fout * 0.7f);
        Lines.stroke(4f * fout);
        Lines.circle(e.x, e.y, 30f * fin + 15f);

        // Молнии
        Draw.color(bright, fout);
        for(int i = 0; i < 6; i++){
            float angle = Mathf.randomSeed(e.id + i, 360f);
            float len = Mathf.randomSeed(e.id + i + 1, 25f, 50f) * fin;
            Tmp.v1.trns(angle, len);
            Lines.line(e.x, e.y, e.x + Tmp.v1.x, e.y + Tmp.v1.y);
        }

        // Частицы
        Draw.color(base, fout * 0.6f);
        for(int i = 0; i < 12; i++){
            float angle = i * 30f + e.rotation;
            float len = 40f * fin;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.2f);

    /** 🌌 Исчезновение (ФИОЛЕТОВОЕ) */
    public static final Effect railDespawnPurple = new Effect(40f, e -> {
        Color base = Color.valueOf("#6a0dad");
        Color bright = Color.valueOf("#c77dff");

        float fout = e.fout();
        float fin = e.fin();

        // Вспышка
        Draw.color(Color.white, bright, fout);
        Fill.circle(e.x, e.y, 20f * fout + 10f);

        // Кольца
        Draw.color(bright, fout * 0.5f);
        Lines.stroke(3f * fout);
        Lines.circle(e.x, e.y, 35f * fin + 20f);
        Lines.circle(e.x, e.y, 45f * fin + 25f);

        // Частицы
        Draw.color(base, fout * 0.6f);
        for(int i = 0; i < 16; i++){
            float angle = i * 22.5f + e.rotation;
            float len = 50f * fin;
            Tmp.v1.trns(angle, len);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2.5f * fout);
        }

        Draw.reset();
    }).layer(Layer.effect + 0.15f);

    /** 🌌 Аккреционный диск (как в Extra Utilities) */
    public static void accretionDisk(float x, float y, float radius, float rotation, float alpha, Color color1, Color color2) {
        Draw.z(Layer.effect + 0.05f);

        for (int i = 0; i < 24; i++) {
            float angle = rotation + i * 15f;
            float dist = radius * (0.65f + Mathf.sin(angle * 3, 0.2f));
            float size = 2f + Mathf.sin(rotation * 3 + i, 1.2f);

            Draw.color(color1, color2, alpha * (0.5f + Mathf.sin(angle * 2, 0.3f)));
            Tmp.v1.trns(angle, dist);
            Fill.circle(x + Tmp.v1.x, y + Tmp.v1.y, size * alpha);
        }

        Draw.reset();
    }

}