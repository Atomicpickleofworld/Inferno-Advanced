package infernoadvanced.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import infernoadvanced.entities.bullet.BlackHoleBulletType;
import infernoadvanced.graphics.IADraw;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.world.blocks.InfernoReactor;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static infernoadvanced.content.InfernoBulletTypes.strontiumBulletSalvo;
import static infernoadvanced.content.InfernoItems.celestine;
import static mindustry.type.ItemStack.with;

public class InfernoBlocks {
    //power-serpulo
    public static Block fusionConverter;

    //crafters-serpulo
    public static GenericCrafter refractoryBronzeSmelter;
    public static Block celestineSynthesizer;
    public static Block barium133Producer;
    public static Block cesiumGenerator;
    public static Block bariumEnricher;
    public static Block strontiumCentrifuge;

    //turrets-serpulo
    public static ItemTurret duplet;
    public static LaserTurret graphiteCutter;
    public static ItemTurret ember;
    public static ItemTurret celestineBurst;

    public static ItemTurret swiftAA;
    public static ItemTurret stormAA;
    public static ItemTurret tempestAA;

    public static ItemTurret vulcan;
    public static ItemTurret blizzard;
    public static PowerTurret phaseRailgun;

    public static PowerTurret impulse;
    public static Block singularityCannon;

    public static ItemTurret MLRSskyVolley;
    public static ItemTurret MLRShail;
    public static ItemTurret cataclysm;

    //turrets-erekir
    public static ItemTurret phoenix;
    public static ItemTurret archimedes;
//    public static Turret oblivion;



    public static void load() {

// Находим пушку и добавляем ей новый тип патрона через ObjectMap
        ((ItemTurret)Blocks.salvo).ammoTypes.put(InfernoItems.strontium, strontiumBulletSalvo);


//█████████████████████████power-serpulo█████████████████████████

        fusionConverter = new InfernoReactor("fusion-converter") {{
            // Удешевили: теперь только базовые ресурсы реактора
            requirements(Category.power, with(
                    Items.lead, 450,
                    Items.graphite, 300,
                    Items.thorium, 150,
                    Items.silicon, 200
            ));

            size = 4;
            health = 3500;

            // Энергия: 4500 / 60 (так как в Mindustry считается за тик)
            powerProduction = 5500f / 60f;

            // Потребление для старта (500 энергии)
            consumePower(500f / 60f);

            consumeItem(InfernoItems.strontium);
            // Жидкость теперь просто декоративная или для красоты, взрыв без неё отключен
            consumeLiquid(Liquids.cryofluid, 0.2f).update(false).optional(true, false);

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion() {{ color = Color.valueOf("a3ff8c"); }}
            );



            lightColor = Color.valueOf("a3ff8c");
        }};

//█████████████████████████crafters-serpulo█████████████████████████
        refractoryBronzeSmelter = new GenericCrafter("refractory-bronze-smelter") {{
            requirements(Category.crafting, with(
                    Items.copper, 60,
                    Items.lead, 40,
                    Items.graphite, 20
            ));

            localizedName = "Плавильня тугоплавкой бронзы";
            description = "Создаёт тугоплавкую бронзу из меди, свинца и графита. Высокая температура плавления требует стабильного энергоснабжения.";

            size = 2;
            health = 480;
            itemCapacity = 30;
            craftTime = 90f; // 1.5 секунды (медленнее, чем сплав волны)

            // Рецепт: 3 медь + 2 свинец + 1 графит = 1 бронза
            consumeItems(with(
                    Items.copper, 3,
                    Items.lead, 2,
                    Items.graphite, 1
            ));

            consumePower(2.0f); // 2 ед/сек (требует энергии)

            outputItem = new ItemStack(InfernoItems.refractoryBronze, 1);

            // Визуал
            craftEffect = Fx.smeltsmoke;
            updateEffect = Fx.pulverizeMedium;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("#ffaa00"); // Оранжевое свечение нагрева
                        alpha = 0.6f;
                    }}
            );
        }};

        celestineSynthesizer = new GenericCrafter("celestine-synthesizer") {{
            localizedName = "Celestine Synthesizer";
            description = "Преобразует титан в кристаллы целестина, используя экстремальное охлаждение криогенной жидкостью.";
            size = 3;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;
            itemCapacity = 20;
            ambientSound = Sounds.loopBio;
            ambientSoundVolume = 0.08f;
            requirements(Category.crafting, with(
                    Items.titanium, 120,
                    Items.silicon, 80,
                    Items.lead, 100
            ));

            craftTime = 90f; // Делаем процесс не очень быстрым

            // Рецепт
            consumePower(4.5f); // Жрет прилично энергии
            consumeItem(Items.titanium, 2);
            consumeLiquid(Liquids.cryofluid, 0.2f);

            outputItem = new ItemStack(celestine, 1);

            // Визуал
            updateEffect = Fx.freezing; // Эффект снежинок/холода вокруг
            craftEffect = Fx.pulverizeMedium;

            regionRotated1 = 1; // Если хочешь добавить крутящиеся части
        }};


        // 1. Производитель бария-133 (из титана и свинца)
        barium133Producer = new GenericCrafter("barium133-producer") {{
            requirements(Category.crafting, with(
                    Items.silicon, 80,
                    Items.titanium, 60,
                    Items.lead, 100
            ));

            size = 2;
            health = 960;

            consumeItems(with(
                    Items.titanium, 2,
                    Items.lead, 3
            ));

            consumePower(2f);

            outputItem = new ItemStack(InfernoItems.barium133, 1);
            craftTime = 120f;

            // ===== БЕЗОПАСНЫЙ ВИЗУАЛ (без жидкостей) =====
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion("-rotator") {{
                        rotateSpeed = 2f;
                        spinSprite = true;
                        x = 0f;
                        y = 2f;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("a8e6cf");
                        alpha = 0.5f;
                        glowScale = 6f;
                        rotateSpeed = 0.5f;
                    }}
            );

            ambientSound = Sounds.loopExtract;
            ambientSoundVolume = 0.05f;

            itemCapacity = 30;
        }};

// 2. Цезиевый генератор (из тория)
        cesiumGenerator = new GenericCrafter("cesium-generator") {{
            requirements(Category.crafting, with(
                    Items.thorium, 120,
                    Items.silicon, 150,
                    Items.graphite, 100,
                    Items.lead, 200
            ));

            size = 3;
            health = 2560;

            // Входные ресурсы
            consumeItems(with(
                    Items.thorium, 2,    // Торий как исходный материал
                    Items.sand, 4        // Песок для химии
            ));

            // Жидкость для охлаждения/реакции
            consumeLiquid(Liquids.water, 0.15f);

            // Энергия для работы
            consumePower(4f);

            // Выход: цезий-137
            outputItem = new ItemStack(InfernoItems.cesium137, 1);

            // Время крафта
            craftTime = 60f;

            // Визуал
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water) {{ alpha = 0.5f; }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("ffd93d");
                        alpha = 0.5f;
                        glowScale = 8f;
                    }}
            );

            ambientSound = Sounds.loopHover;
            ambientSoundVolume = 0.05f;

            // Вместимость предметов
            itemCapacity = 30;
            liquidCapacity = 60f;
        }};

// 3. Обогатитель бария (мега-реактор)
        bariumEnricher = new GenericCrafter("barium-enricher") {{
            requirements(Category.crafting, with(
                    Items.copper, 450,
                    Items.surgeAlloy, 50,
                    Items.phaseFabric, 85,
                    Items.thorium, 200,
                    Items.titanium, 100
            ));

            size = 4;
            health = 4000;

            // Только один крафт — барий-133 + цезий-137 → барий-137
            consumeItems(with(
                    InfernoItems.barium133, 3,
                    InfernoItems.cesium137, 1
            ));

            // Только криожидкость (без воды)
            consumeLiquid(Liquids.cryofluid, 0.2f);

            outputItem = new ItemStack(InfernoItems.barium137, 1);

            craftTime = 240f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile() {{
                        drawLiquidLight = true;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("ff6b6b");
                        alpha = 0.8f;
                        glowScale = 12f;
                    }}
            );

            ambientSound = Sounds.loopHover2;
            ambientSoundVolume = 0.08f;
        }};




        strontiumCentrifuge = new GenericCrafter("strontium-centrifuge") {{
            requirements(Category.crafting, with(
                    Items.titanium, 250,
                    Items.thorium, 150,
                    Items.silicon, 200,
                    Items.surgeAlloy, 50 // Добавим немного фазового сплава для сложности
            ));

            size = 4;
            health = 2000;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;

            // Время крафта — 2.5 секунды (медленно, но верно)
            craftTime = 150f;

            // Сложный рецепт:
            consumeItems(with(
                    celestine, 3,
                    Items.coal, 2,
                    Items.pyratite, 1 // Пиротит нужен для поддержания сверхвысокой температуры
            ));

            // Потребление ресурсов
            consumeLiquid(Liquids.cryofluid, 0.3f); // Огромный расход охладителя
            consumePower(18f); // Жрет энергию как не в себя

            outputItem = new ItemStack(InfernoItems.strontium, 1);

            // Визуал: добавим дым и искры
            craftEffect = Fx.explosion; // Маленький грибок при каждом завершении цикла
            updateEffect = Fx.smokeCloud;
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.09f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile() {{
                        drawLiquidLight = true;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = Color.valueOf("#48db79");
                        alpha = 0.8f;
                        glowScale = 6f;
                    }}
            );

//            drawer = new DrawMulti(
//                    new DrawDefault(),
//                    new DrawFlame(Color.valueOf("ffef99")), // Огонь внутри
//                    new DrawPlasma() {{ // Плазма сверху для пафоса
//                        Color color = Color.valueOf("a3ff8c");
//                    }}
//            );
        }};









//█████████████████████████turrets-serpulo█████████████████████████


// В методе load():

// ██████ 1. DUPLET (Дуплет) — Улучшенный Duo ██████
        // ██████ 1. DUPLET (Дуплет) — Улучшенный Duo ██████
        duplet = new ItemTurret("duplet") {{
            requirements(Category.turret, with(Items.copper, 55,
                    Items.lead, 45,
                    Items.graphite, 25));
            localizedName = "Дуплет";
            description = "Улучшенная версия Duo с двумя стабилизированными стволами. Графитовые направляющие позволяют вести более точный огонь. Выпускает два снаряда за выстрел с минимальной задержкой.";

            size = 1;
            health = 380; // Больше чем Duo (250)
            range = 160f; // Такая же как Duo (20 блоков)
            reload = 20f; // Как у Duo (1 сек)
            shootCone = 15f; // Как у Duo
            rotateSpeed = 10f; // Как у Duo
            recoil = 0.5f; // Как у Duo
            shootY = 3f;
            shake = 0.4f;

            inaccuracy = 2f; // Как у Duo
            targetAir = true;
            targetGround = true;

            // ===== ДВА ВЫСТРЕЛА С МИНИМАЛЬНОЙ ЗАДЕРЖКОЙ =====
            shoot = new ShootAlternate(3f) {{
                shots = 2;
                shotDelay = 0; // Минимальная задержка между выстрелами (визуально почти одновременно)
            }};

            // ===== БОЕПРИПАСЫ (улучшены как у Duo) =====
            ammo(
                    // Медь — базовый, но с улучшенной скорострельностью
                    Items.copper, new BasicBulletType(2.5f, 14) {{
                        width = 7f;
                        height = 9f;
                        lifetime = 60f;
                        ammoMultiplier = 2;
                        reloadMultiplier = 0.9f; // Немного быстрее

                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        hitColor = backColor = trailColor = Pal.copperAmmoBack;
                        frontColor = Pal.copperAmmoFront;
                        trailWidth = 1.2f;
                        trailLength = 5;
                    }},

                    // Графит — основной боеприпас (высокий урон)
                    Items.graphite, new BasicBulletType(3.8f, 22) {{
                        width = 9f;
                        height = 12f;
                        ammoMultiplier = 4;
                        lifetime = 60f;
                        reloadMultiplier = 0.75f; // Быстрее перезарядка
                        rangeChange = 12f; // Немного больше дальность

                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
                        frontColor = Pal.graphiteAmmoFront;
                        trailWidth = 1.5f;
                        trailLength = 6;

                    }},

                    // Кремний — самонаведение и трассеры
                    Items.silicon, new BasicBulletType(3.2f, 14) {{
                        width = 7f;
                        height = 9f;
                        homingPower = 0.25f; // Немного лучше чем у Duo (0.2f)
                        homingRange = 40f;
                        reloadMultiplier = 1.4f; // Быстрее стрельба
                        ammoMultiplier = 5;
                        lifetime = 60f;

                        trailLength = 6;
                        trailWidth = 1.5f;

                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        hitColor = backColor = trailColor = Pal.siliconAmmoBack;
                        frontColor = Pal.siliconAmmoFront;

                        // Уникальное свойство: шанс оглушить врага
                        status = StatusEffects.electrified;
                        statusDuration = 30f;
                    }}
            );

            // ===== ОХЛАЖДЕНИЕ (как у Duo) =====
            coolant = consumeCoolant(0.1f);
            coolantMultiplier = 10f;

            // ===== ЭНЕРГИЯ (опционально для баланса) =====
            // consumePower(0.5f); // Раскомментируй если хочешь сделать энергозависимым

            // ===== ВИЗУАЛЫ (улучшенные, с двумя стволами) =====
            drawer = new DrawTurret() {{
                // Добавляем два ствола как в Duo
                for(int i = 0; i < 2; i++) {
                    int f = i;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")) {{
                        progress = PartProgress.recoil;
                        recoilIndex = f;
                        under = true;
                        moveY = -1.5f;
                    }});
                }

                // Добавляем нагревающуюся верхнюю часть
                parts.add(new RegionPart("-top") {{
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup;
                    color = Pal.turretHeat;
                }});

                // Добавляем графитовые направляющие для визуала
                parts.add(new RegionPart("-rails") {{
                    progress = PartProgress.warmup;
                    moveY = -0.5f;
                }});
            }};

            // ===== ЗВУКИ И ЭФФЕКТЫ =====
            shootSound = Sounds.shootDuo;
            shootSoundVolume = 0.45f;
            ammoUseEffect = Fx.casing1;
            ammoPerShot = 3;

            // ===== ПРОЧЕЕ =====
            recoils = 2;
            researchCostMultiplier = 0.08f;
            outlineColor = Pal.gray;

            // ===== УНИКАЛЬНЫЕ ОСОБЕННОСТИ =====
            // Лимит дальности
            limitRange(5f);
        }};

// ██████ 2. GRAPHITE CUTTER (Графитовый резак) — Ближний бой ██████
        // ██████ 2. GRAPHITE CUTTER (Графитовый резак) — Ближнебойный лазерный резак ██████
        // ██████ 2. GRAPHITE CUTTER (Графитовый резак) — ИСПРАВЛЕННАЯ ВЕРСИЯ ██████
// ██████ Альтернативная версия — через ItemTurret с лазерным снарядом ██████
        // ██████ 2. GRAPHITE CUTTER — Версия с непрерывным лучом (как meltdown) ██████
// ██████ 2. GRAPHITE CUTTER (Графитовый резак) — ИСПРАВЛЕННЫЙ ██████
        // ===== ТУРЕЛЬ 2: GRAPHITE CUTTER (Графитовый резак) — Микро-Meltdown =====



// ██████ 2. GRAPHITE CUTTER (Графитовый резак) — На основе ванильного Meltdown ██████
        graphiteCutter = new LaserTurret("graphite-cutter") {{
            requirements(Category.turret, with(
                    Items.copper, 75,
                    Items.lead, 55,
                    Items.graphite, 45,
                    InfernoItems.refractoryBronze, 35
            ));

            localizedName = "Графитовый резак";
            description = "Экспериментальный лазер ближнего боя. Использует графитовые стержни для генерации мощного электрического разряда. Эффективен против бронированных целей в упор.";

            // ===== БАЗОВЫЕ ПАРАМЕТРЫ (как Meltdown, но меньше) =====
            size = 2; // 2×2 (Meltdown: 3×3)
            health = 680; // Meltdown: 1100
            range = 110f; // 14 блоков (Meltdown: 220f/28 блоков)

            // ✅ ВАЖНО: Параметры стрельбы как в ванильном Meltdown
            reload = 300f;           // 5 секунд между выстрелами (Meltdown: 300f)
            shootDuration = 90f;     // 1.5 секунды ВЫСТРЕЛА (Meltdown: 120f)
            cooldownTime = 150f;     // 2.5 секунды ОСТЫВАНИЯ (Meltdown: 240f)

            recoil = 0f;
            shake = 2f;
            rotateSpeed = 4f;

            targetAir = true;
            targetGround = true;

            shootSound = Sounds.beamLustre;
            shootSoundVolume = 0.4f;

            // ===== ПОТРЕБЛЕНИЕ =====
            consumePower(240f / 60f); // 4 ед/сек (Meltdown: 480f/60f = 8 ед/сек)
            coolant = consumeCoolant(1.2f);
            coolantMultiplier = 4f;

            // ✅ ЛАЗЕРНЫЙ СНАРЯД (ContinuousLaserBulletType как в Meltdown)
            shootType = new ContinuousLaserBulletType(180f) {{
                // ===== ПАРАМЕТРЫ ЛАЗЕРА =====
                length = 110f;  // Дальность (как range турели)
                width = 12f;    // Ширина луча (Meltdown: 18f)
                cooldownTime = 90f; // Должно совпадать с shootDuration!

                // ✅ ВАЖНО: lifetime должен быть >= duration
                lifetime = 45f;

                // ===== ЦВЕТА (графит + электричество) =====
                colors = new Color[]{
                        Color.valueOf("#3a3a4a"), // Тёмно-серое ядро
                        Color.valueOf("#6a6a7a"), // Серая середина
                        Color.valueOf("#a8a8a8"), // Светло-серый (графит)
                        Color.valueOf("#ffd37f"), // Золотистая оболочка (бронза)
                        Color.white               // Белая кромка
                };

                // ===== ЭФФЕКТЫ =====
                shootEffect = Fx.shootHeal;
                hitEffect = Fx.hitMeltdown;
                smokeEffect = Fx.shootSmokeTitan;

                // ===== УРОН =====
                damage = 180f; // Урон в секунду
                buildingDamageMultiplier = 0.6f;

                // ===== ОСОБЫЕ СВОЙСТВА =====
                pierce = true;
                pierceCap = 3;
                pierceArmor = true; // ✅ ИГНОРИРУЕТ БРОНЮ!

                // ===== ЭЛЕКТРИЧЕСТВО (молнии) =====

                // ===== ЭФФЕКТЫ =====
                hitShake = 3f;
                status = StatusEffects.electrified;
                statusDuration = 120f;

                largeHit = true;
                continuous = true;
            }};

            // ===== ВИЗУАЛ (как Meltdown) =====
            drawer = new DrawTurret("reinforced-") {{
                Color heatc = Color.valueOf("#ffd37f"); // Золотой нагрев

                parts.addAll(
                        new RegionPart("-barrel") {{
                            progress = PartProgress.warmup;
                            moveY = -2f;
                            heatColor = heatc;
                        }},
                        new RegionPart("-side") {{
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveX = 1f;
                            moveY = -1f;
                            moveRot = -8f;
                            heatColor = heatc;
                        }},
                        new RegionPart("-glow") {{
                            progress = PartProgress.warmup;
                            color = Color.valueOf("#ffd37f");
                            heatColor = Color.white;
                        }}
                );
            }};

            outlineColor = Color.valueOf("#2a2a3a");
            heatColor = Color.valueOf("#ffd37f");
        }};

        ember = new ItemTurret("ember"){{
            requirements(Category.turret, with(
                    Items.copper, 80,
                    Items.lead, 60,
                    celestine, 40
            ));

            localizedName = "Ember";
            description = "Компактная огненная турель. Поджигает врагов при попадании. Целестин усиливает эффект горения.";

            size = 2;
            health = 450;
            range = 180f;
            reload = 25f;
            shootCone = 20f;
            rotateSpeed = 8f;
            recoil = 1f;
            shake = 1f;
            shootSound = Sounds.shootFlame;

            // Потребление
            consumePower(45f / 60f);
            coolantMultiplier = 10f;

            // Визуал (простой, в стиле Серпуло)
            drawer = new DrawTurret(""){{
                Color heatc = Color.valueOf("ff4444"); // Красный от целестина
                parts.addAll(
                        new RegionPart("-side"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveRot = 12f;
                            heatColor = heatc;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            moveY = -2f / 4f;
                            heatColor = heatc;
                        }}
                );
            }};



            outlineColor = Pal.darkOutline;

            // БОЕПРИПАСЫ
            ammo(
                    // Графит: базовый огонь
                    Items.graphite, new BasicBulletType(3.2f, 22){{
                        width = 8f;
                        height = 11f;
                        lifetime = 55f;
                        status = StatusEffects.burning;
                        statusDuration = 180f;
                        backColor = trailColor = hitColor = Color.valueOf("ff6644");
                        frontColor = Color.white;
                        trailLength = 8;
                        trailWidth = 1.8f;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = Fx.hitFlameSmall;
                        ammoMultiplier = 2f;
                        hitEffect = new Effect(20f, e -> {
                            // 🔥 Искры при попадании
                            IADraw.impactSpark(e.x, e.y, 8f, Color.valueOf("ff6644"), e.rotation);
                        });
                    }},

                    // Целестин: усиленное горение (красное пламя)
                    celestine, new BasicBulletType(3.5f, 28){{
                        width = 9f;
                        height = 12f;
                        lifetime = 50f;
                        status = StatusEffects.burning;
                        statusDuration = 300f; // Дольше горит
                        backColor = trailColor = hitColor = Color.valueOf("ff3333"); // Ярче красный
                        frontColor = Color.valueOf("ffaaaa");
                        trailLength = 10;
                        trailWidth = 2.2f;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = new MultiEffect(Fx.hitFlameSmall, Fx.fire);
                        ammoMultiplier = 1.5f;
                        makeFire = true; // Оставляет огонь на земле
                        hitEffect = new Effect(20f, e -> {
                            // 🔥 Искры при попадании
                            IADraw.impactSpark(e.x, e.y, 8f, Color.valueOf("ff6644"), e.rotation);
                        });
                    }},

                    // Пиротит: взрывной огонь
                    Items.pyratite, new BasicBulletType(3f, 35){{
                        width = 10f;
                        height = 13f;
                        lifetime = 60f;
                        status = StatusEffects.burning;
                        statusDuration = 240f;
                        splashDamage = 25f;
                        splashDamageRadius = 20f;
                        backColor = trailColor = hitColor = Color.valueOf("ffaa55");
                        frontColor = Color.white;
                        trailLength = 9;
                        trailWidth = 2f;
                        shootEffect = Fx.shootSmallFlame;
                        hitEffect = new MultiEffect(Fx.hitFlameSmall, Fx.blastExplosion);
                        ammoMultiplier = 3f;
                        makeFire = true;
                    }}
            );

            limitRange();
        }};


        celestineBurst = new ItemTurret("celestine-burst") {{

            requirements(Category.turret, with(Items.silicon, 120, Items.titanium, 85, Items.plastanium, 55));
            size = 2; // Оставляем компактной
            health = 880;
            reload = 120f; // Немного увеличим перезарядку, т.к. выстрелов 2, но это компенсируется уроном
            range = 260f;
            targetAir = true; // Зенитка
            targetGround = false; // Только по воздуху
            shootSound = Sounds.shootCyclone; // Используем более басистый


            coolantMultiplier = 8f;

            // --- ДВА ВЫСТРЕЛА ЗА РАЗ ---
            // Это ключевой момент! ShootAlternate позволяет чередовать стволы
            shoot = new ShootAlternate(4f); // 4f - это расстояние между стволами
            ((ShootAlternate)shoot).shots = 2; // Два выстрела за раз
            ((ShootAlternate)shoot).barrels = 2; // Используем 2 ствола
            ((ShootAlternate)shoot).spread = 3f; // Небольшой разброс между выстрелами для красоты

            ammo(

                    Items.pyratite, new FlakBulletType(5.2f, 0f) {{ // Увеличим скорость и урон
                        width = 24f;
                        height = 30f;
                        lifetime = 75f; // Немного уменьшим жизнь, т.к. скорость выше, дальность сохранится
                        ammoMultiplier = 4f; // Больше боеприпасов из одного ресурса

                        // --- УСИЛЕНИЕ ЭФФЕКТОВ ---
                        // Волна (weave) - теперь более хаотичная и заметная
                        weaveMag = 4.5f;
                        weaveScale = 5f;

                        // --- ДЛИННЫЙ, ТОЛСТЫЙ, ИСКРЯЩИЙСЯ ХВОСТ ---
                        trailLength = 65; // Ещё длиннее
                        trailWidth = 3f; // Ещё жирнее
                        trailColor = Color.valueOf("ffaa33"); // Ярко-оранжевый

                        // Частицы в хвосте (искры) - теперь их МНОГО
                        trailEffect = new MultiEffect(
                                Fx.artilleryTrail,
                                Fx.fireSmoke, // Добавим дымок
                                new Effect(35f, e -> { // Кастомный эффект искр
                                    color(Color.valueOf("ffaa33"), Color.valueOf("ff5500"), e.fin());
                                    randLenVectors(e.id, 3, 2f + e.fin() * 8f, (x, y) -> {
                                        Fill.circle(e.x + x, e.y + y, 0.5f + e.fout() * 2f);
                                    });
                                })
                        );
                        trailInterval = 1.5f; // Частицы сыпятся чаще

                        // --- ХАРАКТЕРИСТИКИ ---
                        collidesGround = false;
                        collidesAir = true;
                        splashDamage = 12f; // Добавим небольшой радиусный урон
                        splashDamageRadius = 18f;
                        status = StatusEffects.burning;
                        statusDuration = 400f;

                        // --- ОСКОЛКИ (ГЛАВНОЕ УКРАШЕНИЕ) ---
                        // Теперь осколки — это мини-версия снаряда, а не просто "пульки"
                        fragBullets = 7; // Меньше, но каждый мощнее
                        fragVelocityMin = 0.6f;
                        fragVelocityMax = 1.2f;
                        fragLifeMin = 0.6f;
                        fragRandomSpread = 30f;
                        fragBullet = new FlakBulletType(3.2f, 0f) {{ // Осколок - тоже Flak
                            width = 10f;
                            height = 16f;
                            lifetime = 35f;
                            trailLength = 18;
                            trailWidth = 1.8f;
                            trailColor = Color.valueOf("ffaa33");
                            splashDamage = 12f;
                            splashDamageRadius = 12f;
                            status = StatusEffects.burning;
                            statusDuration = 200f;
                            // У осколков тоже есть свой маленький хвостик
                            trailEffect = Fx.artilleryTrail;
                            trailInterval = 2f;
                            backColor = Color.valueOf("ffaa33");
                            frontColor = Color.valueOf("ffffaa");
                            collidesGround = false;
                            hitEffect = Fx.fireHit; // Маленький взрыв при попадании осколка
                        }};

                        // --- ЦВЕТА И СВЕТ ---
                        lightRadius = 160f;
                        lightColor = Color.valueOf("ffaa33");
                        frontColor = Color.valueOf("ffffaa");
                        backColor = Color.valueOf("ff5500");

                        // --- ЭФФЕКТ ПОПАДАНИЯ (АПОФЕОЗ) ---
                        hitEffect = new MultiEffect(
                                Fx.blastExplosion,
                                Fx.massiveExplosion, // Добавим побольше взрыва
                                new Effect(45f, e -> { // Кастомный фейерверк
                                    color(Color.valueOf("ffaa33"), Color.valueOf("ff5500"), e.fin());
                                    for(int i = 0; i < 6; i++) {
                                        float angle = e.rotation + i*60 + Mathf.randomSeedRange(e.id+i, 20f);
                                        float len = e.finpow() * 40f;
                                        Drawf.tri(e.x, e.y, e.fout()*6f, len, angle);
                                    }
                                })
                        );
                        despawnEffect = hitEffect; // При окончании жизни тоже взрывается
                    }}
            );

            // --- ВИЗУАЛ ТУРЕЛИ (ДВА СТВОЛА) ---
            drawer = new DrawTurret() {{
                // Добавляем два ствола, которые будут двигаться при стрельбе
                for(int i = 0; i < 2; i++) {
                    int finalI = i;
                    parts.add(new RegionPart("-barrel") {{
                        progress = PartProgress.recoil;
                        recoilIndex = finalI; // Привязываем отдачу к конкретному выстрелу
                        moveY = -3f; // Отдача назад
                        x = (finalI == 0 ? -4f : 4f); // Левый и правый ствол
                        y = 2f;
                        mirror = false;
                        under = true;
                    }});
                }
                // Добавим вращающуюся деталь для красоты
                parts.add(new RegionPart("-rotator") {{
                    progress = PartProgress.warmup;
                    moveRot = 30f;
                    mirror = false;
                }});
            }};

            coolant = consumeCoolant(0.2f);
            limitRange(25f);
        }};







        //AA-segment

        swiftAA = new ItemTurret("swift-aa") {{
            requirements(Category.turret, with(
                    Items.copper, 250,
                    Items.lead, 200,
                    Items.titanium, 150,
                    Items.silicon, 120
            ));

            localizedName = "Swift AA";
            description = "Компактная зенитка с длинными тонкими снарядами. Высокая скорострельность.";

            size = 2;
            health = 900;
            range = 110f;
            reload = 18f;
            shootCone = 5f;                  // Меньше разброс для длинных пуль
            rotateSpeed = 8f;
            targetAir = true;
            targetGround = false;

            shootSound = Sounds.shootScatter;
            shootSoundVolume = 1.4f;

            // ===== ДВА СТВОЛА =====
            shoot = new ShootAlternate(3.5f);
            ((ShootAlternate)shoot).shots = 2;
            ((ShootAlternate)shoot).barrels = 2;

            // ===== ОХЛАДИТЕЛИ (как в salvo) =====
            coolant = consumeCoolant(0.15f);

            // ===== ЭФФЕКТЫ ВЫСТРЕЛА =====
            ammoUseEffect = Fx.casing3;        // Гильзы как у salvo
            shootEffect = Fx.shootBig;
            ammoPerShot = 1;

            consumeAmmoOnce = true;
            consumePower(300f / 60f);

            shootEffect = new MultiEffect(
                    Fx.shootSmall,
                    new Effect(15f, e -> {  // Доп. эффект выстрела
                        Draw.color(e.color);
                        Drawf.tri(e.x, e.y, 2f, 20f * e.fout(), e.rotation);
                    })
            );

            ammo(
                    // 🟢 1. СВИНЕЦ — длинные жёлтые пули
                    Items.lead, new BasicBulletType(7f, 12f) {{
                        width = 3f;               // Тонкая
                        height = 18f;              // Длинная (соотношение 1:6)
                        lifetime = 50f;
                        splashDamage = 55f;
                        splashDamageRadius = 12f;

                        trailEffect = IADraw.plasmaTrailEffect;
                        trailWidth = 1.2f;
                        trailLength = 12;

                        hitEffect = new MultiEffect(
                                Fx.hitBulletColor,
                                Fx.sparkShoot
                        );
                    }},

                    // 🟡 2. МЕТАЛЛОЛОМ — оранжевые с искрами
                    Items.scrap, new BasicBulletType(6.5f, 15f) {{
                        width = 3.5f;
                        height = 20f;
                        lifetime = 55f;
                        splashDamage = 50f;
                        splashDamageRadius = 15f;

                        hitColor = Pal.scrapAmmoBack;
                        backColor = Pal.scrapAmmoBack;
                        frontColor = Pal.scrapAmmoFront;

                        trailEffect = IADraw.phaseTrailEffect;  // Искрящийся трейл
                        trailColor = Pal.scrapAmmoBack;
                        trailWidth = 1.5f;
                        trailLength = 15;

                        fragOnHit = true;
                        fragBullets = 2;
                        fragBullet = new BasicBulletType(4f, 5f) {{
                            width = 2f;
                            height = 8f;
                            lifetime = 20f;
                            trailEffect = IADraw.plasmaTrailEffect;
                            trailWidth = 0.8f;
                            trailLength = 8;
                        }};
                    }},

                    // 🔵 3. КРЕМНИЙ — голубые самонаводящиеся
                    Items.silicon, new BasicBulletType(8f, 18f) {{
                        width = 3f;
                        height = 22f;              // Самые длинные
                        lifetime = 60f;
                        splashDamage = 60f;
                        splashDamageRadius = 18f;
                        homingPower = 0.15f;
                        homingRange = 200f;

                        hitColor = Pal.siliconAmmoBack;
                        backColor = Pal.siliconAmmoBack;
                        frontColor = Pal.siliconAmmoFront;

                        trailEffect = IADraw.phaseTrailEffect;
                        trailColor = Pal.siliconAmmoBack;
                        trailWidth = 1.4f;
                        trailLength = 18;

                        hitEffect = new MultiEffect(
                                Fx.hitBulletColor,
                                Fx.sparkShoot
                        );
                    }}
            );

            drawer = new DrawTurret() {{
                // Длинные тонкие стволы
                parts.add(new RegionPart("-barrel") {{
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                    moveY = -3f;
                    x = -3.5f;
                }});
                parts.add(new RegionPart("-barrel") {{
                    progress = PartProgress.recoil;
                    recoilIndex = 1;
                    moveY = -3f;
                    x = 3.5f;
                }});
            }};

            limitRange(15f);
        }};
        stormAA = new ItemTurret("storm-aa"){{
            requirements(Category.turret, with(
                    Items.copper, 400, Items.titanium, 350,
                    Items.silicon, 300, Items.graphite, 150
            ));

            localizedName = "Storm AA";
            description = "Тяжёлая зенитка с длинными бронебойными снарядами.";

            size = 2;
            health = 1200;
            range = 160f;
            reload = 45f;                    // ~1.3 выстрела/сек на ствол
            shootCone = 3f;
            rotateSpeed = 5f;
            targetAir = true;
            targetGround = false;

            shootSound = InfernoSounds.shootSwift;
            shootSoundVolume = 0.35f;

            shoot = new ShootBarrel(){{
                barrels = new float[]{-4f, 0f, 0f, 0f, 0f, 0f, 4f, 0f, 0f};
                shots = 3;
                shotDelay = 10f;
            }};

            coolant = consumeCoolant(0.2f);
            ammoUseEffect = Fx.casing3;
            consumePower(450f / 60f);

            ammo(
                    // 🟡 ГРАФИТ — пробивание ×2
                    Items.graphite, new BasicBulletType(7.5f, 45f){{
                        width = 4f;
                        height = 24f;
                        lifetime = 65f;

                        pierceCap = 2;

                        splashDamage = 15f;
                        splashDamageRadius = 20f;

                        hitColor = backColor = trailColor = Pal.graphiteAmmoBack;
                        frontColor = Pal.graphiteAmmoFront;
                        trailWidth = 1.8f;
                        trailLength = 20;
                        reloadMultiplier = 0.9f;
                    }},

                    // 🔵 КРЕМНИЙ — самонаведение + пробитие
                    Items.silicon, new BasicBulletType(8f, 75f){{
                        width = 4f;
                        height = 26f;
                        lifetime = 70f;

                        homingPower = 0.2f;
                        homingRange = 250f;


                        hitColor = backColor = trailColor = Pal.siliconAmmoBack;
                        frontColor = Pal.siliconAmmoFront;
                        trailWidth = 2f;
                        trailLength = 22;
                    }},

                    // 🔴 ТОРИЙ — тяжёлое пробитие ×3 + замедление
                    Items.thorium, new BasicBulletType(7f, 75f){{
                        width = 5f;
                        height = 28f;
                        lifetime = 75f;

                        splashDamage = 20f;
                        splashDamageRadius = 26f;

                        hitColor = backColor = trailColor = Pal.thoriumAmmoBack;
                        frontColor = Pal.thoriumAmmoFront;
                        trailWidth = 2.2f;
                        trailLength = 24;
                        reloadMultiplier = 0.8f;

                        hitEffect = new MultiEffect(Fx.hitBulletColor, Fx.shockwave);
                    }}
            );

            drawer = new DrawTurret(){{
                for(int i = 0; i < 3; i++){
                    int fi = i;
                    parts.add(new RegionPart("-barrel"){{
                        progress = PartProgress.recoil;
                        recoilIndex = fi;
                        moveY = -4f;
                        x = -8f + fi * 8f;
                    }});
                }
            }};

            limitRange(20f);
        }};

        tempestAA = new ItemTurret("tempest-aa"){{
            requirements(Category.turret, with(
                    Items.titanium, 250, Items.silicon, 200,
                    Items.thorium, 450, Items.graphite, 360,
                    Items.copper, 900, Items.plastanium, 50
            ));

            localizedName = "Tempest AA";
            description = "Элитная зенитка с жидкостными ускорителями. Разные боеприпасы дают разные эффекты.";

            size = 3;
            health = 1400;
            range = 240f;
            reload = 28f;                    // ~5.7 выстрелов/сек на ствол
            shootCone = 6f;
            rotateSpeed = 5.5f;
            targetAir = true;
            targetGround = false;

            shootSound = InfernoSounds.shoottempestAA;
            shootSoundVolume = 0.3f;

            shoot = new ShootBarrel(){{
                barrels = new float[]{-5f, 4f, 0f, 5f, 4f, 0f, -5f, -4f, 0f, 5f, -4f, 0f};
                shots = 4;
                shotDelay = 7f;
            }};

            consumePower(500f / 60f);
            consumeAmmoOnce = true;
            coolant = consumeCoolant(0.15f);
            coolantMultiplier = 8f;
            ammoUseEffect = Fx.casing3;
            ammoPerShot = 3;

            ammo(
                    // 🟠 ГРАФИТ — Бронебойный трассер (×1.4 урона против брони)
                    Items.graphite, new BasicBulletType(8f, 55f){{
                        width = 5f;
                        height = 26f;
                        lifetime = 62f;

                        // ✅ ПРОБИТИЕ БРОНИ: КОЛОССАЛЬНЫЙ УРОН
                        pierce = true;
                        pierceCap = 2;

                        splashDamage = 15f;
                        splashDamageRadius = 24f;

                        backColor = trailColor = hitColor = Pal.graphiteAmmoBack;
                        frontColor = Pal.graphiteAmmoFront;
                        trailWidth = 2.3f;
                        trailLength = 22;
                        reloadMultiplier = 0.85f;
                    }},

                    // 🔴 ТОРИЙ — Тяжёлый пробивающий (×1.4 урона + замедление)
                    Items.thorium, new BasicBulletType(7.2f, 95f){{
                        width = 6f;
                        height = 28f;
                        lifetime = 68f;

                        // ✅ МАКС. ПРОБИТИЕ: +40% урона
                        pierce = true;
                        pierceCap = 3;           // Макс. пробитие для зенитки!

                        splashDamage = 25f;
                        splashDamageRadius = 28f;

                        backColor = trailColor = hitColor = Pal.thoriumAmmoBack;
                        frontColor = Pal.thoriumAmmoFront;
                        trailWidth = 2.6f;
                        trailLength = 24;
                        trailEffect = Fx.disperseTrail;
                        reloadMultiplier = 0.65f;

                        status = StatusEffects.slow;
                        statusDuration = 60f * 2.5f;
                        hitEffect = new MultiEffect(Fx.hitBulletColor, Fx.shockwave);
                    }},

                    // 🔥 ПИРОТИТ — Огненный шквал (площадной урон)
                    Items.pyratite, new BasicBulletType(6.5f, 45f){{
                        width = 5.5f;
                        height = 25f;
                        lifetime = 72f;

                        // Огонь = меньше прямого урона, но площадь + статус
                        splashDamage = 85f;      // ✅ Большая площадь
                        splashDamageRadius = 35f;

                        backColor = trailColor = hitColor = Pal.lightFlame;
                        frontColor = Pal.darkFlame;
                        trailWidth = 2.5f;
                        trailLength = 26;
                        trailEffect = Fx.fire;

                        status = StatusEffects.burning;
                        statusDuration = 60f * 4f;
                        hitEffect = new MultiEffect(Fx.hitFlameSmall, Fx.blastExplosion);
                    }},

                    // 💥 ВЗРЫВЧАТАЯ СМЕСЬ — Осколочно-фугасный (макс. площадь)
                    Items.blastCompound, new BasicBulletType(6f, 35f){{
                        width = 6.5f;
                        height = 24f;
                        lifetime = 76f;

                        // Меньше прямого урона, но ОГРОМНАЯ площадь + осколки
                        splashDamage = 120f;     // ✅ Макс. площадь для эндгейма
                        splashDamageRadius = 45f;

                        backColor = trailColor = hitColor = Color.valueOf("ff6644");
                        frontColor = Color.white;
                        trailWidth = 3f;
                        trailLength = 28;

                        status = StatusEffects.blasted;
                        statusDuration = 60f * 3f;

                        // ✅ ОСКОЛКИ: 4 штуки по 28 урона
                        fragOnHit = true;
                        fragBullets = 4;
                        fragSpread = 30f;
                        fragRandomSpread = 15f;
                        fragVelocityMin = 0.9f;
                        fragVelocityMax = 1.1f;
                        fragLifeMin = 0.25f;

                        fragBullet = new BasicBulletType(5f, 28f){{
                            width = 4f;
                            height = 18f;
                            lifetime = 32f;
                            splashDamage = 12f;
                            splashDamageRadius = 18f;
                            backColor = trailColor = hitColor = Color.valueOf("ff6644");
                            frontColor = Color.white;
                            trailWidth = 1.6f;
                            trailLength = 12;
                            hitEffect = Fx.flakExplosion;
                            pierceArmor = true;  // Осколки тоже пробивают!
                        }};

                        buildingDamageMultiplier = 0.8f;  // Меньше по зданиям (баланс)
                    }}
            );

            drawer = new DrawTurret(""){{
                parts.add(new RegionPart("-side"){{
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = 1f;
                    moveY = -2f;
                    moveRot = -10f;
                }});
            }};

            liquidCapacity = 80f;
            limitRange(22f);
        }};

        blizzard = new ItemTurret("blizzard"){{
            requirements(Category.turret, with(
                    Items.copper, 200,
                    Items.lead, 150,
                    Items.silicon, 100,
                    Items.titanium, 80,
                    celestine, 60
            ));

            localizedName = "Метель";
            description = "Криогенная батарея. Выпускает ледяные снаряды, которые замораживают врагов. Целестин при сгорании даёт красный оттенок.";

            size = 3;
            health = 1200;
            range = 240f;
            reload = 120f; // 2 секунды между выстрелами
            recoil = 1f;
            shake = 1.5f;
            rotateSpeed = 3f;
            shootCone = 20f;
            shootSound = Sounds.explosionTitan;
            ammoPerShot = 1;


            coolantMultiplier = 6f;

            // --- Потребление ресурсов (мид-гейм) ---
            consumePower(90f / 60f); // 90 power/sec
            consumeLiquid(Liquids.water, 0.08f); // Крио как охладитель

            // --- Визуал в стиле Серпуло (не Эрекир!) ---
            drawer = new DrawTurret(""){{
                // 🔴 Красный цвет целестина при горении (химия!)
                Color heatc = Color.valueOf("ff4444");
                parts.addAll(
                        new RegionPart("-side"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveRot = 15f;
                            x = 10 / 4f;
                            y = -1f / 4f;
                            moveY = 2f / 4f;
                            under = true;
                            heatColor = heatc;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            moveY = -3f / 4f;
                            heatColor = heatc;
                        }}
                );
            }};

            outlineColor = Color.valueOf("262626");
            scaledHealth = 150;

            // --- БОЕПРИПАСЫ ---
            ammo(
                    // 1. Целестин: Ледяной снаряд с КРАСНЫМ свечением (химия!)
                    celestine, new ArtilleryBulletType(2f, 85){{
                        hitEffect = Fx.titanExplosion;
                        despawnEffect = Fx.none;
                        knockback = 1f;
                        lifetime = 90f;
                        height = 12f;
                        width = 10f;
                        splashDamageRadius = 40f;
                        splashDamage = 85f;
                        scaledSplashDamage = true;

                        // 🔴 КРАСНЫЙ цвет (соли стронция горят красным!)
                        backColor = hitColor = trailColor = Color.valueOf("ff4444");
                        frontColor = Color.valueOf("ffaaaa");

                        ammoMultiplier = 1f;
                        hitSound = Sounds.explosion;

                        // ❄️ Эффект заморозки
                        status = StatusEffects.freezing;
                        statusDuration = 60f * 3f; // 3 секунды

                        trailLength = 20;
                        trailWidth = 2f;
                        trailEffect = Fx.vapor;
                        trailInterval = 4f;

                        despawnShake = 3f;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;
                        buildingDamageMultiplier = 0.4f;

                        // Осколки при взрыве
                        fragLifeMin = 1f;
                        fragBullets = 4;
                        fragSpread = 25f;
                        fragBullet = new ArtilleryBulletType(1.5f, 25, "shell"){{
                            hitEffect = Fx.explosion;
                            width = 5f;
                            height = 8f;
                            lifetime = 25f;
                            splashDamageRadius = 15f;
                            splashDamage = 25f;
                            backColor = frontColor = hitColor = Color.valueOf("ff4444");
                            status = StatusEffects.freezing;
                            statusDuration = 60f * 1.5f;
                            buildingDamageMultiplier = 0.3f;
                        }};
                    }},


                    // 3. Титан: Бронебойный лёд (синий)
                    Items.titanium, new ArtilleryBulletType(3f, 95){{
                        hitEffect = Fx.titanExplosion;
                        despawnEffect = Fx.none;
                        knockback = 0.8f;
                        lifetime = 75f;
                        height = 13f;
                        width = 11f;
                        splashDamageRadius = 35f;
                        splashDamage = 95f;
                        scaledSplashDamage = true;

                        backColor = hitColor = trailColor =  Color.valueOf("#ffd37f");
                        frontColor = Color.white;

                        ammoMultiplier = 2f;
                        hitSound = Sounds.explosion;
                        status = StatusEffects.freezing;
                        statusDuration = 60f * 2f;

                        trailLength = 18;
                        trailWidth = 1.8f;
                        trailEffect = Fx.vapor;
                        despawnShake = 2.5f;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;
                        buildingDamageMultiplier = 0.35f;
                    }}
            );

            coolantMultiplier = 5f;
            limitRange();
        }};

        // ██████ IMPULSE TURRET — Мид-гейм сдерживание ██████

// В методе load():
        impulse = new PowerTurret("impulse"){{
            requirements(Category.turret, with(
                    Items.copper, 450,
                    Items.lead, 350,
                    Items.silicon, 280,
                    Items.graphite, 180,
                    Items.titanium, 120
            ));

            localizedName = "Impulse";
            description = "Импульсная турель для сдерживания волн. Выпускает снаряды, которые отталкивают врагов, не нанося урона. Чем дальше летит снаряд, тем больше становится.";

            size = 3;
            health = 1800;
            range = 320f;
            reload = 55f;
            shootCone = 8f;
            rotateSpeed = 4f;
            targetAir = true;
            targetGround = true;

            shootSound = Sounds.shootLancer;
            shootSoundVolume = 1.2f;

            consumePower(180f / 60f);
            coolantMultiplier = 6f;
            Color heatc= Color.valueOf("6ec9ff");

            // ━━━ ВИЗУАЛ ━━━
            drawer = new DrawTurret(""){{


                parts.addAll(
                        new RegionPart("-base"){{
                            progress = PartProgress.constant(1f);
                            under = true;
                            mirror = false;
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            moveY = -4f;
                            heatColor = heatc;
                        }},
                        new RegionPart("-side"){{
                            progress = PartProgress.warmup;
                            mirror = true;
                            x = 8f;
                            y = -4f;
                            moveX = 1f;
                            moveRot = -8f;
                            heatColor = heatc;
                        }}
                );
            }};

            outlineColor = Pal.darkOutline;
            scaledHealth = 200;

            // ━━━ НОВЫЙ ТИП СНАРЯДА ━━━
            shootType = new infernoadvanced.entities.bullet.PushBulletType(5f, 64f){{
                knockbackStrength = 1.2f;
                sizeGrowth = 0.4f;
                maxSize = 35f;
                minSize = 8f;

                pushBackColor = Color.valueOf("6ec9ff");
                pushFrontColor = Color.white;
                pushHitColor = Color.valueOf("6ec9ff");
                pushTrailColor = Color.valueOf("6ec9ff");
                trailWidth = 0;
                trailLength = 0;
            }};

        }};
        singularityCannon = new PowerTurret("singularity-cannon") {{
            requirements(Category.turret, ItemStack.with(
                    Items.surgeAlloy, 800,
                    Items.phaseFabric, 500,
                    Items.thorium, 1000,
                    Items.silicon, 1200,
                    Items.plastanium, 400
            ));

            localizedName = "Singularity Cannon";
            description = "Заряжает сингулярность чистой энергией и выпускает чёрную дыру, проходящую сквозь врагов. Игнорирует броню и искажает пространство.";

            size = 6;
            health = 8500;
            armor = 15;



            // Параметры стрельбы
            reload = 60f*120;
            range = 520f;            // большая дальность
            shootCone = 2f;
            rotateSpeed = 1.2f;
            recoil = 8f;
            shake = 12f;

            // Эффект зарядки
//            chargeEffect = IAFx.chargeSingularity;
//            chargeTime = 60f;        // 1 секунда зарядки
//            chargeColor = Color.valueOf("#ff5533");

            // ПОТРЕБЛЕНИЕ — ТОЛЬКО ЭЛЕКТРИЧЕСТВО
            consumePower(1000f);

            // Звуки
            shootSound = Sounds.shootForeshadow;
            chargeSound = Sounds.chargeVela;
            shootSoundVolume = 2f;
            float chargeSoundVolume = 1.5f;

            // БОЕПРИПАС — ЭНЕРГЕТИЧЕСКАЯ ЧЁРНАЯ ДЫРА
            shootType = new BlackHoleBulletType(0.5f, 20500f) {{
                // БРОНЕБОЙНОСТЬ И ПРОХОД СКВОЗЬ
                pierce = true;
                pierceCap = 15;
                pierceBuilding = true;
                pierceArmor = true;
                despawnHit = false;
                keepVelocity = true;

                lifetime = 60f * 60;

                status = InfernoStatusEffects.gravityCollapse;
                statusDuration = 60f * 12;

            }};

            // ========== ВИЗУАЛ ==========
            drawer = new DrawTurret("singularity-") {{
                // Вращающееся кольцо зарядки
                parts.add(new RegionPart("-charge-ring") {{
                    progress = PartProgress.warmup;
                    moveRot = -360f;
                    rotateSpeed = 2f;
                    under = false;
                    layer = Layer.effect;
                }});

                // Основной ствол
                parts.add(new RegionPart("-barrel") {{
                    progress = PartProgress.recoil;
                    moveY = -8f;
                    heatColor = Color.valueOf("#ff4422");
                }});
            }};

            // Эффекты
            shootEffect = IAFx.shootSingularity;
            smokeEffect = IAFx.shootSingularityAlt;

            outlineColor = Color.valueOf("#2a1a0a");
            heatColor = Color.valueOf("#ff4422");

            limitRange(45f);
        }};







        // В InfernoTurrets.java внутри load()
        // В методе load() добавь спавнер:


        MLRSskyVolley = new ItemTurret("MLRS-sky-volley") {{
                    // ===== Ванильные требования (мид-гейм) =====
                    requirements(Category.turret, with(
                            Items.copper, 300,
                            Items.lead, 250,
                            Items.graphite, 150,
                            Items.silicon, 120,
                            Items.titanium, 150
                    ));

                    localizedName = "MLRS Sky Volley";
                    description = "РСЗО 3×3. Выпускает 3 быстрые ракеты по воздушным целям. Свинец — просто урон, Титан — бронебойный, Пиротит — самонаводится и поджигает.";

                    // ===== Базовые параметры =====
                    size = 3;
                    health = 1100;
                    range = 400f;                    // Дальность для перехвата
                    reload = 80f;                    // ~1.3 сек между залпами (быстрее)
                    shootCone = 35f;                 // Широкий конус
                    rotateSpeed = 4.0f;              // Очень быстрое наведение на воздух
                    recoil = 1.5f;
                    shake = 2.0f;

                    inaccuracy = 0f;                 // Высокая точность
                    targetAir = true;
                    targetGround = false;            // Только воздух
                    predictTarget = true;

                    shootSound = Sounds.shootMissileSmall;
                    shootSoundVolume = 0.5f;

                    ammoPerShot = 12;                 // 3 ракеты за залп
                    maxAmmo = 24;

                    // ===== ЗАЛП ИЗ 3 ТРУБ =====
                    shoot = new ShootBarrel() {{
                        barrels = new float[] {
                                -6f, -4f, 0f,   // левая
                                0f, 0f, 0f,     // центральная
                                6f, -4f, 0f     // правая
                        };
                        shots = 3;
                        shotDelay = 5f;      // Быстрая очередь
                    }};

                    // ===== Потребление =====
                    consumePower(100f / 60f);    // 1.6 ед/сек
                    consumeAmmoOnce = false;

                    // ===== БОЕПРИПАСЫ (Ванильные, только снаряды!) =====
                    ammo(
                            // 🔘 СВИНЕЦ — Базовый, быстрый, без эффектов
                            Items.lead, new BasicBulletType(10f, 35f) {{
                                // ✅ Только hex-цвета!
                                backColor = Color.valueOf("#7a7a8c");
                                frontColor = Color.valueOf("#ffffff");
                                trailColor = Color.valueOf("#7a7a8c");

                                width = 9f;
                                height = 28f;          // ✅ Длинный снаряд
                                sprite = "missile-large";

                                lifetime = 45f;        // Быстрый полёт
                                drag = 0.002f;         // ✅ Почти нет сопротивления (летит далеко и быстро)

                                // ✅ Нет самонаведения, нет пронзания
                                homingPower = 0f;
                                pierce = false;
                                pierceArmor = false;

                                splashDamage = 25f;
                                splashDamageRadius = 35f;

                                trailWidth = 2.5f;
                                trailLength = 25;      // Длинный след
                                trailEffect = Fx.missileTrail;

                                hitEffect = new MultiEffect(
                                        Fx.blastExplosion,
                                        new Effect(20f, e -> {
                                            Draw.color(Color.valueOf("#7a7a8c"));
                                            Lines.stroke(2f * e.fout());
                                            Lines.circle(e.x, e.y, 25f * e.finpow());
                                        })
                                );

                                // ✅ Нет дебаффов
                                status = StatusEffects.none;

                                lightRadius = 30f;
                                lightColor = Color.valueOf("#7a7a8c");
                                lightOpacity = 0.5f;
                            }},

                            // ⚪ ТИТАН — Бронебойный, быстрый, без самонаведения
                            Items.titanium, new BasicBulletType(11f, 55f) {{
                                backColor = Color.valueOf("#8da1e3");
                                frontColor = Color.valueOf("#ffffff");
                                trailColor = Color.valueOf("#8da1e3");

                                width = 9f;
                                height = 30f;          // ✅ Самый длинный
                                sprite = "missile-large";

                                lifetime = 40f;        // Очень быстрый
                                drag = 0.001f;         // ✅ Минимальное сопротивление

                                // ✅ Бронебойный (игнорирует броню), но НЕ пронзает цели
                                homingPower = 0f;      // Нет наведения
                                pierce = false;        // Не проходит сквозь
                                pierceArmor = true;    // ✅ Игнорирует броню врага

                                splashDamage = 35f;
                                splashDamageRadius = 40f;

                                trailWidth = 3f;
                                trailLength = 30;
                                trailEffect = Fx.missileTrail;

                                hitEffect = new MultiEffect(
                                        Fx.blastExplosion,
                                        new WaveEffect() {{
                                            colorFrom = Color.valueOf("#8da1e3");
                                            colorTo = Color.valueOf("#ffffff");
                                            sizeTo = 50f;
                                            lifetime = 15f;
                                            strokeFrom = 3f;
                                        }}
                                );

                                buildingDamageMultiplier = 0.8f; // Хорош против турелей

                                lightRadius = 35f;
                                lightColor = Color.valueOf("#8da1e3");
                                lightOpacity = 0.6f;
                            }},

                            // 🔥 ПИРОТИТ — Самонаводится, поджигает, НЕ пронзает
                            Items.pyratite, new MissileBulletType(9f, 30f) {{
                                backColor = Color.valueOf("#ff6b4a");
                                frontColor = Color.valueOf("#ffcc80");
                                trailColor = Color.valueOf("#ff6b4a");

                                width = 9f;
                                height = 26f;
                                sprite = "missile-large";

                                lifetime = 50f;
                                drag = 0.004f;

                                // ✅ Самонаведение есть
                                homingPower = 0.15f;     // ✅ Агрессивное наведение
                                homingRange = 150f;

                                // ✅ НЕ пронзает
                                pierce = false;
                                pierceArmor = false;

                                splashDamage = 45f;
                                splashDamageRadius = 67f;

                                trailWidth = 3.5f;
                                trailLength = 35;        // 🔥 Огненный след
                                trailEffect = Fx.missileTrail;

                                // ✅ Поджигает врагов
                                status = StatusEffects.burning;
                                statusDuration = 600f;   // 5 секунд горения

                                // ✅ Оставляет огонь при полёте
                                makeFire = true;

                                hitEffect = new MultiEffect(
                                        Fx.blastExplosion,
                                        Fx.fireSmoke,
                                        Fx.ballfire
                                );

                                lightRadius = 40f;
                                lightColor = Color.valueOf("#ff6b4a");
                                lightOpacity = 0.7f;
                            }}
                    );

                    // ===== Визуал =====
                    outlineColor = Color.valueOf("#2a2a3a");
                    heatColor = Color.valueOf("#ff6b4a");

                    drawer = new DrawTurret("") {{
                        parts.add(
                                new RegionPart("-barrel") {{
                                    progress = PartProgress.recoil;
                                    moveY = -1.5f;
                                    mirror = true;
                                }},
                                new RegionPart("-top") {{
                                    progress = PartProgress.warmup;
                                    heatProgress = PartProgress.warmup;
                                    color = Color.valueOf("#4a4a5a");
                                }}
                        );
                    }};

                    // ===== Статы =====
                    researchCostMultiplier = 1.1f;
                    limitRange();
                }};






        MLRShail = new ItemTurret("MLRS-hail") {{
    requirements(Category.turret, with(
            Items.copper, 900,
            Items.lead, 600,
            Items.titanium, 250,
            Items.surgeAlloy, 250,
            Items.thorium, 550,
            Items.plastanium, 200,
            Items.silicon, 900
    ));

    localizedName = "MLRS Hail";
    description = "Тяжелая батарея РСЗО 4x4. Выпускает залп из 40 неуправляемых ракет. Требует нефть для запуска.";



    size = 4;
    health = 3200;
    range = 780f;
    reload = 1800f;
    shootCone = 1f;                   // ⬅️ Убрал разброс (было 10)
    rotateSpeed = 0.7f;
    recoil = 0.4f;
    shake = 4f;

    inaccuracy = 5f;  // Добавить разброс
    shootCone = 11f;  // Увеличить конус стрельбы

    shootSound = InfernoSounds.hailLaunch;
    shootSoundVolume = 0.4f;          // ⬅️ Убавил громкость (было 3.2)

    ammoPerShot = 200;
    maxAmmo = 300;
    targetAir = true;
    targetGround = true;
    predictTarget = true;

    // ===== 8 ТРУБ, КАЖДАЯ ПО 5 ВЫСТРЕЛОВ = 40 РАКЕТ =====
    shoot = new ShootBarrel() {{
        // x, y, rotation (x вдоль пушки, y поперёк)
        // Плотно упаковано для 4x4
        barrels = new float[] {
            // Левый ряд (от -14 до -4)
            -14f, -8f, 0f,   // труба 1, зад
            -14f, 0f, 0f,    // труба 1, центр
            -14f, 8f, 0f,    // труба 1, перед

            -10f, -8f, 0f,   // труба 2, зад
            -10f, 0f, 0f,    // труба 2, центр
            -10f, 8f, 0f,    // труба 2, перед

            -6f, -8f, 0f,    // труба 3, зад
            -6f, 0f, 0f,     // труба 3, центр
            -6f, 8f, 0f,     // труба 3, перед

            -2f, -8f, 0f,    // труба 4, зад
            -2f, 0f, 0f,     // труба 4, центр
            -2f, 8f, 0f,     // труба 4, перед

            // Правый ряд (от 2 до 14)
            2f, -8f, 0f,     // труба 5, зад
            2f, 0f, 0f,      // труба 5, центр
            2f, 8f, 0f,      // труба 5, перед

            6f, -8f, 0f,     // труба 6, зад
            6f, 0f, 0f,      // труба 6, центр
            6f, 8f, 0f,      // труба 6, перед

            10f, -8f, 0f,    // труба 7, зад
            10f, 0f, 0f,     // труба 7, центр
            10f, 8f, 0f,     // труба 7, перед

            14f, -8f, 0f,    // труба 8, зад
            14f, 0f, 0f,     // труба 8, центр
            14f, 8f, 0f,     // труба 8, перед
        };
        shots = 40;                    // 40 ракет за залп!
        shotDelay = 10f;                // Минимальная задержка между выстрелами
    }};

    consumePower(450f / 60f);
    consumeLiquid(Liquids.oil, 0.5f);  // 30 ед/сек нефти

    ammo(
            // ██████ 2. ВЗРЫВЧАТКА (бывший стронций) — ТИТАНОВЫЙ ТИП, СТАТЫ СТРОНЦИЯ ██████
            Items.blastCompound, new BasicBulletType(12f, 0f) {{  // ⬅️ ТИП КАК У ТИТАНА
                shootEffect = Fx.shootBig;
                smokeEffect = Fx.shootSmokeMissile;
                hitColor = Color.valueOf("ff5544");                   // ⬅️ АЛО-ОРАНЖЕВЫЙ
                ammoMultiplier = 1f;

                // Внешний вид (как у титана)
                width = 9f;
                height = 21f;
                sprite = "missile-large";

                // Поведение (как у титана)
                pierce = true;
                pierceCap = 15;
                pierceBuilding = true;
                pierceArmor = true;

                lifetime = 80f;
                drag = 0f;
                keepVelocity = true;

                // Трейл (ало-оранжевый)
                trailColor = Color.valueOf("ff5544");
                trailWidth = 2.8f;
                trailLength = 20;
                trailEffect = Fx.artilleryTrail;
                trailInterval = 3f;

                // Урон (как у стронция)
                splashDamage = 100f;           // ⬅️ СТАТЫ СТРОНЦИЯ
                splashDamageRadius = 80f;
                buildingDamageMultiplier = 0.6f;

                // Статус (как у стронция)
                status = StatusEffects.blasted;
                statusDuration = 1200f;

                // Взрыв (как у титана, но с ало-оранжевым цветом)
                hitEffect = new MultiEffect(
                        Fx.blastExplosion,
                        Fx.shockwave,
                        Fx.smokeAoeCloud,
                        new Effect(30f, 80f, e -> {
                            Draw.color(Color.valueOf("ff5544"));
                            Lines.stroke(3f * e.fout());
                            Lines.circle(e.x, e.y, 30f * e.finpow() + 10f);
                            for(int i = 0; i < 6; i++){
                                float angle = i * 60f + e.rotation + Mathf.randomSeedRange(e.id + i, 20f);
                                float len = 25f * e.finpow() + 10f;
                                Drawf.tri(e.x, e.y, 2f * e.fout(), len, angle);
                            }
                        })
                );

                // ===== 3 БОМБЫ (КАК У СТРОНЦИЯ) =====
                fragOnHit = true;
                fragBullets = 4;
                fragRandomSpread = 15f;
                fragSpread = 20f;
                fragVelocityMin = 0.8f;
                fragVelocityMax = 1.2f;
                fragLifeMin = 0.2f;

                fragBullet = new BombBulletType(5f, 550f) {{
                    width = 8f;
                    height = 12f;
                    lifetime = 40f;
                    splashDamage = 350f;
                    splashDamageRadius = 35f;
                    collidesTiles = true;
                    collidesAir = true;
                    buildingDamageMultiplier = 1f;

                    trailColor = Color.valueOf("ff5544");
                    trailWidth = 2.5f;
                    trailLength = 12;

                    hitEffect = new MultiEffect(
                            Fx.blastExplosion,
                            Fx.shockwave,
                            new Effect(20f, e -> {
                                Draw.color(Color.valueOf("ff5544"));
                                for(int i = 0; i < 4; i++){
                                    float angle = i * 90f + e.rotation;
                                    Drawf.tri(e.x, e.y, 2f, 25f * e.fout(), angle);
                                }
                            })
                    );
                }};

                // Звуки
                hitSound = Sounds.explosion;
                hitSoundVolume = 2f;
                shootSound = InfernoSounds.hailLaunch;
                shootSoundVolume = 0.35f;

                // Оптика
                lightRadius = 40f;
                lightColor = Color.valueOf("ff5544");
                lightOpacity = 0.7f;

                backColor = Color.valueOf("ff5544");
                frontColor = Color.white;
            }},

            // ██████ 3. ТИТАН — Бронебойный снаряд (ЗОЛОТОЙ) ██████
            Items.titanium, new BasicBulletType(12f, 570f) {{
                shootEffect = Fx.shootBig;
                smokeEffect = Fx.shootSmokeMissile;
                hitColor = Color.valueOf("#ffd37f");           // ⬅️ ВЕРНУЛ ЗОЛОТОЙ

                width = 9f;
                height = 21f;
                sprite = "missile";


                pierce = true;
                pierceCap = 15;
                pierceBuilding = true;
                pierceArmor = true;

                lifetime = 80f;
                drag = 0f;
                keepVelocity = true;

                trailColor = Color.valueOf("#ffd37f");
                trailWidth = 2.8f;
                trailLength = 20;
                trailEffect = Fx.artilleryTrail;
                trailInterval = 3f;

                hitEffect = new MultiEffect(
                        Fx.blastExplosion,
                        new WaveEffect() {{
                            colorFrom = Color.valueOf("#ffd37f");
                            colorTo = Color.valueOf("#ffaa55");
                            sizeTo = 50f;
                            lifetime = 15f;
                            strokeFrom = 4f;
                        }}
                );

                buildingDamageMultiplier = 0.9f;

                fragOnHit = true;
                fragBullets = 2;
                fragRandomSpread = 20f;
                fragSpread = 30f;
                fragVelocityMin = 0.8f;
                fragVelocityMax = 1.2f;
                fragLifeMin = 0.2f;

                fragBullet = new BasicBulletType(5f, 100f) {{
                    width = 5f;
                    height = 8f;
                    lifetime = 25f;
                    splashDamage = 130f;
                    splashDamageRadius = 20f;
                    buildingDamageMultiplier = 0.5f;
                    trailColor = Color.valueOf("#ffd37f");
                    trailWidth = 1.5f;
                    trailLength = 8;
                    backColor = Color.valueOf("#ffd37f");
                    frontColor = Color.white;
                    hitEffect = Fx.explosion;
                }};

                hitSound = Sounds.explosion;
                hitSoundVolume = 2f;
                shootSound = InfernoSounds.hailLaunch;
                shootSoundVolume = 0.35f;

                lightRadius = 40f;
                lightColor = Color.valueOf("#ffd37f");
                lightOpacity = 0.7f;

                backColor = Color.valueOf("#ffd37f");
                frontColor = Color.white;
            }}
    );


    limitRange();
}};


        cataclysm = new ItemTurret("cataclysm"){{
            requirements(Category.turret, with(
                    Items.copper, 1200,
                    Items.silicon, 600,
                    Items.graphite, 500,
                    Items.titanium, 400,
                    Items.surgeAlloy, 800,
                    Items.phaseFabric, 500
            ));

            localizedName = "Cataclysm";
            description = "Эндгейм батарея РСЗО. Выпускает каскад ракет с дробящимися боеголовками. Требует криофлюид для охлаждения.";

            size = 5;
            health = 6000;
            range = 1200f;
            reload = 980f;
            recoil = 1f;
            shake = 8f;
            rotateSpeed = 0.6f;
            shootCone = 3f;
            shootSound = Sounds.shootScathe;
            ammoPerShot = 10;
            maxAmmo = 30;
            targetAir = true;
            targetGround = true;
            predictTarget = false;
            minWarmup = 0.9f;
            shootWarmupSpeed = 0.02f;
            warmupMaintainTime = 180f;

            coolantMultiplier = 20f;
            consumePower(600f / 60f);

            drawer = new DrawTurret(""){{
                Color heatc = Color.valueOf("ff4400");
                Color heatc2 = Color.valueOf("ffd37f");
                Color phaseColor = Color.valueOf("ffd37f");

                parts.addAll(
                        new RegionPart("-base"){{
                            progress = PartProgress.constant(1f);
                            under = true;
                            mirror = false;
                        }},
                        new RegionPart("-blade"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.heat;
                            heatColor = heatc;
                            moveRot = -30f;
                            moveX = 0f;
                            moveY = -8f;
                            mirror = true;
                            children.add(new RegionPart("-side"){{
                                progress = PartProgress.warmup.delay(0.5f);
                                heatProgress = PartProgress.recoil;
                                heatColor = heatc;
                                mirror = true;
                                under = false;
                                moveY = -6f;
                                moveX = 3f;
                                moves.add(new PartMove(PartProgress.recoil, 3f, 10f, -50f));
                            }});
                        }},
                        new RegionPart("-barrel"){{
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(10f, 0.3f) * p.warmup);
                            mirror = false;
                            under = true;
                            moveY = -10f;
                            heatColor = heatc2;
                        }},
                        new RegionPart("-core"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.heat;
                            color = Color.valueOf("00aaff");
                            colorTo = Color.valueOf("ffd37f");
                            mirror = false;
                            under = true;
                            moves.add(new PartMove(PartProgress.warmup, 0f, -3f, 0f));
                        }},
                        new ShapePart(){{
                            progress = PartProgress.warmup;
                            color = phaseColor;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 5f;
                            radius = 25f;
                            layer = Layer.effect;
                            y = -12f;
                        }},
                        new ShapePart(){{
                            progress = PartProgress.warmup.delay(0.3f);
                            color = Color.white;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 4f;
                            radius = 18f;
                            layer = Layer.effect;
                            y = -12f;
                            rotateSpeed = 3f;
                        }},
                        new ShapePart(){{
                            progress = PartProgress.warmup.delay(0.6f);
                            color = phaseColor;
                            circle = false;
                            sides = 6;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 3.5f;
                            radius = 12f;
                            layer = Layer.effect;
                            y = -12f;
                            rotateSpeed = -5f;
                        }},
                        new HaloPart(){{
                            progress = PartProgress.heat;
                            color = phaseColor;
                            layer = Layer.effect;
                            y = 30f;
                            haloRadius = 30f;
                            triLength = 0f;
                            triLengthTo = 25f;
                            shapes = 12;
                            tri = true;
                        }}
                );
            }};

            outlineColor = Color.valueOf("262626");
            scaledHealth = 280;
            envEnabled |= Env.space;
            coolantMultiplier = 20f;
            fogRadiusMultiplier = 0.3f;
            unitSort = UnitSorts.strongest;
            newTargetInterval = 50f;

            // ===== БОЕПРИПАСЫ =====
            ammo(

                    // ██████ СТРОНЦИЙ — Красный, мощный, с оригинальным трейлом ██████
                    InfernoItems.strontium, new BulletType(0f, 0f){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootSmokeMissileColor;
                        hitColor = Color.valueOf("ff6644");
                        ammoMultiplier = 1.5f;
                        reloadMultiplier = 0.9f;

                        spawnUnit = new MissileUnitType("cataclysm-missile-strontium"){{
                            speed = 4.5f;
                            maxRange = 150f;
                            lifetime = 60f * 5f;
                            hitSize = 12f;
                            outlineColor = Pal.darkOutline;
                            engineColor = trailColor = Color.valueOf("ff6644");
                            engineLayer = Layer.effect;
                            engineSize = 3.5f;
                            engineOffset = 11f;
                            rotateSpeed = 0.18f;
                            trailLength = 22;
                            missileAccelTime = 60f;
                            lowAltitude = true;
                            loopSound = Sounds.loopMissileTrail;
                            loopSoundVolume = 0.7f;
                            deathSound = Sounds.explosionMissile;
                            targetAir = true;
                            targetGround = true;
                            fogRadius = 7f;
                            health = 1500;
                            status = StatusEffects.melting;
                            statusDuration = 60f * 6f;

                            weapons.add(new Weapon(){{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                deathExplosionEffect = Fx.massiveExplosion;
                                shootOnDeath = true;
                                shake = 15f;
                                bullet = new ExplosionBulletType(8200f, 120f){{
                                    hitColor = Color.valueOf("ff6644");
                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosion, Fx.scatheLight, new WaveEffect(){{
                                        lifetime = 15f;
                                        strokeFrom = 6f;
                                        sizeTo = 200f;
                                        colorTo = Color.valueOf("ff6644");
                                    }});
                                    collidesAir = true;
                                    collidesGround = true;
                                    buildingDamageMultiplier = 0.1f;
                                    status = StatusEffects.blasted;
                                    statusDuration = 60f * 5f;

                                    fragLifeMin = 0.12f;
                                    fragBullets = 12;
                                    fragRandomSpread = 10f;
                                    fragSpread = 30f;
                                    fragBullet = new ArtilleryBulletType(4.5f, 80){{
                                        buildingDamageMultiplier = 0.15f;
                                        drag = 0.028f;
                                        hitEffect = Fx.massiveExplosion;
                                        despawnEffect = Fx.scatheSlash;
                                        knockback = 1.5f;
                                        lifetime = 35f;
                                        width = height = 24f;
                                        collidesTiles = false;
                                        splashDamageRadius = 70f;
                                        splashDamage = 180f;
                                        backColor = trailColor = hitColor = Color.valueOf("ff6644");
                                        frontColor = Color.white;
                                        smokeEffect = Fx.shootBigSmoke2;
                                        despawnShake = 12f;
                                        lightRadius = 50f;
                                        lightColor = Color.valueOf("ff6644");
                                        lightOpacity = 0.8f;
                                        trailLength = 28;
                                        trailWidth = 5f;
                                        status = StatusEffects.melting;
                                        statusDuration = 60f * 4f;
                                    }};
                                }};
                            }});

                            abilities.add(new MoveEffectAbility(){{
                                effect = Fx.missileTrailSmoke;  // ОРИГИНАЛЬНЫЙ ТРЕЙЛ
                                rotation = 180f;
                                y = -10f;
                                color = Color.grays(0.5f).lerp(Color.valueOf("ff6644"), 0.6f).a(0.5f);
                                interval = 6f;
                            }});
                        }};
                    }},

                    // ██████ СПЛАВ ВОЛНЫ — Двухступенчатое разделение + НОВЫЙ ТРЕЙЛ ██████
                    Items.surgeAlloy, new BulletType(0f, 0f){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootSmokeMissileColor;
                        hitColor = Color.valueOf("#ffd37f");
                        ammoMultiplier = 1f;
                        reloadMultiplier = 0.85f;

                        spawnUnit = new MissileUnitType("cataclysm-missile-surge"){{
                            speed = 11f;
                            maxRange = 150f;
                            lifetime = 60f*2f;
                            hitSize = 12f;
                            outlineColor = Pal.darkOutline;
                            engineColor = trailColor = Color.valueOf("#ffd37f");
                            engineLayer = Layer.effect;
                            engineSize = 3.5f;
                            engineOffset = 11f;
                            rotateSpeed = 0.22f;
                            trailLength = 44;
                            missileAccelTime = 45f;
                            lowAltitude = true;
                            loopSound = Sounds.loopMissileTrail;
                            loopSoundVolume = 0.7f;
                            deathSound = Sounds.explosionMissile;
                            targetAir = true;
                            targetGround = true;
                            fogRadius = 7f;
                            health = 1850;

                            weapons.add(new Weapon(){{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                deathExplosionEffect = Fx.massiveExplosion;
                                shootOnDeath = true;
                                shake = 12f;
                                bullet = new ExplosionBulletType(2200f, 70f){{
                                    hitColor = Color.valueOf("#ffd37f");
                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosionSmall);
                                    collidesAir = true;
                                    collidesGround = true;
                                    buildingDamageMultiplier = 0.15f;

                                    lightning = 12;
                                    lightningDamage = 55f;
                                    lightningLength = 15;
                                    lightningCone = 360f;

                                    fragLifeMin = 0.1f;
                                    fragBullets = 6;
                                    fragRandomSpread = 0f;
                                    fragSpread = 10f;
                                    fragBullet = new BulletType(){{
                                        shootEffect = Fx.shootBig;
                                        smokeEffect = Fx.shootSmokeMissileColor;
                                        hitColor = Color.valueOf("#ffd37f");
                                        ammoMultiplier = 1f;

                                        spawnUnit = new MissileUnitType("cataclysm-missile-surge-split"){{
                                            speed = 20f;
                                            maxRange = 150f;
                                            lifetime = 45f;
                                            outlineColor = Pal.darkOutline;
                                            engineColor = trailColor = Color.valueOf("#ffd37f");
                                            engineLayer = Layer.effect;
                                            engineSize = 2.5f;
                                            engineOffset = 9f;
                                            rotateSpeed = 1.2f;
                                            trailLength = 15;
                                            lowAltitude = true;
                                            loopSound = Sounds.loopMissileTrail;
                                            loopSoundVolume = 0.6f;
                                            deathSound = Sounds.explosionMissile;
                                            targetAir = true;
                                            targetGround = true;
                                            fogRadius = 6f;
                                            health = 80;

                                            weapons.add(new Weapon(){{
                                                shootCone = 360f;
                                                mirror = false;
                                                reload = 1f;
                                                deathExplosionEffect = Fx.massiveExplosion;
                                                shootOnDeath = true;
                                                shake = 10f;
                                                bullet = new ExplosionBulletType(220f, 45f){{
                                                    lightning = 5;
                                                    lightningDamage = 30f;
                                                    lightningLength = 8;
                                                    hitColor = Color.valueOf("#ffd37f");
                                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosionSmall, Fx.scatheLightSmall, new WaveEffect(){{
                                                        lifetime = 10f;
                                                        strokeFrom = 4f;
                                                        sizeTo = 110f;
                                                    }});
                                                    collidesAir = true;
                                                    collidesGround = true;
                                                    buildingDamageMultiplier = 0.15f;

                                                    fragLifeMin = 0.08f;
                                                    fragBullets = 2;
                                                    fragSpread = 45f;
                                                    fragBullet = new BulletType(3f, 35){{
                                                        float width = 10f;
                                                        float height = 10f;
                                                        lifetime = 20f;
                                                        splashDamage = 40f;
                                                        splashDamageRadius = 30f;
                                                        Color backColor = hitColor = Color.valueOf("#ffd37f");
                                                        lightning = 2;
                                                        lightningDamage = 15f;
                                                        lightningLength = 5;
                                                    }};
                                                }};
                                            }});

                                            abilities.add(new MoveEffectAbility(){{
                                                effect = IADraw.phaseTrailEffect;  // ✨ НОВЫЙ ТРЕЙЛ
                                                rotation = 180f;
                                                y = -9f;
                                                color = Color.valueOf("#ffd37f");
                                                interval = 3f;
                                            }});
                                        }};
                                    }};
                                }};
                            }});

                            abilities.add(new MoveEffectAbility(){{
                                effect = IADraw.phaseTrailEffect;  // ✨ НОВЫЙ ТРЕЙЛ
                                rotation = 180f;
                                y = -10f;
                                color = Color.valueOf("#ffd37f");
                                interval = 4f;
                            }});
                        }};
                    }},

                    // ██████ ФАЗОВАЯ ТКАНЬ — Залп из 8 ракет + ШОК + НОВЫЙ ТРЕЙЛ ██████
                    Items.phaseFabric, new FlakBulletType(2.2f, 450f){{
                        width = 26f;
                        height = 34f;
                        lifetime = 200f;
                        ammoMultiplier = 3f;

                        weaveMag = 4f;
                        weaveScale = 5f;

                        trailEffect = IADraw.phaseTrailEffect;  // ✨ НОВЫЙ ТРЕЙЛ
                        trailInterval = 2.5f;

                        backColor = trailColor = hitColor = Color.valueOf("ffd37f");
                        frontColor = Color.white;
                        lightRadius = 180f;
                        lightColor = Color.valueOf("ffd37f");
                        lightOpacity = 0.7f;

                        collidesGround = true;
                        collidesAir = true;
                        splashDamageRadius = 60f;
                        splashDamage = 450f;
                        knockback = 1.2f;
                        buildingDamageMultiplier = 1f;

                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootSmokeMissileColor;
                        hitEffect = new MultiEffect(
                                IADraw.phaseExplosionEffect,     // ✨ НОВЫЙ ВЗРЫВ
                                IADraw.shockHitEffect            // ✨ ЭФФЕКТ ШОКА
                        );
                        despawnEffect = Fx.scatheSlash;
                        despawnShake = 8f;

                        status = InfernoStatusEffects.shocked;
                        statusDuration = 60f * 4f;

                        fragBullets = 8;
                        fragSpread = 35f;
                        fragLifeMin = 0.1f;
                        fragBullet = new FlakBulletType(4.2f, 50f){{
                            width = 18f;
                            height = 24f;
                            lifetime = 45f;

                            weaveMag = 2f;
                            weaveScale = 6f;

                            trailEffect = IADraw.phaseTrailEffect;  // ✨ ТРЕЙЛ У ОСКОЛКОВ
                            trailInterval = 2f;

                            splashDamage = 50f;
                            splashDamageRadius = 32f;
                            buildingDamageMultiplier = 1f;

                            backColor = trailColor = hitColor = Color.valueOf("ffd37f");
                            frontColor = Color.white;

                            hitEffect = new MultiEffect(
                                    Fx.blastExplosion,
                                    IADraw.shockHitEffect        // ✨ ШОК У ОСКОЛКОВ
                            );
                            despawnEffect = Fx.scatheSlash;
                            collidesGround = true;
                            collidesAir = true;
                            hitSound = Sounds.explosionArtillery;

                            status = InfernoStatusEffects.shocked;
                            statusDuration = 60f * 2f;
                        }};
                    }}
            );

            limitRange();
        }};







//█████████████████████████turrets-erekir█████████████████████████
        phoenix = new ItemTurret("phoenix"){{
            requirements(Category.turret, with(
                    Items.beryllium, 300,
                    Items.silicon, 350,
                    Items.graphite, 300,
                    Items.tungsten, 250,
                    Items.surgeAlloy, 150,
                    Items.carbide, 120
            ));

            localizedName = "Феникс";
            description = "Термоядерная турель Эрекира. Нагревается при работе: больше тепла = больше урона и радиуса, но медленнее стрельба. Требует воду для охлаждения.";

            size = 4;
            health = 5200;
            range = 960f;
            reload = 55f;
            recoil = 1.5f;
            shake = 4f;
            rotateSpeed = 1.2f;
            shootCone = 15f;
            shootSound = Sounds.shootScathe;
            ammoPerShot = 3;
            maxAmmo = 45;
            targetAir = true;
            targetGround = true;
            predictTarget = true;
            minWarmup = 0.85f;
            shootWarmupSpeed = 0.04f;
            warmupMaintainTime = 150f;


            coolantMultiplier = 20f;

            // --- Потребление ресурсов ---
            consumePower(150f / 60f); // 150 power/sec
            consumeLiquid(Liquids.water, 0.3f); // Вода для охлаждения

            // --- МЕХАНИКА НАГРЕВА (как у твоего реактора) ---
            heatColor = Color.valueOf("ffaa00"); // Золотой нагрев
            heatRequirement = 120f; // Макс. нагрев
            maxHeatEfficiency = 2.0f; // +100% урона при макс. нагреве

            // --- Визуал в стиле Эрекира (reinforced) ---
            drawer = new DrawTurret("reinforced-"){{
                Color heatc = Color.valueOf("ffaa00"); // Золотой
                Color heatc2 = Color.valueOf("ff6600"); // Оранжевый
                parts.addAll(
                        // ██████ ЗОЛОТЫЕ КРЫЛЬЯ ПРИ ВЫСТРЕЛЕ ██████
                        new ShapePart(){{
                            progress = PartProgress.recoil.delay(0.1f);
                            color = heatc;
                            circle = false;
                            sides = 3;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 3f;
                            radius = 14f;
                            layer = Layer.effect;
                            y = -8f;
                            rotateSpeed = 0f;
                            rotation = 90f;
                            float scaleX = 0.3f;
                            float scaleY = 1f;
                        }},

                        new ShapePart(){{
                            progress = PartProgress.recoil.delay(0.2f);
                            color = heatc2;
                            circle = false;
                            sides = 3;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2.5f;
                            radius = 10f;
                            layer = Layer.effect;
                            y = -8f;
                            rotateSpeed = 0f;
                            rotation = 90f;
                            float scaleX = 0.3f;
                            float scaleY = 1f;
                        }},
                        // Левое крыло
                        new RegionPart("-wing-l"){{
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.heat;
                            heatColor = heatc;
                            mirror = false;
                            under = true;
                            moveX = -6f;
                            moveY = -4f;
                            moveRot = -25f;
                            moves.add(new PartMove(PartProgress.recoil, -2f, -6f, -15f));
                        }},
                        // Правое крыло
                        new RegionPart("-wing-r"){{
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.heat;
                            heatColor = heatc;
                            mirror = false;
                            under = true;
                            moveX = 6f;
                            moveY = -4f;
                            moveRot = 25f;
                            moves.add(new PartMove(PartProgress.recoil, 2f, -6f, 15f));
                        }},
                        // Центральная часть
                        new RegionPart("-blade"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup;
                            heatColor = heatc;
                            moveRot = -20f;
                            moveX = 0f;
                            moveY = -5f;
                            mirror = true;
                            children.add(new RegionPart("-side"){{
                                progress = PartProgress.warmup.delay(0.5f);
                                heatProgress = PartProgress.recoil;
                                heatColor = heatc2;
                                mirror = true;
                                under = false;
                                moveY = -4f;
                                moveX = 1.5f;
                                moves.add(new PartMove(PartProgress.recoil, 1f, 6f, -35f));
                            }});
                        }},
                        // Ядро с нагревом
                        new RegionPart("-core"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.heat;
                            color = Color.valueOf("ffaa00");
                            colorTo = Color.valueOf("ff4444");
                            mirror = false;
                            under = true;
                            moves.add(new PartMove(PartProgress.warmup, 0f, -2f, 0f));
                        }},
                        // Свечение при нагреве (вместо кругов)
                        new ShapePart(){{
                            progress = PartProgress.heat;
                            color = heatc;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2.5f;
                            radius = 10f;
                            layer = Layer.effect;
                            y = -8f;
                        }},
                        new ShapePart(){{
                            progress = PartProgress.heat.delay(0.4f);
                            color = heatc2;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 6f;
                            layer = Layer.effect;
                            y = -8f;
                            rotateSpeed = 1.5f;
                        }}
                );
            }};

            outlineColor = Color.valueOf("262626");
            scaledHealth = 280;
            envEnabled |= Env.space;
            coolantMultiplier = 12f;
            fogRadiusMultiplier = 0.5f;
            newTargetInterval = 45f;
            unitSort = UnitSorts.strongest;

            // --- БОЕПРИПАСЫ ---
            ammo(
                    // ██████ 1. КАРБИД — Огненный шторм (базовый) ██████
                    Items.carbide, new BulletType(0f, 0f){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootSmokeMissileColor;
                        hitColor = Color.valueOf("ab8ec5"); // Фиолетовый карбид
                        ammoMultiplier = 1f;

                        spawnUnit = new MissileUnitType("phoenix-missile-carbide"){{
                            speed = 4.2f;
                            maxRange = 60f; // 480 / 8
                            lifetime = 60f * 5f;
                            hitSize = 10f;
                            outlineColor = Color.valueOf("262626");
                            engineColor = trailColor = Color.valueOf("ab8ec5");
                            engineLayer = Layer.effect;
                            engineSize = 3f;
                            engineOffset = 10f;
                            rotateSpeed = 0.22f;
                            trailLength = 20;
                            missileAccelTime = 45f;
                            lowAltitude = true;
                            loopSound = Sounds.loopMissileTrail;
                            loopSoundVolume = 0.6f;
                            deathSound = Sounds.explosionMissile;
                            targetAir = true;
                            targetGround = true;
                            fogRadius = 6f;
                            health = 280;

                            weapons.add(new Weapon(){{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                deathExplosionEffect = Fx.massiveExplosion;
                                shootOnDeath = true;
                                shake = 10f;
                                bullet = new ExplosionBulletType(950f, 60f){{
                                    hitColor = Color.valueOf("ab8ec5");
                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosion, Fx.scatheLight, new WaveEffect(){{
                                        lifetime = 10f;
                                        strokeFrom = 4f;
                                        sizeTo = 120f;
                                        colorTo = Color.valueOf("ab8ec5");
                                    }});
                                    collidesAir = true;
                                    collidesGround = true;
                                    buildingDamageMultiplier = 0.12f;
                                    status = StatusEffects.blasted;
                                    statusDuration = 60f * 4f;

                                    // Дробящиеся осколки
                                    fragLifeMin = 0.12f;
                                    fragBullets = 6;
                                    fragRandomSpread = 10f;
                                    fragSpread = 25f;
                                    fragBullet = new ArtilleryBulletType(3.2f, 38){{
                                        buildingDamageMultiplier = 0.15f;
                                        drag = 0.022f;
                                        hitEffect = Fx.massiveExplosion;
                                        despawnEffect = Fx.scatheSlash;
                                        knockback = 0.7f;
                                        lifetime = 25f;
                                        width = height = 16f;
                                        collidesTiles = false;
                                        splashDamageRadius = 38f;
                                        splashDamage = 90f;
                                        backColor = trailColor = hitColor = Color.valueOf("ab8ec5");
                                        frontColor = Color.white;
                                        smokeEffect = Fx.shootBigSmoke2;
                                        despawnShake = 6f;
                                        lightRadius = 28f;
                                        lightColor = Color.valueOf("ab8ec5");
                                        lightOpacity = 0.5f;
                                        trailLength = 18;
                                        trailWidth = 3.2f;
                                        status = StatusEffects.blasted;
                                        statusDuration = 60f * 2f;
                                    }};
                                }};
                            }});

                            abilities.add(new MoveEffectAbility(){{
                                effect = Fx.missileTrailSmoke;
                                rotation = 180f;
                                y = -9f;
                                color = Color.grays(0.6f).lerp(Color.valueOf("ab8ec5"), 0.5f).a(0.4f);
                                interval = 6f;
                            }});
                        }};
                    }},

                    // ██████ 2. СПЛАВ ВОЛНЫ — Электрический феникс (быстрый) ██████
                    Items.surgeAlloy, new BulletType(0f, 0f){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootSmokeMissileColor;
                        hitColor =  Color.valueOf("#ffd37f"); // Жёлтый сплав
                        ammoMultiplier = 1.5f;
                        reloadMultiplier = 0.85f;

                        spawnUnit = new MissileUnitType("phoenix-missile-surge"){{
                            speed = 5f;
                            maxRange = 60f;
                            lifetime = 60f * 3.5f;
                            hitSize = 10f;
                            outlineColor = Color.valueOf("262626");
                            engineColor = trailColor =  Color.valueOf("#ffd37f");
                            engineLayer = Layer.effect;
                            engineSize = 3f;
                            engineOffset = 10f;
                            rotateSpeed = 0.28f;
                            trailLength = 20;
                            missileAccelTime = 35f;
                            lowAltitude = true;
                            loopSound = Sounds.loopMissileTrail;
                            loopSoundVolume = 0.6f;
                            deathSound = Sounds.explosionMissile;
                            targetAir = true;
                            targetGround = true;
                            fogRadius = 6f;
                            health = 320;

                            weapons.add(new Weapon(){{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                deathExplosionEffect = Fx.massiveExplosion;
                                shootOnDeath = true;
                                shake = 12f;
                                bullet = new ExplosionBulletType(1600f, 50f){{
                                    hitColor =  Color.valueOf("#ffd37f");
                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosionSmall, Fx.scatheLight);
                                    collidesAir = true;
                                    collidesGround = true;
                                    buildingDamageMultiplier = 0.1f;

                                    // ⚡ МОЛНИИ! ⚡
                                    lightning = 10;
                                    lightningDamage = 50f;
                                    lightningLength = 14;
                                    lightningCone = 360f;

                                    fragLifeMin = 0.1f;
                                    fragBullets = 5;
                                    fragRandomSpread = 0f;
                                    fragSpread = 20f;
                                    fragBullet = new BulletType(){{
                                        shootEffect = Fx.shootBig;
                                        smokeEffect = Fx.shootSmokeMissileColor;
                                        hitColor =  Color.valueOf("#ffd37f");
                                        ammoMultiplier = 1f;

                                        spawnUnit = new MissileUnitType("phoenix-missile-surge-split"){{
                                            speed = 5.5f;
                                            maxRange = 60f;
                                            lifetime = 60f * 2.5f;
                                            outlineColor = Color.valueOf("262626");
                                            engineColor = trailColor =  Color.valueOf("#ffd37f");
                                            engineLayer = Layer.effect;
                                            engineSize = 2.3f;
                                            engineOffset = 8f;
                                            rotateSpeed = 1.3f;
                                            trailLength = 14;
                                            lowAltitude = true;
                                            loopSound = Sounds.loopMissileTrail;
                                            loopSoundVolume = 0.55f;
                                            deathSound = Sounds.explosionMissile;
                                            targetAir = true;
                                            targetGround = true;
                                            fogRadius = 5f;
                                            health = 60;

                                            weapons.add(new Weapon(){{
                                                shootCone = 360f;
                                                mirror = false;
                                                reload = 1f;
                                                deathExplosionEffect = Fx.massiveExplosion;
                                                shootOnDeath = true;
                                                shake = 8f;
                                                bullet = new ExplosionBulletType(200f, 38f){{
                                                    lightning = 5;
                                                    lightningDamage = 28f;
                                                    lightningLength = 7;
                                                    hitColor =  Color.valueOf("#ffd37f");
                                                    shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosionSmall, Fx.scatheLightSmall, new WaveEffect(){{
                                                        lifetime = 10f;
                                                        strokeFrom = 4f;
                                                        sizeTo = 95f;
                                                        colorTo =  Color.valueOf("#ffd37f");
                                                    }});
                                                    collidesAir = true;
                                                    collidesGround = true;
                                                    buildingDamageMultiplier = 0.12f;
                                                }};
                                            }});

                                            abilities.add(new MoveEffectAbility(){{
                                                effect = Fx.missileTrailSmokeSmall;
                                                rotation = 180f;
                                                y = -9f;
                                                color = Color.grays(0.6f).lerp( Color.valueOf("#ffd37f"), 0.5f).a(0.4f);
                                                interval = 5f;
                                            }});
                                        }};
                                    }};
                                }};
                            }});

                            abilities.add(new MoveEffectAbility(){{
                                effect = Fx.missileTrailSmoke;
                                rotation = 180f;
                                y = -9f;
                                color = Color.grays(0.6f).lerp( Color.valueOf("#ffd37f"), 0.5f).a(0.4f);
                                interval = 6f;
                            }});
                        }};
                    }}
            );

            limitRange();
        }};











// Это наша мега-пушка. Наследуемся от ItemTurret, так как она будет потреблять предметы
// (например, сверх-топливо или фазовую ткань).

// Где-то в методе load() после создания всех блоков:
        archimedes = new ItemTurret("archimedes") {{
            // --- Основные характеристики ---
            requirements(Category.turret, with(
                    Items.surgeAlloy, 2000,
                    Items.carbide, 1500,
                    Items.phaseFabric, 1200,
                    Items.oxide, 800
            ));

            size = 5;
            health = 15000;
            armor = 30f;
            scaledHealth = 300;

            // --- Параметры стрельбы ---
            reload = 500f;
            range = 800f;
            shootCone = 1f;
            inaccuracy = 0f;
            recoil = 15f;
            shake = 10f;
            shootSound = Sounds.explosionArtilleryShockBig;
            shootSoundVolume = 2f;

            coolantMultiplier = 15f;

            // --- Механики и потребление ---
            coolantMultiplier = 5f;
            consumePower(25f);
            ammoPerShot = 10;
            maxAmmo = 100;

            // --- БОЕПРИПАСЫ ---
            ammo(
                    // ██████ КАРБИД — Фазовая стрела (уникальная механика) ██████
                    Items.carbide, new BasicBulletType(10f, 100f) {{
                        // ━━━ ВИЗУАЛ "СТРЕЛЫ" ━━━
                        sprite = "bullet";
                        width = 8f;
                        height = 35f; // Длинная форма
                        shrinkX = 0.3f; // Сужается к хвосту
                        shrinkY = 0.1f;

                        // Наконечник (передняя часть)
                        frontColor = Color.valueOf("ab8ec5"); // Фиолетовый карбид
                        backColor = Color.valueOf("6a5a8c"); // Тёмный хвост
                        trailColor = Color.valueOf("ab8ec5");

                        // Трейл как "древко" стрелы
                        trailWidth = 3f;
                        trailLength = 20;
                        trailEffect = new Effect(30f, e -> {
                            Draw.color(e.color);
                            Drawf.tri(e.x, e.y, e.fout() * 4f, 15f * e.fout(), e.rotation + 180f);
                        });

                        // ━━━ ПОВЕДЕНИЕ ━━━
                        lifetime = 100f;
                        pierce = true;
                        pierceCap = 10;
                        pierceBuilding = true;
                        knockback = 3f;


                        // При пробитии цели выпускает "осколки реальности"
                        fragOnHit = true;
                        fragBullets = 4;
                        fragSpread = 20f;
                        fragBullet = new BasicBulletType(8f, 200) {{
                            width = 5f;
                            height = 12f;
                            lifetime = 25f;
                            pierce = true;
                            pierceCap = 3;
                            backColor = trailColor = hitColor = Color.valueOf("ab8ec5");
                            frontColor = Color.white;
                            trailWidth = 1.5f;
                            status = StatusEffects.corroded;
                            statusDuration = 60f * 4f;
                        }};

                        // ━━━ УРОН И ЭФФЕКТЫ ━━━
                        splashDamage = 1200f;
                        splashDamageRadius = 120f;
                        buildingDamageMultiplier = 0.4f;

                        status = InfernoStatusEffects.radioactive;
                        statusDuration = 60f * 6f;

                        hitEffect = new MultiEffect(
                                Fx.titanExplosionSmall,
                                new WaveEffect() {{
                                    colorFrom = Color.valueOf("ab8ec5");
                                    colorTo = Color.valueOf("6a5a8c");
                                    sizeTo = 100f;
                                    lifetime = 20f;
                                    strokeFrom = 5f;
                                }},
                                new Effect(40f, e -> {
                                    Draw.color(Color.valueOf("ab8ec5"));
                                    for(int i = 0; i < 6; i++) {
                                        float angle = i * 60f + e.rotation;
                                        Drawf.tri(e.x, e.y, e.fout() * 6f, 25f * e.fout(), angle);
                                    }
                                })
                        );
                        despawnEffect = hitEffect;
                        hitSound = Sounds.explosionArtilleryShock;
                    }},

                    // ██████ СПЛАВ ВОЛНЫ — Электрический колосс (альтернатива) ██████
                    Items.surgeAlloy, new BasicBulletType(12f, 1800f) {{
                        sprite = "missile-large";
                        width = 25f;
                        height = 40f;
                        hitSize = 15f;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;
                        trailEffect = Fx.missileTrail;
                        trailColor = Color.valueOf("#ffd37f");
                        trailWidth = 8f;
                        trailLength = 25;

                        lifetime = 90f;
                        pierce = true;
                        pierceCap = 15;
                        pierceBuilding = true;
                        knockback = 5f;

                        splashDamage = 1200f;
                        splashDamageRadius = 150f;
                        buildingDamageMultiplier = 0.5f;

                        status = StatusEffects.blasted;
                        statusDuration = 60f * 5;

                        hitEffect = new MultiEffect(
                                Fx.massiveExplosion,
                                Fx.coreExplosion,
                                new WaveEffect() {{
                                    colorFrom = Color.valueOf("#ffd37f");
                                    colorTo = Color.valueOf("#ffffe0");
                                    sizeTo = 250f;
                                    lifetime = 30f;
                                    strokeFrom = 10f;
                                }}
                        );
                        despawnEffect = hitEffect;
                        hitSound = Sounds.explosionArtilleryShockBig;
                        hitSoundVolume = 2f;

                        // Осколки для сплава
                        fragBullets = 8;
                        fragVelocityMin = 0.8f;
                        fragVelocityMax = 1.2f;
                        fragBullet = new BasicBulletType(6f, 300) {{
                            width = 10f;
                            height = 18f;
                            lifetime = 30f;
                            splashDamage = 150f;
                            splashDamageRadius = 50f;
                            trailColor = Color.valueOf("ffffe0");
                            hitEffect = Fx.blastExplosion;
                            status = StatusEffects.burning;
                            statusDuration = 60f * 3;
                        }};
                    }}

            );

            // --- КАСТОМНЫЙ ОТРИСОВЩИК ---
            drawer = new DrawTurret("reinforced-") {{
                parts.addAll(
                        new RegionPart("-barrel") {{
                            progress = PartProgress.recoil;
                            moveY = -25f;
                            heatColor = Color.valueOf("ff4500");
                        }},
                        new RegionPart("-vent") {{
                            mirror = true;
                            progress = PartProgress.warmup;
                            moveRot = 30f;
                        }},
                        // Ореол при зарядке
                        new HaloPart() {{
                            progress = PartProgress.warmup;
                            color = Color.valueOf("#ffd37f");
                            layer = Layer.effect;
                            y = 20f;
                            haloRadius = 16f;
                            triLength = 0f;
                            triLengthTo = 15f;
                            shapes = 6;
                            tri = true;
                        }}
                );
            }};

            outlineColor = Pal.darkOutline;
            limitRange(30f);
        }};

        // ██████ EREKIR MEGA-ENDGAME TURRET: OBLIVION ██████

// trash(((
// В методе load():
//        oblivion = new ItemTurret("oblivion"){{
//            requirements(Category.turret, with(
//                    Items.tungsten, 3000,
//                    Items.silicon, 2500,
//                    Items.graphite, 2000,
//                    Items.surgeAlloy, 1500,
//                    Items.carbide, 1200,
//                    Items.phaseFabric, 800,
//                    Items.oxide, 600,
//                    Items.beryllium, 500
//            ));
//
//            localizedName = "Oblivion";
//            description = "Абсолютное оружие Эрекира. 6×6 турель с 5 типами боеприпасов. Каждый снаряд имеет уникальную механику и визуальные эффекты. Требует воду для охлаждения.";
//
//            size = 6;
//            health = 25000;
//            armor = 50f;
//            range = 1100f;
//            reload = 120f;
//            shootCone = 2f;
//            inaccuracy = 0f;
//            rotateSpeed = 0.8f;
//            recoil = 20f;
//            shake = 15f;
//            shootSound = Sounds.explosionArtilleryShockBig;
//            shootSoundVolume = 3f;
//            ammoPerShot = 15;
//            maxAmmo = 150;
//
//            // Потребление
//            consumePower(400f / 60f); // 400 energy/sec
//            coolant = consume(new ConsumeLiquid(Liquids.water, 0.5f));
//            coolantMultiplier = 25f;
//
//            // Нагрев
//            heatColor = Color.valueOf("ff4400");
//            heatRequirement = 200f;
//            maxHeatEfficiency = 3f; // +200% урона при перегреве!
//
//            // Визуал (МАКСИМУМ ЭФФЕКТОВ!)
//            drawer = new DrawTurret("reinforced-"){{
//                Color heatc = Color.valueOf("ff4400");
//                Color heatc2 = Color.valueOf("ffaa00");
//                Color heatc3 = Color.valueOf("ffffff");
//                parts.addAll(
//                        // База
//                        new RegionPart("-base"){{
//                            progress = PartProgress.constant(1f);
//                            under = true;
//                            mirror = false;
//                        }},
//                        // Лопасти
//                        new RegionPart("-blade"){{
//                            progress = PartProgress.warmup;
//                            heatProgress = PartProgress.heat;
//                            heatColor = heatc;
//                            moveRot = -30f;
//                            moveX = 0f;
//                            moveY = -8f;
//                            mirror = true;
//                            children.add(new RegionPart("-side"){{
//                                progress = PartProgress.warmup.delay(0.5f);
//                                heatProgress = PartProgress.recoil;
//                                heatColor = heatc;
//                                mirror = true;
//                                under = false;
//                                moveY = -6f;
//                                moveX = 3f;
//                                moves.add(new PartMove(PartProgress.recoil, 3f, 10f, -50f));
//                            }});
//                        }},
//                        // Центральный ствол
//                        new RegionPart("-barrel"){{
//                            progress = PartProgress.recoil;
//                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(10f, 0.3f) * p.warmup);
//                            mirror = false;
//                            under = true;
//                            moveY = -10f;
//                            heatColor = heatc2;
//                        }},
//                        // Ядро
//                        new RegionPart("-core"){{
//                            progress = PartProgress.warmup;
//                            heatProgress = PartProgress.heat;
//                            color = Color.valueOf("00aaff");
//                            colorTo = Color.valueOf("ff4400");
//                            mirror = false;
//                            under = true;
//                            moves.add(new PartMove(PartProgress.warmup, 0f, -3f, 0f));
//                        }},
//                        // Кольцо 1
//                        new ShapePart(){{
//                            progress = PartProgress.warmup;
//                            color = heatc;
//                            circle = true;
//                            hollow = true;
//                            stroke = 0f;
//                            strokeTo = 4f;
//                            radius = 20f;
//                            layer = Layer.effect;
//                            y = -12f;
//                        }},
//                        // Кольцо 2
//                        new ShapePart(){{
//                            progress = PartProgress.warmup.delay(0.3f);
//                            color = heatc2;
//                            circle = true;
//                            hollow = true;
//                            stroke = 0f;
//                            strokeTo = 3f;
//                            radius = 14f;
//                            layer = Layer.effect;
//                            y = -12f;
//                            rotateSpeed = 3f;
//                        }},
//                        // Кольцо 3 (вращается в другую сторону)
//                        new ShapePart(){{
//                            progress = PartProgress.warmup.delay(0.5f);
//                            color = heatc3;
//                            circle = true;
//                            hollow = true;
//                            stroke = 0f;
//                            strokeTo = 2f;
//                            radius = 8f;
//                            layer = Layer.effect;
//                            y = -12f;
//                            rotateSpeed = -5f;
//                        }},
//                        // Искры при нагреве
//                        new ShapePart(){{
//                            progress = PartProgress.heat;
//                            color = heatc;
//                            circle = false;
//                            sides = 6;
//                            hollow = true;
//                            stroke = 0f;
//                            strokeTo = 3f;
//                            radius = 25f;
//                            layer = Layer.effect;
//                            y = -12f;
//                            rotateSpeed = 2f;
//                        }},
//                        // Аура при перегреве
//                        new HaloPart(){{
//                            progress = PartProgress.heat;
//                            color = heatc;
//                            layer = Layer.effect;
//                            y = 30f;
//                            haloRadius = 25f;
//                            triLength = 0f;
//                            triLengthTo = 20f;
//                            shapes = 8;
//                            tri = true;
//                        }}
//                );
//            }};
//
//            outlineColor = Pal.darkOutline;
//            scaledHealth = 400;
//            envEnabled |= Env.space;
//            fogRadiusMultiplier = 0.2f;
//            newTargetInterval = 60f;
//            unitSort = UnitSorts.strongest;
//
//            // ━━━ 5 УНИКАЛЬНЫХ БОЕПРИПАСОВ ━━━
//            ammo(
//                    // ██████ 1. БЕРИЛЛИЙ — Ледяной шторм ❄️ ██████
//                    Items.beryllium, new ArtilleryBulletType(4f, 450){{
//                        width = 22f;
//                        height = 30f;
//                        lifetime = 140f;
//                        splashDamage = 450f;
//                        splashDamageRadius = 100f;
//                        status = StatusEffects.freezing;
//                        statusDuration = 60f * 8f;
//                        backColor = trailColor = hitColor = Color.valueOf("6ec9ff");
//                        frontColor = Color.white;
//                        trailLength = 25;
//                        trailWidth = 4f;
//                        trailEffect = Fx.disperseTrail;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSpark, new Effect(40f, e -> {
//                            Draw.color(Color.valueOf("6ec9ff"));
//                            for(int i = 0; i < 8; i++){
//                                float angle = e.rotation + i * 45f;
//                                Drawf.tri(e.x, e.y, e.fout() * 4f, 30f * e.fout(), angle);
//                            }
//                        }));
//                        hitEffect = new MultiEffect(
//                                Fx.massiveExplosion,
//                                Fx.hitMeltdown,
//                                new WaveEffect(){{
//                                    colorFrom = Color.valueOf("6ec9ff");
//                                    colorTo = Color.valueOf("ffffff");
//                                    sizeTo = 180f;
//                                    lifetime = 40f;
//                                    strokeFrom = 8f;
//                                }},
//                                new Effect(50f, e -> {
//                                    Draw.color(Color.valueOf("6ec9ff"), e.fout());
//                                    Fill.circle(e.x, e.y, e.fin() * 60f);
//                                })
//                        );
//                        despawnEffect = hitEffect;
//                        hitSound = Sounds.explosionArtilleryShock;
//                        fragBullets = 6;
//                        fragSpread = 40f;
//                        fragBullet = new ArtilleryBulletType(3f, 80){{
//                            width = 12f;
//                            height = 16f;
//                            lifetime = 40f;
//                            splashDamage = 80f;
//                            splashDamageRadius = 40f;
//                            status = StatusEffects.freezing;
//                            statusDuration = 60f * 3f;
//                            backColor = trailColor = hitColor = Color.valueOf("6ec9ff");
//                            frontColor = Color.white;
//                        }};
//                    }},
//
//                    // ██████ 2. ВОЛЬФРАМ — Кинетический пробивник 🎯 ██████
//                    Items.tungsten, new RailBulletType(){{
//                        damage = 1200f;
//                        length = 900f;
//                        hitColor = Color.valueOf("5b5b5b");
//                        pierceEffect = new MultiEffect(Fx.hitBulletColor, Fx.colorSpark);
//                        pointEffect = new Effect(20f, e -> {
//                            Draw.color(Color.valueOf("5b5b5b"));
//                            Fill.circle(e.x, e.y, e.fout() * 8f);
//                        });
//                        pointEffectSpace = 15f;
//                        lineEffect = new MultiEffect(Fx.none, new Effect(30f, e -> {
//                            Draw.color(Color.valueOf("5b5b5b"), e.fout());
//                            Lines.line(e.x, e.y, e.data instanceof Vec2 ? ((Vec2)e.data).x : e.x + 100f, e.data instanceof Vec2 ? ((Vec2)e.data).y : e.y);
//                        }));
//                        endEffect = new MultiEffect(Fx.instBomb, new Effect(40f, e -> {
//                            Draw.color(Color.valueOf("5b5b5b"));
//                            for(int i = 0; i < 12; i++){
//                                float angle = i * 30f;
//                                Drawf.tri(e.x, e.y, e.fout() * 5f, 40f * e.fout(), angle);
//                            }
//                        }));
//                        pierceCap = 15;
//                        pierceBuilding = true;
//                        status = StatusEffects.slow;
//                        statusDuration = 60f * 4f;
//                    }},
//
//                    // ██████ 3. КАРБИД — Фазовая плазма 🟣 ██████
//                    Items.carbide, new BasicBulletType(8f, 680){{
//                        width = 20f;
//                        height = 28f;
//                        lifetime = 110f;
//                        pierce = true;
//                        pierceCap = 8;
//                        pierceBuilding = true;
//                        splashDamage = 200f;
//                        splashDamageRadius = 70f;
//                        status = InfernoStatusEffects.radioactive;
//                        statusDuration = 60f * 10f;
//                        backColor = trailColor = hitColor = Color.valueOf("ab8ec5");
//                        frontColor = Color.white;
//                        trailLength = 30;
//                        trailWidth = 4.5f;
//                        trailEffect = Fx.disperseTrail;
//                        trailInterval = 2f;
//                        trailRotation = true;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSparkBig, new Effect(50f, e -> {
//                            Draw.color(Color.valueOf("ab8ec5"));
//                            for(int i = 0; i < 10; i++){
//                                float angle = e.rotation + i * 36f;
//                                Drawf.tri(e.x, e.y, e.fout() * 5f, 35f * e.fout(), angle);
//                            }
//                        }));
//                        hitEffect = new MultiEffect(
//                                Fx.titanExplosion,
//                                Fx.hitMeltdown,
//                                new WaveEffect(){{
//                                    colorFrom = Color.valueOf("ab8ec5");
//                                    colorTo = Color.valueOf("d4b8e8");
//                                    sizeTo = 200f;
//                                    lifetime = 45f;
//                                    strokeFrom = 10f;
//                                }},
//                                new Effect(60f, e -> {
//                                    Draw.color(Color.valueOf("ab8ec5"), e.fout());
//                                    for(int i = 0; i < 6; i++){
//                                        float angle = i * 60f + e.rotation;
//                                        Drawf.tri(e.x, e.y, e.fout() * 8f, 50f * e.fout(), angle);
//                                    }
//                                })
//                        );
//                        despawnEffect = hitEffect;
//                        hitSound = Sounds.explosionArtilleryShock;
//                        fragBullets = 5;
//                        fragSpread = 35f;
//                        fragBullet = new BasicBulletType(6f, 120){{
//                            width = 10f;
//                            height = 14f;
//                            lifetime = 35f;
//                            pierce = true;
//                            pierceCap = 3;
//                            backColor = trailColor = hitColor = Color.valueOf("ab8ec5");
//                            frontColor = Color.white;
//                        }};
//                    }},
//
//                    // ██████ 4. СПЛАВ ВОЛНЫ — Электрический каскад ⚡ ██████
//                    Items.surgeAlloy, new MissileBulletType(7f, 520){{
//                        width = 18f;
//                        height = 26f;
//                        lifetime = 120f;
//                        splashDamage = 520f;
//                        splashDamageRadius = 90f;
//                        lightning = 8;
//                        lightningDamage = 65f;
//                        lightningLength = 15;
//                        lightningCone = 360f;
//                        status = StatusEffects.shocked;
//                        statusDuration = 60f * 5f;
//                        backColor = trailColor = hitColor = Pal.accent;
//                        frontColor = Color.white;
//                        trailLength = 28;
//                        trailWidth = 4f;
//                        trailEffect = Fx.disperseTrail;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSparkBig, Fx.lightningShoot, new Effect(45f, e -> {
//                            color(Pal.accent);
//                            for(int i = 0; i < 8; i++){
//                                float angle = e.rotation + i * 45f;
//                                Drawf.tri(e.x, e.y, e.fout() * 4f, 32f * e.fout(), angle);
//                            }
//                        }));
//                        hitEffect = new MultiEffect(
//                                Fx.massiveExplosion,
//                                Fx.titanLightSmall,
//                                new WaveEffect(){{
//                                    colorFrom = Pal.accent;
//                                    colorTo = Color.white;
//                                    sizeTo = 170f;
//                                    lifetime = 40f;
//                                    strokeFrom = 8f;
//                                }},
//                                new Effect(55f, e -> {
//                                    color(Pal.accent, e.fout());
//                                    for(int i = 0; i < 12; i++){
//                                        float len = Mathf.random(30f, 70f);
//                                        float angle = Mathf.random(360f);
//                                        Lines.line(e.x, e.y, e.x + Angles.trnsx(angle, len), e.y + Angles.trnsy(angle, len));
//                                    }
//                                })
//                        );
//                        despawnEffect = hitEffect;
//                        hitSound = Sounds.explosionArtilleryShock;
//                        fragBullets = 4;
//                        fragSpread = 30f;
//                        fragBullet = new MissileBulletType(5f, 90){{
//                            width = 10f;
//                            height = 14f;
//                            lifetime = 40f;
//                            splashDamage = 90f;
//                            splashDamageRadius = 35f;
//                            lightning = 3;
//                            lightningDamage = 25f;
//                            lightningLength = 8;
//                            backColor = trailColor = hitColor = Pal.accent;
//                            frontColor = Color.white;
//                        }};
//                    }},
//
//                    // ██████ 5. ФАЗОВАЯ ТКАНЬ — Телепортирующий луч 🌀 ██████
//                    Items.phaseFabric, new BasicBulletType(10f, 850){{
//                        width = 24f;
//                        height = 32f;
//                        lifetime = 100f;
//                        pierce = true;
//                        pierceCap = 12;
//                        pierceBuilding = true;
//                        splashDamage = 300f;
//                        splashDamageRadius = 85f;
//                        homingPower = 0.2f;
//                        homingRange = 400f;
//                        status = StatusEffects.unmoving;
//                        statusDuration = 60f * 3f;
//                        backColor = trailColor = hitColor = Color.valueOf("ffd37f");
//                        frontColor = Color.white;
//                        trailLength = 35;
//                        trailWidth = 5f;
//                        trailEffect = Fx.disperseTrail;
//                        trailInterval = 1.5f;
//                        trailRotation = true;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSparkBig, new Effect(60f, e -> {
//                            Draw.color(Color.valueOf("ffd37f"));
//                            for(int i = 0; i < 8; i++){
//                                float angle = e.rotation + i * 45f;
//                                float len = 20f + Mathf.sin(e.fin() * 180f, 40f);
//                                Drawf.tri(e.x + Angles.trnsx(angle, len), e.y + Angles.trnsy(angle, len), e.fout() * 5f, 40f * e.fout(), angle);
//                            }
//                        }));
//                        hitEffect = new MultiEffect(
//                                Fx.massiveExplosion,
//                                Fx.hitMeltdown,
//                                new WaveEffect(){{
//                                    colorFrom = Color.valueOf("ffd37f");
//                                    colorTo = Color.white;
//                                    sizeTo = 220f;
//                                    lifetime = 50f;
//                                    strokeFrom = 12f;
//                                }},
//                                new Effect(70f, e -> {
//                                    Draw.color(Color.valueOf("ffd37f"), e.fout());
//                                    for(int i = 0; i < 8; i++){
//                                        float angle = i * 45f + e.rotation;
//                                        float x = e.x + Angles.trnsx(angle, 50f * e.fin());
//                                        float y = e.y + Angles.trnsy(angle, 50f * e.fin());
//                                        Drawf.tri(x, y, e.fout() * 6f, 45f * e.fout(), angle);
//                                    }
//                                }),
//                                new Effect(50f, e -> {
//                                    Draw.color(Color.valueOf("ffd37f"), e.fout() * 0.5f);
//                                    Fill.circle(e.x, e.y, e.fin() * 80f);
//                                })
//                        );
//                        despawnEffect = hitEffect;
//                        hitSound = Sounds.explosionArtilleryShockBig;
//                        fragBullets = 8;
//                        fragSpread = 40f;
//                        fragBullet = new BasicBulletType(7f, 150){{
//                            width = 12f;
//                            height = 16f;
//                            lifetime = 30f;
//                            pierce = true;
//                            pierceCap = 4;
//                            backColor = trailColor = hitColor = Color.valueOf("ffd37f");
//                            frontColor = Color.white;
//                        }};
//                    }}
//            );
//
//            limitRange();
//        }};
//        interceptorAA = new ItemTurret("interceptor-aa") {{
//            requirements(Category.turret, with(
//                    Items.titanium, 350,
//                    Items.silicon, 300,
//                    Items.plastanium, 200,
//                    Items.thorium, 150
//            ));
//
//            localizedName = "Interceptor AA";
//            description = "Зенитный комплекс с самонаводящимися перехватчиками. При попадании выпускает микро-дроны для добивания.";
//
//            size = 3;
//            health = 2400;
//            range = 480f;
//            reload = 45f;
//            shootCone = 5f;
//            rotateSpeed = 6f;
//            targetAir = true;
//            targetGround = false;
//
//            shootSound = Sounds.shoot;
//            shootSoundVolume = 1.2f;
//
//            ammo(
//                    // 🟡 1. Обычные перехватчики
//                    Items.titanium, new BasicBulletType(7f, 35f) {{
//                        width = 6f;
//                        height = 10f;
//                        lifetime = 50f;
//                        splashDamage = 20f;
//                        splashDamageRadius = 18f;
//                        homingPower = 0.15f;
//                        homingRange = 200f;
//
//                        trailColor = Color.valueOf("a0d6ff");
//                        trailWidth = 1.8f;
//                        trailLength = 12;
//
//                        fragOnHit = true;
//                        fragBullets = 3;
//                        fragBullet = new MissileBulletType(4f, 15f) {{
//                            width = 4f;
//                            height = 6f;
//                            lifetime = 30f;
//                            homingPower = 0.2f;
//                            trailColor = Color.valueOf("a0d6ff");
//                        }};
//                    }},
//
//                    // 🔴 2. Тяжёлые перехватчики (с дронами)
//                    InfernoItems.strontium, new BasicBulletType(6f, 60f) {{
//                        width = 8f;
//                        height = 14f;
//                        lifetime = 60f;
//                        splashDamage = 40f;
//                        splashDamageRadius = 25f;
//                        homingPower = 0.2f;
//                        homingRange = 250f;
//
//                        trailColor = Color.valueOf("ff6666");
//                        trailWidth = 2.2f;
//                        trailLength = 15;
//
//                        fragOnHit = true;
//                        fragBullets = 2;
//                        fragBullet = new BulletType(0f, 0f) {{
//                            spawnUnit = new MissileUnitType("interceptor-drone") {{
//                                speed = 3f;
//                                lifetime = 60f * 3f;
//                                health = 50;
//                                targetAir = true;
//                                weapons.add(new Weapon() {{
//                                    bullet = new BasicBulletType(4f, 10f);
//                                }});
//                            }};
//                        }};
//                    }}
//            );
//
//            drawer = new DrawTurret() {{
//                parts.add(
//                        new RegionPart("-barrel") {{
//                            progress = PartProgress.recoil;
//                            moveY = -3f;
//                        }},
//                        new RegionPart("-radar") {{
//                            progress = PartProgress.warmup;
//                            moveRot = 90f;
//                        }}
//                );
//            }};
//        }};

        //        singularity = new PowerTurret("singularity"){{
//            requirements(Category.turret, with(
//                    Items.copper, 1200,
//                    Items.silicon, 800,
//                    Items.thorium, 600,
//                    Items.phaseFabric, 400,
//                    Items.surgeAlloy, 300
//            ));
//
//            localizedName = "Singularity";
//            description = "Создаёт миниатюрные чёрные дыры, которые притягивают и сжимают врагов. При схлопывании наносит огромный урон.";
//
//            size = 4;
//            health = 4200;
//            range = 400f;
//            reload = 600f;
//            shootCone = 3f;
//            rotateSpeed = 3f;
//            targetAir = true;
//            targetGround = true;
//
//            shootSound = Sounds.shootForeshadow;
//            shootSoundVolume = 2.0f;
//
//            consumePower(450f / 60f);
//            coolant = consumeCoolant(2f);
//            coolantMultiplier = 4f;
//
//            // ━━━ ВИЗУАЛ ━━━
//            drawer = new DrawTurret(""){{
//                Color heatc = Color.valueOf("6a0dad");
//
//                parts.addAll(
//                        new RegionPart("-base"){{
//                            progress = PartProgress.constant(1f);
//                            under = true;
//                            mirror = false;
//                        }},
//                        new RegionPart("-barrel"){{
//                            progress = PartProgress.recoil;
//                            mirror = false;
//                            moveY = -6f;
//                            heatColor = heatc;
//                        }},
//                        new RegionPart("-side"){{
//                            progress = PartProgress.warmup;
//                            mirror = true;
//                            x = 12f;
//                            y = -6f;
//                            moveX = 2f;
//                            moveRot = -10f;
//                            heatColor = heatc;
//                        }},
//                        new ShapePart(){{
//                            progress = PartProgress.reload.inv();
//                            color = Color.valueOf("0a0a0a");
//                            circle = true;
//                            hollow = false;
//                            radius = 15f;
//                            layer = Layer.effect;
//                            y = -15f;
//                        }},
//                        new ShapePart(){{
//                            progress = PartProgress.reload.inv().delay(0.3f);
//                            color = Color.valueOf("6a0dad");
//                            circle = true;
//                            hollow = true;
//                            stroke = 0f;
//                            strokeTo = 4f;
//                            radius = 20f;
//                            layer = Layer.effect;
//                            y = -15f;
//                            rotateSpeed = 5f;
//                        }}
//                );
//            }};
//
//            outlineColor = Pal.darkOutline;
//            scaledHealth = 260;
//
//            // ━━━ ЧЁРНАЯ ДЫРА ━━━
//            shootType = new infernoadvanced.entities.bullet.BlackHoleBulletType(){{
//                gravityStrength = 15f;
//                pullRadius = 150f;
//                damageRadius = 50f;
//                lifetime = 240f;
//                growthTime = 60f;
//                maxSize = 40f;
//                minSize = 10f;
//                pullDamage = 3f;
//                implosionDamage = 500f;
//
//                blackHoleColor = Color.valueOf("0a0a0a");
//                eventHorizon = Color.valueOf("6a0dad");
//                accretionDisk = Color.valueOf("9d4edd");
//                gravityWave = Color.valueOf("c77dff");
//
//                // ✅ ВАЖНО: Переопределяем статы турели
//                damage = 0f;
//                splashDamage = 0f;
//                splashDamageRadius = 0f;
//                knockback = 0f;
//            }};
//
//        }};
        //        tempestAA = new ItemTurret("tempest-aa") {{
//            requirements(Category.turret, with(
//                    Items.titanium, 250,      // Уменьшил стоимость
//                    Items.silicon, 200,
//                    Items.thorium, 450,
//                    Items.graphite, 360,
//                    Items.copper, 900
//            ));
//
//            localizedName = "Tempest AA";
//            description = "Элитная зенитка с жидкостными ускорителями. Разные жидкости дают разные эффекты.";
//
//            size = 3;
//            health = 2800;                    // Поменьше здоровья
//            range = 480f;
//            reload = 28f;
//            shootCone = 6f;
//            rotateSpeed = 5.5f;
//            targetAir = true;
//            targetGround = false;
//
//            shootSound = InfernoSounds.shootSwift2;
//            shootSoundVolume = 2.8f;
//
//            // ===== 4 СТВОЛА =====
//            shoot = new ShootBarrel() {{
//                barrels = new float[] {
//                        -5f, 4f, 0f,
//                        5f, 4f, 0f,
//                        -5f, -4f, 0f,
//                        5f, -4f, 0f,
//                };
//                shots = 4;
//                shotDelay = 7f;
//            }};
//
//            consumePower(500f / 60f);
//            consumeAmmoOnce = true;
//
//            // ===== ОХЛАДИТЕЛИ (как в salvo) =====
//            coolant = consumeCoolant(0.25f);
//
//            // ===== ЭФФЕКТЫ ВЫСТРЕЛА =====
//            ammoUseEffect = Fx.casing3;        // Гильзы как у salvo
//            shootEffect = Fx.shootBig;
//
//            ammo(
//                    // 🟠 1. ГРАФИТ — бронебойный
//                    Items.graphite, new BasicBulletType(7f, 25f) {{
//                        width = 4.5f;
//                        height = 24f;
//                        lifetime = 65f;
//                        splashDamage = 35f;
//                        splashDamageRadius = 20f;
//                        pierceCap = 2;
//                        reloadMultiplier = 0.9f;
//
//                        hitColor = Pal.graphiteAmmoBack;
//                        backColor = Pal.graphiteAmmoBack;
//                        frontColor = Pal.graphiteAmmoFront;
//                        trailColor = Pal.graphiteAmmoBack;
//
//                        trailWidth = 2f;
//                        trailLength = 18;
//                        hitEffect = Fx.hitBulletColor;
//                    }},
//
//                    // 🔴 2. ТОРИЙ — тяжёлый
//                    Items.thorium, new BasicBulletType(6.5f, 35f) {{
//                        width = 5f;
//                        height = 26f;
//                        lifetime = 70f;
//                        splashDamage = 45f;
//                        splashDamageRadius = 24f;
//                        pierceCap = 3;
//
//                        hitColor = Pal.thoriumAmmoBack;
//                        backColor = Pal.thoriumAmmoBack;
//                        frontColor = Pal.thoriumAmmoFront;
//                        trailColor = Pal.thoriumAmmoBack;
//
//                        trailWidth = 2.3f;
//                        trailLength = 20;
//                        hitEffect = new MultiEffect(
//                                Fx.hitBulletColor,
//                                Fx.shockwave
//                        );
//                    }}
//            );
//
//            drawer = new DrawTurret() {{
//                // 4 ствола
//                for(int i = 0; i < 4; i++) {
//                    int finalI = i;
//                    parts.add(new RegionPart("-barrel") {{
//                        progress = PartProgress.recoil;
//                        recoilIndex = finalI;
//                        moveY = -3f;
//                        x = (finalI < 2 ? -5f : 5f);
//                        y = (finalI % 2 == 0 ? 4f : -4f);
//                    }});
//                }
//            }};
//
//            liquidCapacity = 80f;
//            limitRange(22f);
//        }};
//        vulcan = new ItemTurret("vulcan"){{
//            requirements(Category.turret, with(
//                    Items.copper, 300,
//                    Items.lead, 250,
//                    Items.silicon, 180,
//                    Items.titanium, 120,
//                    InfernoItems.strontium, 80
//            ));
//
//            localizedName = "Vulcan";
//            description = "Скорострельная осколочная турель. Стронций добавляет радиоактивный эффект к снарядам.";
//
//            size = 3;
//            health = 1400;
//            range = 260f;
//            reload = 8f; // Быстрая стрельба
//            shootCone = 15f;
//            rotateSpeed = 5f;
//            recoil = 0.8f;
//            shake = 2f;
//            shootSound = Sounds.shootSalvo;
//
//            coolantMultiplier = 6f;
//
//            targetAir = false;
//
//            // Очередь из 3 снарядов
//            shoot = new ShootPattern(){{
//                shots = 3;
//                shotDelay = 4f;
//                inaccuracy = 3f;
//            }};
//
//            // Потребление
//
//            // Визуал
//            drawer = new DrawTurret(""){{
//                Color heatc = Color.valueOf("ff6644"); // Оранжевый от стронция
//                parts.addAll(
//                        new RegionPart("-side"){{
//                            progress = PartProgress.warmup;
//                            mirror = true;
//                            moveX = 1.5f;
//                            moveY = -1f;
//                            moveRot = -8f;
//                            heatColor = heatc;
//                        }},
//                        new RegionPart("-barrel"){{
//                            progress = PartProgress.recoil;
//                            mirror = false;
//                            moveY = -3f / 4f;
//                            heatColor = heatc;
//                        }}
//                );
//            }};
//
//            outlineColor = Pal.darkOutline;
//            scaledHealth = 160;
//
//            // БОЕПРИПАСЫ
//            ammo(
//                    // Титан: базовые осколки
//                    Items.titanium, new BasicBulletType(5f, 32){{
//                        width = 9f;
//                        height = 13f;
//                        lifetime = 52f;
//                        pierce = true;
//                        pierceCap = 2;
//                        backColor = trailColor = hitColor = Pal.accent;
//                        frontColor = Color.white;
//                        trailLength = 10;
//                        trailWidth = 2f;
//                        shootEffect = Fx.shootBig;
//                        hitEffect = Fx.hitBulletColor;
//                        ammoMultiplier = 3f;
//                    }},
//
//                    // Стронций: радиоактивные осколки
//                    InfernoItems.strontium, new BasicBulletType(5.2f, 38){{
//                        width = 10f;
//                        height = 14f;
//                        lifetime = 50f;
//                        pierce = true;
//                        pierceCap = 3;
//                        status = InfernoStatusEffects.radioactive; // Твой кастомный статус!
//                        statusDuration = 360f;
//                        backColor = trailColor = hitColor = Color.valueOf("ff6644");
//                        frontColor = Color.white;
//                        trailLength = 11;
//                        trailWidth = 2.3f;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSpark);
//                        hitEffect = new MultiEffect(Fx.hitBulletColor, Fx.smokeCloud);
//                        ammoMultiplier = 2f;
//                        fragBullets = 2; // Выпускает 2 мелких осколка
//                        fragBullet = new BasicBulletType(4f, 12){{
//                            width = 5f;
//                            height = 7f;
//                            lifetime = 20f;
//                            backColor = trailColor = hitColor = Color.valueOf("ff6644");
//                            frontColor = Color.white;
//                        }};
//                    }},
//
//                    // Сплав волны: электрические осколки
//                    Items.surgeAlloy, new BasicBulletType(5.5f, 45){{
//                        width = 11f;
//                        height = 15f;
//                        lifetime = 47f;
//                        pierce = true;
//                        pierceCap = 4;
//                        lightning = 2; // Молнии при попадании
//                        lightningDamage = 15f;
//                        lightningLength = 6;
//                        backColor = trailColor = hitColor = Pal.accent;
//                        frontColor = Color.white;
//                        trailLength = 12;
//                        trailWidth = 2.5f;
//                        shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSparkBig);
//                        hitEffect = new MultiEffect(Fx.hitBulletColor, Fx.lightningShoot);
//                        ammoMultiplier = 1.5f;
//                    }}
//            );
//
//            coolantMultiplier = 8f;
//            limitRange();
//        }};
//        // ━━━ 🔫 ФАЗОВЫЙ РЕЙЛГАН (Phase Railgun) ━━━
//        phaseRailgun = new PowerTurret("phase-railgun"){{
//            requirements(Category.turret, with(
//                    Items.copper, 1500,
//                    Items.silicon, 1000,
//                    Items.thorium, 800,
//                    Items.phaseFabric, 500,
//                    Items.surgeAlloy, 400
//            ));
//
//            localizedName = "Phase Railgun";
//            description = "Стреляет сгустками фазовой энергии. Пробивает врагов насквозь и бьёт молнией.";
//
//            size = 3;
//            health = 3500;
//            range = 500f; // Очень дальняя
//            reload = 45f; // Быстрая для рейлгана
//            shootCone = 5f;
//            rotateSpeed = 4f;
//            targetAir = true;
//            targetGround = true;
//
//            shootSound = Sounds.shootForeshadow;
//            shootSoundVolume = 1.5f;
//
//            consumePower(300f / 60f);
//            coolant = consumeCoolant(1.5f);
//            coolantMultiplier = 3f;
//
//            // ━━━ ВИЗУАЛ ━━━
//            drawer = new DrawTurret(""){{
//                Color heatc = Color.valueOf("#ffd37f"); // Золотой нагрев
//                parts.addAll(
//                        new RegionPart("-barrel"){{
//                            progress = PartProgress.recoil;
//                            moveY = -8f;
//                            heatColor = heatc;
//                        }},
//                        new RegionPart("-side"){{
//                            progress = PartProgress.warmup;
//                            mirror = true;
//                            x = 10f;
//                            y = -5f;
//                            moveX = 1f;
//                            moveRot = -5f;
//                            heatColor = heatc;
//                        }}
//                );
//            }};
//
//            outlineColor = Pal.darkOutline;
//            scaledHealth = 250;
//
//            // ━━━ СНАРЯД ━━━
//            shootType = new BasicBulletType(12f, 150f){{ // Быстрый и мощный
//                width = 8f;
//                height = 25f;
//                lifetime = 50f;
//                pierce = true;
//                pierceCap = 5; // Пробивает 5 врагов
//                pierceBuilding = true;
//
//                // ━━━ ЦВЕТА ━━━
//                backColor = Color.valueOf("#6a0dad"); // Фиолетовый
//                frontColor = Color.valueOf("#ffd37f"); // Золотой
//                trailColor = Color.valueOf("#9d4edd");
//
//                // ━━━ ЭФФЕКТЫ (ТВОИ IADraw!) ━━━
//                trailEffect = IADraw.phaseTrailEffect; // ✨ Твой готовый трейл
//                hitEffect = IADraw.shockHitEffect;     // ⚡ Твоя молния
//                despawnEffect = IADraw.phaseExplosionEffect; // 💥 Твой взрыв
//
//                // ━━━ УРОН ━━━
//                splashDamage = 50f;
//                splashDamageRadius = 40f;
//                lightning = 3; // 3 молнии при попадании!
//                lightningLength = 10;
//                lightningDamage = 50f;
//
//                status = StatusEffects.shocked;
//                statusDuration = 60f;
//            }};
//        }};



    }
}