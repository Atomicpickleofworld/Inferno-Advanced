package infernoadvanced.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;

public class InfernoStatusEffects {

    // ██████ КАСТОМНЫЕ СТАТУС-ЭФФЕКТЫ ██████

    /** Шокированный: электрический урон + замедление + визуальные молнии */
    public static StatusEffect shocked;

    /** Радиоактивный: периодический урон + снижение брони */
    public static StatusEffect radioactive;

    public static StatusEffect phaseSlow;

    /** Перегретый: снижение скорострельности + урон от тепла */
    public static StatusEffect overheated;

    public static StatusEffect gravityCollapse;

    public static void load() {

        // ██████ SHOCKED — Электрический шок ██████
        shocked = new StatusEffect("shocked"){{
            color = Color.valueOf("ffff00");  // Ярко-жёлтый (как сплав волны)

            // Статы
            speedMultiplier = 0.3f;        // -30% скорости
            reloadMultiplier = 0.6f;       // -40% скорострельности
            damageMultiplier = 0.7f;       // -10% урона

            // Периодический урон молнией
            intervalDamage = 50f;           // 8 урона каждые...
            intervalDamageTime = 15f;      // ...15 тиков (0.25 сек)

            // Визуал
            effect = new MultiEffect(
                    Fx.lightningShoot,
                    new Effect(20f, e -> {
                        Draw.color(Color.valueOf("ffff00"), e.fout());
                        for(int i = 0; i < 4; i++){
                            float angle = i * 90f + e.rotation;
                            Drawf.tri(e.x, e.y, e.fout() * 2f, 12f * e.fout(), angle);
                        }
                    })
            );
            effectChance = 0.15f;  // 15% шанс показать эффект каждый тик

            // Реактивный: не стакается, но обновляет время
            reactive = true;

            // Противоположные эффекты
            init(() -> {
                opposite(StatusEffects.wet);  // Вода гасит шок
                affinity(StatusEffects.tarred, (unit, result, time) -> {
                    // Смол + шок = больше урона
                    unit.damage(5f);
                    result.set(shocked, Math.min(time + result.time, 180f));
                });
            });
        }};

        // ██████ RADIOACTIVE — Радиоактивное заражение ██████
        radioactive = new StatusEffect("radioactive"){{
            color = Color.valueOf("339554");  // Зелёный стронция

            // Статы
            healthMultiplier = 0.85f;        // -15% здоровья

            // Периодический урон
            intervalDamage = 4f;
            intervalDamageTime = 20f;

            // Визуал
            effect = new Effect(40f, e -> {
                Draw.color(Color.valueOf("339554"), e.fout());
                for(int i = 0; i < 3; i++){
                    float angle = i * 120f + e.rotation;
                    Drawf.tri(e.x, e.y, e.fout() * 1.5f, 8f * e.fout(), angle);
                }
            });
            effectChance = 0.08f;

            init(() -> {
                opposite(StatusEffects.freezing);  // Холод замедляет распад
            });
        }};

        // ━━━ 🌀 ФАЗОВОЕ ЗАМЕДЛЕНИЕ (уникальный статус) ━━━
        phaseSlow = new StatusEffect("phase-slow"){{
            color = Color.valueOf("#9d4edd");
            speedMultiplier = 0.3f; // -70% скорости
            reloadMultiplier = 0.5f; // -50% скорострельности
            damageMultiplier = 0.7f; // -30% урона

            // Визуальный эффект на юните
            effect = new Effect(20f, e -> {
                Draw.color(Color.valueOf("#9d4edd"), e.fout());
                Lines.stroke(2f * e.fout());
            });

            effectChance = 0.1f;
            permanent = false;
        }};

        // ██████ OVERHEATED — Перегрев ██████
        overheated = new StatusEffect("overheated"){{
            color = Color.valueOf("ff6644");  // Оранжево-красный

            // Статы
            reloadMultiplier = 0.5f;         // -50% скорострельности
            speedMultiplier = 0.8f;          // -20% скорости

            // Урон от перегрева
            intervalDamage = 3f;
            intervalDamageTime = 30f;

            // Визуал
            effect = new Effect(30f, e -> {
                Draw.color(Color.valueOf("ff6644"), e.fout());
                Fill.circle(e.x + Mathf.range(3f), e.y + Mathf.range(3f), e.fout() * 2f);
            });
            effectChance = 0.12f;

            init(() -> {
                opposite(StatusEffects.freezing);  // Холод остужает
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    // Огонь + перегрев = взрывной урон
                    unit.damage(12f);
                    result.set(overheated, Math.min(time + result.time, 120f));
                });
            });
        }};
        // ██████ GRAVITY_COLLAPSE — Гравитационный коллапс ██████
        gravityCollapse = new StatusEffect("gravity-collapse") {{
            color = Color.valueOf("#ff5533");  // красно-оранжевый


            // Эффект при наложении статуса
            applyEffect = new Effect(25f, e -> {
                Draw.color(Color.valueOf("#ff5533"), e.fout());

                // Взрывное кольцо
                Lines.stroke(3f * e.fout());
                Lines.circle(e.x, e.y, 25f * e.finpow());

                // Частицы
                for (int i = 0; i < 8; i++) {
                    float angle = e.rotation + i * 45f;
                    float len = 20f * e.finpow();
                    Tmp.v1.trns(angle, len);
                    Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2.5f * e.fout());
                }
            });

            // Статы
            speedMultiplier = 0.4f;        // -60% скорости
            reloadMultiplier = 0.5f;       // -50% скорострельности
            healthMultiplier = 0.8f;

            // Периодический урон (разрывает материю)
            intervalDamage = 20f;          // 12 урона каждые...
            intervalDamageTime = 1f;      // ...20 тиков (0.33 сек) → 36 урона/сек

            // Визуал (красные/оранжевые искры)
            effect = new Effect(35f, e -> {
                Draw.color(Color.valueOf("#ff5533"), Color.valueOf("#ff8844"), e.fout());

                // Частицы разрыва
                for (int i = 0; i < 4; i++) {
                    float angle = e.rotation + i * 90f + e.time * 15f;
                    float len = 12f * e.finpow();
                    Tmp.v1.trns(angle, len);

                    // Кроваво-красные лучи
                    Lines.stroke(2f * e.fout(), Color.valueOf("#ff4422"));
                    Lines.lineAngle(e.x, e.y, angle, len);

                    // Искры
                    Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * e.fout());
                }

                // Вихревая аура
                Draw.color(Color.valueOf("#ff6644"), e.fout() * 0.6f);
                for (int i = 0; i < 6; i++) {
                    float angle = e.time * 20f + i * 60f;
                    Tmp.v1.trns(angle, 10f * e.finpow());
                    Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 1.5f * e.fout());
                }
            });
            effectChance = 0.1f;  // 10% шанс показать эффект каждый тик

            // Противоположные эффекты
            init(() -> {
                opposite(StatusEffects.freezing);   // Холод гасит гравитацию
                opposite(StatusEffects.wet);        // Вода мешает искажению
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    // Огонь + гравитация = взрывной урон
                    unit.damage(25f);
                    result.set(gravityCollapse, Math.min(time + result.time, 120f));
                });
            });
        }};
    }
}