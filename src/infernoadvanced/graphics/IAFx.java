package infernoadvanced.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.Effect;
import mindustry.graphics.*;

public class IAFx {

    // ========== ЭФФЕКТ ЗАРЯДКИ ДЛЯ ПУШКИ ==========
    public static Effect chargeSingularity = new Effect(60f, 120f, e -> {
        float progress = e.data instanceof Float ? (Float)e.data : 1f;
        float rot = e.rotation;

        // Вращающиеся кольца энергии
        for (int i = 0; i < 4; i++) {
            float angle = rot + i * 90f + e.time * 15f;
            float rad = 28f * progress * (0.8f + Mathf.sin(e.time * 12f, 4f));

            Draw.color(Color.valueOf("#ff6644"), Color.valueOf("#ff3300"), e.fin());
            Lines.stroke(3f * progress * e.fout());
            Lines.arc(e.x, e.y, rad, 0.2f, angle);

            Draw.color(Color.valueOf("#ffaa66"), e.fin() * 0.6f);
            Lines.stroke(2f * progress * e.fout());
            Lines.arc(e.x, e.y, rad + 6f, 0.2f, angle + 30f);
        }

        // Пульсирующее ядро
        Draw.blend(Blending.additive);
        Draw.color(e.color, progress * 0.8f);
        Fill.circle(e.x, e.y, 22f * progress * (0.7f + Mathf.absin(e.time, 8f, 0.2f)));

        Draw.color(Color.black, progress * 0.5f);
        Fill.circle(e.x, e.y, 14f * progress);
        Draw.blend();

        // Искры
        for (int i = 0; i < 6; i++) {
            float angle = e.time * 20f + i * 60f;
            float rad = 32f * progress;
            Tmp.v1.trns(angle, rad);

            Draw.color(Color.valueOf("#ff8844"), e.fout());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3f * progress * e.fout());
        }
    }).layer(Layer.effect);

    // ========== ЭФФЕКТ ВЫСТРЕЛА (ударная волна) ==========
    public static Effect shootSingularity = new Effect(45f, e -> {
        // Ударная волна
        Draw.color(Color.valueOf("#ff5533"), Color.valueOf("#ff8844"), e.fout());
        Lines.stroke(6f * e.fout());
        Lines.circle(e.x, e.y, 70f * e.finpow());

        // Разлетающиеся искры
        for (int i = 0; i < 20; i++) {
            float angle = Mathf.random(360f);
            float len = Mathf.random(50f, 100f) * e.finpow();
            Tmp.v1.trns(angle, len);
            Draw.color(Color.valueOf("#ff6644"), e.fout());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4f * e.fout());
        }

        // Гравитационная вспышка
        Draw.blend(Blending.additive);
        Draw.color(Color.white, Color.valueOf("#ff4422"), e.fin());
        Fill.circle(e.x, e.y, 28f * e.finpow());
        Draw.blend();
    }).layer(Layer.effect);

    // ========== ЭФФЕКТ ВЫСТРЕЛА (альтернативный) ==========
    public static Effect shootSingularityAlt = new Effect(40f, e -> {
        // Кольца
        for (int i = 0; i < 3; i++) {
            float rad = 35f * e.finpow() + i * 12f;
            Draw.color(Color.valueOf("#ff6644"), Color.valueOf("#ff3300"), e.fout());
            Lines.stroke(3f * e.fout());
            Lines.circle(e.x, e.y, rad);
        }

        // Частицы
        for (int i = 0; i < 12; i++) {
            float angle = e.rotation + i * 30f + e.time * 15f;
            float len = 55f * e.finpow();
            Tmp.v1.trns(angle, len);

            Draw.color(Color.valueOf("#ff8844"), e.fout());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3f * e.fout());
        }
    }).layer(Layer.effect);

    // ========== ЭФФЕКТ ПОПАДАНИЯ (для чёрной дыры) ==========
    public static Effect blackHoleHit = new Effect(35f, e -> {
        // Взрывное кольцо
        Draw.color(Color.valueOf("#ff5533"), Color.valueOf("#ff8844"), e.fout());
        Lines.stroke(4f * e.fout());
        Lines.circle(e.x, e.y, 45f * e.finpow());

        // Гравитационная рябь
        for (int i = 0; i < 8; i++) {
            float angle = e.rotation + i * 45f + e.time * 20f;
            float len = 40f * e.finpow();
            Tmp.v1.trns(angle, len);

            Draw.color(Color.valueOf("#ff6644"), e.fout() * 0.8f);
            Lines.lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, angle + 90f, 15f * e.fout());
        }

        // Частицы
        for (int i = 0; i < 12; i++) {
            float angle = Mathf.random(360f);
            float len = Mathf.random(20f, 50f) * e.finpow();
            Tmp.v1.trns(angle, len);
            Draw.color(Color.valueOf("#ff8866"), e.fout());
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * e.fout());
        }
    }).layer(Layer.effect);


    // ========== ЭФФЕКТ ИСЧЕЗНОВЕНИЯ ЧЁРНОЙ ДЫРЫ ==========
    public static Effect blackHoleCollapse = new Effect(50f, 100f, e -> {
        float radius = e.rotation;
        float life = e.fout();

        // Взрывное кольцо
        Draw.color(Color.valueOf("#ff5533"), Color.valueOf("#ff8844"), life);
        Lines.stroke(5f * life);
        Lines.circle(e.x, e.y, radius * (1f + e.fin() * 1.5f));

        // Разлетающиеся искры
        for (int i = 0; i < 24; i++) {
            float angle = Mathf.random(360f);
            float len = radius * (0.5f + Mathf.random(1f)) * e.fin();
            Tmp.v1.trns(angle, len);

            Draw.color(Color.valueOf("#ff6644"), Color.valueOf("#ffaa66"), life);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3f * life + 1f);
        }

        // Гравитационные волны
        for (int i = 0; i < 8; i++) {
            float angle = e.time * 20f + i * 45f;
            float len = radius * (0.8f + Mathf.sin(e.time * 15f, 0.3f)) * e.fin();
            Tmp.v1.trns(angle, len);

            Draw.color(Color.valueOf("#ff8844"), life * 0.8f);
            Lines.stroke(2f * life);
            Lines.lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, angle + 90f, 12f * life);
        }

        // Финальная вспышка
        Draw.blend(Blending.additive);
        Draw.color(Color.valueOf("#ff4422"), life * 0.5f);
        Fill.circle(e.x, e.y, radius * 0.8f * e.fin());
        Draw.blend();

        // Частицы пепла
        for (int i = 0; i < 16; i++) {
            float angle = Mathf.random(360f);
            float len = radius * (0.6f + Mathf.random(1f)) * e.fin();
            Tmp.v1.trns(angle, len);

            Draw.color(Color.valueOf("#442200"), life * 0.6f);
            Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * life);
        }
    }).layer(Layer.effect);
}
