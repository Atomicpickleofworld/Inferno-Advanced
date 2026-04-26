//package infernoadvanced.content.nuclear;
//
//import arc.graphics.Color;
//import arc.graphics.g2d.Draw;
//import arc.graphics.g2d.Fill;
//import arc.graphics.g2d.Lines;
//import arc.math.Interp;
//import arc.math.Mathf;
//import mindustry.content.Fx;
//import mindustry.entities.Effect;
//import mindustry.entities.bullet.BasicBulletType;
//import mindustry.entities.bullet.BulletType;
//import mindustry.entities.effect.MultiEffect;
//import mindustry.entities.effect.WaveEffect;
//import mindustry.gen.Sounds;
//import mindustry.gen.TimedKillUnit;
//import mindustry.graphics.Layer;
//import mindustry.type.Weapon;
//import mindustry.type.unit.MissileUnitType;
//
//import static arc.math.Angles.randLenVectors;
//
//public class InfernoNuclearUnits {
//
//    // ===== ЦВЕТА (ПЕРЕКРАШИВАЕМ ASTHOSUS В ЗЕЛЁНЫЙ!) =====
//    private static final Color CESIUM_GREEN = Color.valueOf("#99ff66");        // Вместо c2ba9c
//    private static final Color CESIUM_DARK = Color.valueOf("#336622");         // Вместо a69d7d
//    private static final Color SMOKE_GRAY = Color.valueOf("#4a4a4a");          // Вместо 959595
//    private static final Color SPARK_ORANGE = Color.valueOf("#ff6644");        // Вместо edc66c
//    private static final Color WHITE_HOT = Color.valueOf("#ffffff");
//
//    public static BulletType nuclearShrapnel;
//    public static BulletType nuclearShockwaveBullet;
//    public static MissileUnitType nuclearMissile;
//
//    public static void load() {
//
//        // ██████ 1. ОСКОЛКИ (долгоживущие) ██████
//        nuclearShrapnel = new BasicBulletType(3f, 5000f) {{
//            width = 12f;
//            height = 12f;
//            sprite = "circle";
//            lifetime = 600f;
//            drag = 0.015f;
//
//            hittable = false;
//            absorbable = false;
//            reflectable = false;
//            collides = false;
//            collidesGround = true;
//            collidesAir = true;
//            collidesTiles = true;
//
//            pierce = true;
//            pierceCap = 999;
//            pierceBuilding = true;
//
//            backColor = SPARK_ORANGE;
//            frontColor = WHITE_HOT;
//            trailColor = SPARK_ORANGE;
//            trailWidth = 3f;
//            trailLength = 25;
//
//            hitEffect = despawnEffect = new Effect(30f, e -> {
//                Draw.color(SPARK_ORANGE, WHITE_HOT, e.fout());
//                Fill.circle(e.x, e.y, 8f * e.fout());
//            });
//
//            shootEffect = Fx.none;
//            hitSound = Sounds.explosion;
//            hitSoundVolume = 2f;
//
//            status = infernoadvanced.content.InfernoStatusEffects.radioactive;
//            statusDuration = 60f * 120f;
//
//            lightRadius = 30f;
//            lightColor = SPARK_ORANGE;
//            lightOpacity = 0.6f;
//        }};
//
//        // ██████ 2. УДАРНАЯ ВОЛНА (ПРЯМО КАК В ASTHOSUS, НО ЗЕЛЁНАЯ!) ██████
//        // ██████ 2. УДАРНАЯ ВОЛНА (ПЕРЕКРАШЕННЫЙ ASTHOSUS, БЕЗ УРОНА!) ██████
//        nuclearShockwaveBullet = new BulletType() {{
//            speed = 0f;
//            damage = 0;
//            splashDamage = 2000f; // ✅ УРОН ВЗРЫВА = 2000 (вместо 70000!)
//            splashDamageRadius = 320f; // 40 блоков
//            splashDamagePierce = true;
//
//            instantDisappear = true;
//            collides = false;
//            hittable = false;
//            absorbable = false;
//
//
//            // ✅ MultiEffect (КАК В ASTHOSUS, НО БЫСТРЕЕ И МЕНЬШЕ!)
//            despawnEffect = new MultiEffect(
//                    // 1. ExplosionEffect (главный взрыв — плотное облако)
//                    new Effect(180f, 400f, e -> {
//                        Draw.color(CESIUM_GREEN, SMOKE_GRAY, e.fout());
//                        Draw.alpha(0.9f * e.fout());
//
//                        // Плотное облако (120 частиц)
//                        randLenVectors(e.id, 120, 320f * e.finpow(), (x, y) -> {
//                            float size = 8f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f);
//                            Fill.circle(e.x + x, e.y + y, size);
//                        });
//
//                        // Белое ядро
//                        Draw.color(WHITE_HOT, e.fout() * 0.8f);
//                        Fill.circle(e.x, e.y, 40f * e.finpow());
//
//                        // Внешнее кольцо
//                        Draw.color(CESIUM_GREEN, e.fout());
//                        Lines.stroke(6f * e.fout());
//                        Lines.circle(e.x, e.y, 320f * e.finpow());
//                    }).layer(Layer.effect + 10f),
//
//                    // 2. Первая волна (быстрая)
//                    new WaveEffect() {{
//                        sizeFrom = 0f;
//                        sizeTo = 24f;
//                        strokeFrom = 1f;
//                        strokeTo = 6f;
//                        interp = Interp.circleOut;
//                        lifetime = 48f;
//                        startDelay = 0f;
//                        colorFrom = CESIUM_GREEN;
//                        colorTo = CESIUM_DARK;
//                    }},
//
//                    // 3. Вторая волна (сжатие)
//                    new WaveEffect() {{
//                        sizeFrom = 24f;
//                        sizeTo = 0f;
//                        strokeFrom = 6f;
//                        strokeTo = 1f;
//                        interp = Interp.circleOut;
//                        startDelay = 47f;
//                        lifetime = 48f;
//                        colorFrom = CESIUM_GREEN;
//                        colorTo = CESIUM_DARK;
//                    }},
//
//                    // 4. ГЛАВНАЯ ВОЛНА (БЫСТРАЯ И МАЛЕНЬКАЯ!)
//                    new WaveEffect() {{
//                        sizeFrom = 0f;
//                        sizeTo = 320f; // ✅ РАЗМЕР = ВЗРЫВУ (320f пикселей = 40 блоков)
//                        strokeFrom = 6f;
//                        strokeTo = 64f;
//                        interp = Interp.exp5Out;
//                        startDelay = 100f;
//                        lifetime = 800f; // ✅ В 3 РАЗА БЫСТРЕЕ (было 2400f / 3 = 800f)
//                        colorFrom = CESIUM_GREEN;
//                        colorTo = CESIUM_DARK;
//                    }}
//            );
//
//            hitSound = Sounds.explosionArtilleryShockBig;
//            hitSoundVolume = 5f;
//
//            // ✅ УБРАЛ update() — НЕТ ПОСТЕПЕННОГО УРОНА!
//        }};
//
//        // ██████ 3. ЮНИТ-РАКЕТА (MissileUnitType) ██████
//        nuclearMissile = new MissileUnitType("nuclear-missile") {{
//            speed = 4f;
//            accel = 0.2f;
//            drag = 0.01f;
//            lifetime = 60f * 3f;
//            rotateSpeed = 2.5f;
//            health = 100f;
//            hitSize = 6f;
//
//            outlineColor = Color.valueOf("#262626");
//            engineColor = trailColor = CESIUM_GREEN;
//            engineSize = 2.5f;
//            engineOffset = 5f;
//            engineLayer = Layer.effect;
//            trailLength = 20;
//
//            constructor = TimedKillUnit::create;
//
//            targetAir = true;
//            targetGround = true;
//            lowAltitude = true;
//            loopSound = Sounds.loopMissileTrail;
//            loopSoundVolume = 0.5f;
//
//            canDrown = false;
//
//            deathSound = Sounds.explosionArtilleryShockBig;
//
//            weapons.add(new Weapon("inferno-advanced-nuclear-weapon-1") {{
//                shootCone = 360f;
//                mirror = false;
//                reload = 1f;
//                shootOnDeath = true;
//                shootSound = Sounds.explosionArtilleryShockBig;
//                shootSoundVolume = 5f;
//                shake = 100f;
//
//                bullet = nuclearShockwaveBullet;
//            }});
//
//
//            researchCostMultiplier = 0f;
//            hidden = false;
//            drawCell = false;
//            drawMinimap = true;
//        }};
//    }
//}