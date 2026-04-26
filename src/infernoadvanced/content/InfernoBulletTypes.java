package infernoadvanced.content;

import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.content.*;
import mindustry.entities.effect.MultiEffect;


public class InfernoBulletTypes {

    public static BulletType deathExplosion;
    public static BulletType strontiumBulletSalvo;
    public static Effect strontiumSmallCloud;
    public static BulletType celestineFlame;
    public static BulletType celestineFlare;
    public static BulletType nuclearWave;


    public static void load() {
        {
            // 1. Создаем маленький эффект яда (радиус ~3 блока)
            strontiumSmallCloud = new Effect(40f, e -> {
                Draw.color(Color.valueOf("a3ff8c"));
                // Рисуем несколько случайных облачков в небольшом радиусе
                for(int i = 0; i < 16; i++){
                    float ang = i * 22.5f + Mathf.random(30f);
                    // Разлет максимум на 24 пикселя (3 блока)
                    float len = e.fin(Interp.pow2Out) * 24f;
                    Fill.circle(e.x + Mathf.cosDeg(ang) * len, e.y + Mathf.sinDeg(ang) * len, 1f + e.fout() * 3f);
                }
            });

            // 2. Настраиваем сам снаряд
            strontiumBulletSalvo = new ArtilleryBulletType(3f, 40f){{
                // Вместо гигантского взрыва используем наш новый аккуратный эффект
                hitEffect = new MultiEffect(Fx.blastExplosion, strontiumSmallCloud);
                despawnEffect = strontiumSmallCloud;

                width = height = 10f;
                shrinkY = 0.3f; // Снаряд немного сужается в полете

                splashDamageRadius = 3f * 8f; // РАДИУС 3 БЛОКА (1 блок = 8 пикселей)
                splashDamage = 45f;
                damage = 12f;
                collidesAir = true;
                collidesGround = true;
                ammoMultiplier = 2f;
                buildingDamageMultiplier = 0.3f;

                // Оставляем радиацию, но уменьшаем длительность до 5 секунд (300 тиков)
                status = InfernoStatusEffects.radioactive;
                statusDuration = 1200f;

                hitColor = Color.valueOf("a3ff8c");
                backColor = Color.valueOf("339554");
                frontColor = Color.valueOf("a3ff8c");

                trailColor = Color.valueOf("a3ff8c");
                trailWidth = 2.5f;
                trailLength = 8;
            }};
        }



        deathExplosion = new BombBulletType(15000f, 12f * 8f) {{
            hitColor = Color.valueOf("ff6644");
            // Используем наш новый статус
            status = InfernoStatusEffects.radioactive;
            statusDuration = 2400f;
            instantDisappear = true;

            // Смешиваем эффекты: Огромная вспышка + Ядерное облако + Твои дымы
            hitEffect = new MultiEffect(
                    Fx.massiveExplosion,
                    Fx.scatheExplosion,
                    new Effect(60f, e -> {
                        // Создаем пучок зеленых облаков (твоя идея со smokeAoeCloud)
                        for(int i = 0; i < 15; i++){
                            float angle = i * (360f / 15f);
                            float len = e.fin() * 120f; // Облака разлетаются в стороны
                            Fx.smokeAoeCloud.at(e.x + Mathf.cosDeg(angle) * len, e.y + Mathf.sinDeg(angle) * len, Color.valueOf(Color.valueOf("013220"), "a3ff8c"));
                        }
                    })
            );
        }};



        













    }
}
