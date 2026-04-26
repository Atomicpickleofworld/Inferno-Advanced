//package infernoadvanced.content.nuclear;
//
//import arc.graphics.Color;
//import arc.graphics.g2d.Draw;
//import arc.math.Mathf;
//import mindustry.content.Fx;
//import mindustry.entities.Effect;
//import mindustry.entities.bullet.BulletType;
//import mindustry.entities.bullet.ExplosionBulletType;
//import mindustry.gen.Sounds;
//
//import static arc.graphics.g2d.Draw.*;
//import static arc.graphics.g2d.Fill.*;
//import static arc.graphics.g2d.Lines.*;
//
//public class InfernoNuclearBullets {
//
//    // ===== ЦВЕТА (ТОЛЬКО HEX) =====
//    public static final Color CESIUM_GREEN = Color.valueOf("#99ff66");
//    public static final Color WHITE_HOT = Color.valueOf("#ffffff");
//    public static final Color SMOKE_GRAY = Color.valueOf("#4a4a4a");
//
//    // ===== СНАРЯД ДЫМОВОГО ОБЛАКА (летит во все стороны) =====
//    public static BulletType smokeCloudBullet;
//
//    // ===== ВЗРЫВ В ЦЕНТРЕ (как у Quad, но 100 блоков) =====
//    public static BulletType nuclearDeathExplosion;
//
//    public static void load() {
//
//        // ██████ 1. СНАРЯД ДЫМОВОГО ОБЛАКА ██████
//        smokeCloudBullet = new ExplosionBulletType(70000f, 60f) {{
//            speed = 2.5f;
//            lifetime = 120f;
//            drag = 0.02f;
//
//            // ✅ Несбиваемый
//            hittable = false;
//            absorbable = false;
//            reflectable = false;
//            collides = false;
//            collidesGround = true;
//            collidesAir = true;
//            collidesTiles = true;
//
//
//            hitColor = CESIUM_GREEN;
//            trailColor = WHITE_HOT;
//            trailColor = CESIUM_GREEN;
//            trailWidth = 4f;
//            trailLength = 30;
//
//            // ✅ Пробивает всё
//            pierce = true;
//            pierceCap = 999;
//            pierceBuilding = true;
//
//            // ✅ Эффекты (дым + облако)
//            hitEffect = despawnEffect = new Effect(90f, 200f, e -> {
//                Draw.color(CESIUM_GREEN, SMOKE_GRAY, e.fout());
//                Draw.alpha(0.7f * e.fout());
//
//                for(int i = 0; i < 40; i++){
//                    float angle = Mathf.randomSeed(e.id + i, 360f);
//                    float len = Mathf.randomSeed(e.id + i + 1, 0f, 50f * e.finpow());
//                    float size = 6f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f);
//                    circle(e.x + Mathf.cos(angle) * len, e.y + Mathf.sin(angle) * len, size);
//                }
//
//                Draw.color(WHITE_HOT, e.fout() * 0.5f);
//                stroke(3f * e.fout());
//                circle(e.x, e.y, 60f * e.finpow());
//            }).layer(100f);
//
//            shootEffect = Fx.none;
//            hitSound = Sounds.explosionArtilleryShockBig;
//            hitSoundVolume = 3f;
//
//
//            // ✅ Статус-эффект (радиация)
//            status = infernoadvanced.content.InfernoStatusEffects.radioactive;
//            statusDuration = 60f * 300f;
//
//            lightRadius = 50f;
//            lightColor = CESIUM_GREEN;
//            lightOpacity = 0.7f;
//        }};
//
//        // ██████ 2. ВЗРЫВ В ЦЕНТРЕ (как у Quad, 100 блоков) ██████
//        nuclearDeathExplosion = new ExplosionBulletType(120000f, 100f * 8f) {{
//            speed = 0f;
//            lifetime = 1f;
//
//            hitColor = CESIUM_GREEN;
//            shootEffect = Fx.none;
//
//            // ✅ Эффект дыма (как smokeAoeCloud)
//            hitEffect = new Effect(180f, 400f, e -> {
//                Draw.color(CESIUM_GREEN, SMOKE_GRAY, e.fout());
//                Draw.alpha(0.7f * e.fout());
//
//                for(int i = 0; i < 60; i++){
//                    float angle = Mathf.randomSeed(e.id + i, 360f);
//                    float len = Mathf.randomSeed(e.id + i + 1, 0f, 80f * e.finpow());
//                    float size = 8f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f);
//                    circle(e.x + Mathf.cos(angle) * len, e.y + Mathf.sin(angle) * len, size);
//                }
//
//                Draw.color(WHITE_HOT, e.fout() * 0.5f);
//                stroke(4f * e.fout());
//                circle(e.x, e.y, 100f * 8f * e.finpow());
//            }).layer(100f);
//
//            hitSound = Sounds.explosionArtilleryShockBig;
//            hitSoundVolume = 5f;
//
//            // ✅ ГЛАВНОЕ: Спавнит дымовые облака во все стороны
//            fragBullets = 48;
//            fragSpread = 360f;
//            fragRandomSpread = 0f;
//            fragBullet = smokeCloudBullet;
//
//
//            // ✅ Статус-эффект (радиация на 10 минут)
//            status = infernoadvanced.content.InfernoStatusEffects.radioactive;
//            statusDuration = 60f * 600f;
//        }};
//    }
//
//    private static void circle(float v, float v1, float size) {
//
//    }
//}