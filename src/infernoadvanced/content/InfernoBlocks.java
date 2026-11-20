package infernoadvanced.content;

import arc.graphics.Color;
import infernoadvanced.entities.bullet.BlackHoleBulletType;
import infernoadvanced.entities.bullet.GammaLaserBulletType;
import infernoadvanced.entities.bullet.PushBulletType;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.graphics.IAPal;
import infernoadvanced.loaders.IASounds;
import infernoadvanced.world.blocks.defense.mines.TriggerMine;
import infernoadvanced.world.blocks.defense.mines.TripwireMine;
import infernoadvanced.world.blocks.defense.other.GasVent;
import infernoadvanced.world.blocks.defense.turrets.GammaChargeTurret;
import infernoadvanced.world.blocks.production.FloorLiquidExtracter;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

import static mindustry.type.ItemStack.with;

public class InfernoBlocks {

    public static Block

    celestineOre,

    //crafters-serpulo
    refractoryBronzeSmelter, celestineSynthesizer, barium133Producer, cesiumGenerator, bariumEnricher, strontiumCentrifuge,

    wasteBurner,

    sandOilRig, co2Vent,

    //turrets-serpulo
    impulse,

    clusterMine, blastMine,
    tripwireMine, stormMine,

    skyfall, MLRSdownpour, MLRShail,

    sievert, singularityCannon,

    //turrets-erekir
    archimedes;

    /*
    regions:
    1.1-region Crafters
    2.1-region turrets
    2.2-region specific turrets
     */

    public static void load() {

        celestineOre = new OreBlock("ore-celestine", InfernoItems.celestine) {{
            variants = 3;
            useColor = true;
            mapColor.set(IAPal.celestine).mul(0.85f);
            oreDefault = false;
        }};


        //1.1-region Crafters
        refractoryBronzeSmelter = new GenericCrafter("refractory-bronze-smelter") {{
            requirements(Category.crafting, with(
                    Items.copper, 60,
                    Items.lead, 40,
                    Items.graphite, 20
            ));

            size = 2;
            scaledHealth = 60f;
            itemCapacity = 10;
            craftTime = 130f;

            consumeItems(with(
                    Items.copper, 3,
                    Items.lead, 2,
                    Items.graphite, 1
            ));
            consumePower(0.5f);

            outputItem = new ItemStack(InfernoItems.refractoryBronze, 1);

            craftEffect = Fx.smeltsmoke;
            updateEffect = Fx.pulverizeMedium;
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.07f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.refractoryBronzeGlow;
                        alpha = 0.6f;
                    }}
            );
        }};

        celestineSynthesizer = new GenericCrafter("celestine-synthesizer") {{
            requirements(Category.crafting, with(
                    Items.titanium, 80,
                    Items.silicon, 60,
                    Items.lead, 70
            ));

            size = 3;
            scaledHealth = 80f;
            itemCapacity = 20;
            liquidCapacity = 40f;
            craftTime = 150f;

            consumeItems(with(Items.sand, 6));
            consumeLiquid(InfernoLiquids.carbonDioxide, 0.15f);
            consumePower(1.5f);

            outputItem = new ItemStack(InfernoItems.celestine, 1);

            craftEffect = Fx.pulverizeMedium;
            updateEffect = Fx.freezing;
            ambientSound = Sounds.loopBio;
            ambientSoundVolume = 0.08f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(InfernoLiquids.carbonDioxide) {{
                        alpha = 0.35f;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.celestineGlow;
                        alpha = 0.5f;
                        glowScale = 6f;
                    }}
            );
        }};

        strontiumCentrifuge = new GenericCrafter("strontium-centrifuge") {{
            requirements(Category.crafting, with(
                    Items.titanium, 150,
                    Items.thorium, 100,
                    Items.silicon, 120,
                    Items.surgeAlloy, 30,
                    InfernoItems.refractoryBronze, 50
            ));

            size = 4;
            scaledHealth = 120f;
            itemCapacity = 15;
            liquidCapacity = 60f;
            craftTime = 110f;

            consumeItems(with(
                    InfernoItems.celestine, 3,
                    Items.coal, 2,
                    Items.pyratite, 1
            ));
            consumeLiquid(Liquids.cryofluid, 0.3f);
            consumePower(3f);

            outputItem = new ItemStack(InfernoItems.strontium, 1);

            craftEffect = Fx.pulverizeMedium;
            updateEffect = Fx.smokeCloud;
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.09f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile() {{
                        drawLiquidLight = true;
                    }},
                    new DrawRegion("-rotor") {{
                        spinSprite = true;
                        rotateSpeed = 3f;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.strontiumGlow;
                        alpha = 0.8f;
                        glowScale = 6f;
                    }}
            );
        }};

        barium133Producer = new GenericCrafter("barium133-producer") {{
            requirements(Category.crafting, with(
                    Items.silicon, 60,
                    Items.titanium, 50,
                    Items.lead, 70,
                    InfernoItems.refractoryBronze, 15
            ));

            size = 2;
            scaledHealth = 100f;
            itemCapacity = 15;
            craftTime = 120f;

            consumeItems(with(
                    Items.titanium, 2,
                    Items.lead, 3
            ));
            consumePower(2f);

            outputItem = new ItemStack(InfernoItems.barium133, 1);

            craftEffect = Fx.smeltsmoke;
            ambientSound = Sounds.loopExtract;
            ambientSoundVolume = 0.05f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.barium133Glow;
                        alpha = 0.5f;
                        glowScale = 6f;
                        rotateSpeed = 0.5f;
                    }}
            );
        }};

        cesiumGenerator = new GenericCrafter("cesium-generator") {{
            requirements(Category.crafting, with(
                    Items.thorium, 80,
                    Items.silicon, 100,
                    Items.graphite, 70,
                    Items.lead, 120,
                    InfernoItems.refractoryBronze, 30
            ));

            size = 3;
            scaledHealth = 140f;
            itemCapacity = 20;
            liquidCapacity = 60f;
            craftTime = 90f;

            consumeItems(with(
                    Items.thorium, 2,
                    Items.sand, 4
            ));
            consumeLiquid(Liquids.water, 0.15f);
            consumePower(2f);

            outputItem = new ItemStack(InfernoItems.cesium137, 1);

            craftEffect = Fx.smeltsmoke;
            ambientSound = Sounds.loopHover;
            ambientSoundVolume = 0.05f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water) {{
                        alpha = 0.5f;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.cesium137Glow;
                        alpha = 0.5f;
                        glowScale = 8f;
                    }}
            );
        }};

        bariumEnricher = new GenericCrafter("barium-enricher") {{
            requirements(Category.crafting, with(
                    Items.copper, 250,
                    Items.surgeAlloy, 30,
                    Items.phaseFabric, 50,
                    Items.thorium, 120,
                    Items.titanium, 80,
                    InfernoItems.refractoryBronze, 60
            ));

            size = 4;
            scaledHealth = 150f;
            itemCapacity = 20;
            liquidCapacity = 60f;
            craftTime = 120f;

            consumeItems(with(
                    InfernoItems.barium133, 3,
                    InfernoItems.cesium137, 1
            ));
            consumeLiquid(Liquids.cryofluid, 0.2f);
            consumePower(4f);

            outputItem = new ItemStack(InfernoItems.barium137m, 1);

            craftEffect = Fx.smeltsmoke;
            ambientSound = Sounds.loopHover2;
            ambientSoundVolume = 0.08f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile() {{
                        drawLiquidLight = true;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion() {{
                        color = IAPal.barium133Glow;
                        alpha = 0.55f;
                        glowScale = 10f;
                        rotateSpeed = 0.4f;
                    }},
                    new DrawGlowRegion() {{
                        color = IAPal.cesium137Glow;
                        alpha = 0.55f;
                        glowScale = 10f;
                        rotateSpeed = -0.4f;
                    }}
            );
        }};

        wasteBurner = new ConsumeGenerator("waste-burner") {{
            requirements(Category.power, with(
                    Items.copper, 180,
                    Items.lead, 220,
                    Items.graphite, 100,
                    Items.silicon, 50,
                    InfernoItems.refractoryBronze, 80
            ));

            size = 3;
            scaledHealth = 160f;
            hasLiquids = true;
            hasItems = false;

            powerProduction = 12.5f;

            consumeLiquid(InfernoLiquids.oilWaste, 0.3f);

            ambientSound = Sounds.loopFire;
            ambientSoundVolume = 0.2f;

            generateEffect = IAFx.Blocks.wasteBurnerSmoke;
            generateEffectRange = 6f;
            effectChance = 0.01f;
        }};

        sandOilRig = new FloorLiquidExtracter("sand-oil-rig") {{
            requirements(Category.production, with(
                    Items.copper, 150,
                    Items.graphite, 175,
                    Items.lead, 115,
                    Items.thorium, 115,
                    Items.silicon, 75
            ));

            size = 3;
            liquidCapacity = 40f;
            itemCapacity = 10;

            attribute = Attribute.oil;
            baseEfficiency = 0f;
            boostScale = 1f;

            craftTime = 60f;

            consumeItem(Items.sand);
            consumeLiquid(Liquids.water, 0.15f);
            consumePower(3f);

            outputLiquids = LiquidStack.with(InfernoLiquids.oilWaste, 10f / 60f, InfernoLiquids.carbonDioxide, 8f / 60f);
            liquidOutputDirections = new int[]{1, 3};
            rotate = true;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(InfernoLiquids.oilWaste),
                    new DrawDefault()
            );

            ambientSound = Sounds.loopFire;
            ambientSoundVolume = 0.3f;

            updateEffect = new MultiEffect(IAFx.Blocks.oilRigFlame, IAFx.Blocks.oilRigSmoke);
            updateEffectChance = 0.2f;
            updateEffectSpread = 0.4f;
        }};

        co2Vent = new GasVent("co2-vent") {{
            requirements(Category.effect, with(
                    Items.copper, 30,
                    Items.graphite, 20,
                    Items.silicon, 15
            ));

            size = 2;
            liquidCapacity = 60f;
            extinguishRadius = 64f;
            extinguishPower = 90f;
            useAmount = 25f;
        }};

        //2.1-region turrets serpulo
        skyfall = new ItemTurret("skyfall") {{
            requirements(Category.turret, with(
                    Items.copper, 150,
                    Items.lead, 70,
                    Items.graphite, 50
            ));

            researchCostMultiplier = 0.35f;

            scaledHealth = 90;

            size = 2;
            range = 250f;
            reload = 120f;
            shootCone = 35f;
            rotateSpeed = 6.5f;
            recoil = 1.5f;
            recoilTime = 8f;

            inaccuracy = 3f;
            targetAir = true;
            targetGround = false;
            predictTarget = true;

            shootSound = Sounds.shootMissileSmall;
            shootSoundVolume = 0.5f;

            ammoPerShot = 3;
            maxAmmo = 18;

            recoils = 3;
            shootY = 3f;

            shoot = new ShootBarrel() {{
                barrels = new float[]{
                        -3f, 6f, 0f,
                        0f, 6f, 0f,
                        3f, 6f, 0f
                };
                shots = 6;
                shotDelay = 10f;
            }};

            ammo(
                    Items.lead, InfernoBullets.skyfallLead,
                    Items.pyratite, InfernoBullets.skyfallPyratite
            );

            coolant = consumeCoolant(0.2f);

            heatColor = Pal.turretHeat;

            drawer = new DrawTurret() {{
                float[] xs = {-3f, 0f, 3f};
                for (int i = 0; i < xs.length; i++) {
                    final int fi = i;
                    parts.add(new RegionPart("-barrel") {{
                        x = xs[fi];
                        y = 0f;
                        progress = PartProgress.recoil;
                        recoilIndex = fi;
                        moveY = -1.5f;
                        heatColor = Pal.turretHeat;
                        heatProgress = PartProgress.recoil;
                        mirror = false;
                    }});
                }

                parts.add(new RegionPart("-top") {{
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup;
                }});
            }};
            limitRange();
        }};

        MLRSdownpour = new ItemTurret("MLRS-downpour") {{
            requirements(Category.turret, with(
                    Items.copper, 300,
                    InfernoItems.refractoryBronze, 50,
                    Items.graphite, 170,
                    Items.silicon, 120,
                    Items.plastanium, 100
            ));

            researchCostMultiplier = 0.35f;

            scaledHealth = 160;

            size = 3;
            range = 300f;
            reload = 400f;
            shootCone = 20f;
            rotateSpeed = 3.5f;
            recoil = 2.0f;
            recoilTime = 15f;
            shake = 2f;

            inaccuracy = 2f;
            targetAir = false;
            targetGround = true;
            predictTarget = true;

            shootSound = Sounds.shootMissile;
            shootEffect = Fx.shootBig;
            smokeEffect = IAFx.Turrets.shootSmokeDownpour;
            heatColor = Pal.turretHeat;

            ammoPerShot = 12;
            maxAmmo = 60;

            recoils = 4;
            shootY = 9f;

            shoot = new ShootBarrel() {{
                barrels = new float[]{
                        -3f, 2f, 0f,
                        3f, 2f, 0f,
                        -5f, -2f, 0f,
                        5f, -2f, 0f
                };
                shots = 12;
                shotDelay = 5f;
            }};

            consumePower(180f / 60f);
            coolant = consumeCoolant(0.2f);

            ammo(
                    Items.graphite, InfernoBullets.downpourGraphite,
                    Items.titanium, InfernoBullets.downpourTitanium,
                    Items.blastCompound, InfernoBullets.downpourBlast
            );

            drawer = new DrawTurret() {{
                float[] frontXs = {-3f, 3f};
                for (int i = 0; i < frontXs.length; i++) {
                    final int fi = i;
                    parts.add(new RegionPart("-barrel") {{
                        x = frontXs[fi];
                        y = 2f;
                        progress = PartProgress.recoil;
                        recoilIndex = fi;
                        moveY = -2f;
                        heatColor = Pal.turretHeat;
                        heatProgress = PartProgress.recoil;
                        mirror = false;
                    }});
                }

                float[] backXs = {-5f, 5f};
                for (int i = 0; i < backXs.length; i++) {
                    final int fi = i + 2;
                    parts.add(new RegionPart("-barrel") {{
                        x = backXs[fi - 2];
                        y = -2f;
                        progress = PartProgress.recoil;
                        recoilIndex = fi;
                        moveY = -2f;
                        heatColor = Pal.turretHeat;
                        heatProgress = PartProgress.recoil;
                        mirror = false;
                    }});
                }

                parts.add(new RegionPart("-frame") {{
                    progress = PartProgress.constant(1f);
                    mirror = false;
                }});
            }};
            limitRange();
        }};

        MLRShail = new ItemTurret("MLRS-hail") {{
            requirements(Category.turret, with(
                    Items.copper, 800,
                    InfernoItems.refractoryBronze, 650,
                    Items.graphite, 500,
                    Items.silicon, 775,
                    Items.plastanium, 275,
                    Items.surgeAlloy, 250,
                    InfernoItems.strontium, 25
            ));

            researchCostMultiplier = 0.9f;

            scaledHealth = 200;

            size = 4;
            range = 780f;
            reload = 2000f;
            shootCone = 11f;
            rotateSpeed = 0.3f;
            shake = 3f;
            inaccuracy = 6.5f;

            ammoPerShot = 150;
            maxAmmo = 300;

            targetAir = true;
            targetGround = true;
            predictTarget = true;

            shootSound = IASounds.hailLaunch;
            shootSoundVolume = 0.3f;
            shootEffect = Fx.shootBig;
            smokeEffect = IAFx.Turrets.shootSmokeHail;
            heatColor = Pal.turretHeat;

            recoils = 6;
            recoil = 1.4f;
            recoilTime = 15f;
            shootY = 0f;
            shoot = new ShootBarrel() {{
                barrels = new float[]{
                        -10f, 12f, 0f, -6f, 12f, 0f, -2f, 12f, 0f,
                        2f, 12f, 0f, 6f, 12f, 0f, 10f, 12f, 0f,
                };
                shots = 42;
                shotDelay = 10f;
            }};


            consumePower(450f / 60f);
            consumeLiquid(Liquids.oil, 0.5f);

            ammo(
                    Items.blastCompound, InfernoBullets.hailBlastMissile,
                    Items.titanium, InfernoBullets.hailTitaniumMissile
//                    InfernoItems.strontium, InfernoBullets.hailStrontiumMissile
            );

            limitRange();

            drawer = new DrawTurret() {{
                parts.add(new RegionPart("-plate") {{
                    progress = PartProgress.warmup;
                    under = true;
                    mirror = false;
                    heatColor = Pal.turretHeat;
                }});

                float[] xs = {-10f, -6f, -2f, 2f, 6f, 10f};
                for (int i = 0; i < xs.length; i++) {
                    final int fi = i;
                    parts.add(new RegionPart("-barrel") {{
                        x = xs[fi];
                        y = 0f;
                        progress = PartProgress.recoil;
                        recoilIndex = fi;
                        moveY = -3f;
                        heatColor = Pal.turretHeat;
                        heatProgress = PartProgress.recoil;
                        mirror = false;
                    }});
                }

                parts.add(new RegionPart("-frame") {{
                    progress = PartProgress.constant(1f);
                    y = -2f;
                    mirror = false;
                }});
            }};
        }};

        //2.1-region turrets erekir

        //2.2-region specific turrets
        clusterMine = new TriggerMine("cluster-mine") {{
            requirements(Category.effect, with(Items.lead, 30, Items.titanium, 20, Items.silicon, 15));
            size = 1;
            health = 60;
            shots = 8;
            cone = 360f;
            bullet = new BasicBulletType(5f, 30) {{
                lifetime = 25f;
                pierce = true;
                pierceCap = 2;
                collidesAir = false;
                splashDamage = 20f;
                splashDamageRadius = 22f;
                width = 4f;
                height = 6f;
                hitColor = backColor = trailColor = Color.valueOf("7d89d8");
                frontColor = Color.valueOf("dae1ee");
                hitEffect = despawnEffect = Fx.hitBulletColor;
                trailWidth = 1.5f;
                trailLength = 4;
            }};
        }};

        blastMine = new TriggerMine("blast-mine") {{
            requirements(Category.effect, with(Items.blastCompound, 40, Items.titanium, 35, Items.silicon, 25));
            size = 1;
            health = 80;
            shots = 1;
            cone = 0f;
            bullet = new BasicBulletType(3f, 300) {{
                lifetime = 6f;
                collidesAir = false;
                splashDamage = 250f;
                splashDamageRadius = 45f;
                width = 6f;
                height = 6f;
                hitColor = backColor = trailColor = Pal.blastAmmoBack;
                frontColor = Pal.blastAmmoFront;
                hitEffect = despawnEffect = IAFx.Explosions.mineBlast;
            }};
        }};

        tripwireMine = new TripwireMine("tripwire-mine") {{
            requirements(Category.effect, with(Items.titanium, 30, Items.graphite, 25, Items.silicon, 15));
            size = 1;
            health = 60;
            range = 55f;
            cone = 40f;
            shots = 7;
            fuseTime = 10f;
            bullet = new BasicBulletType(6f, 38) {{
                lifetime = 20f;
                pierce = true;
                pierceCap = 3;
                collidesAir = false;
                splashDamage = 12f;
                splashDamageRadius = 18f;
                width = 4f;
                height = 6f;
                hitColor = backColor = trailColor = Color.valueOf("7d89d8");
                frontColor = Color.valueOf("dae1ee");
                hitEffect = despawnEffect = Fx.hitBulletColor;
                trailWidth = 1.5f;
                trailLength = 4;
            }};
        }};

        stormMine = new TripwireMine("storm-mine") {{
            requirements(Category.effect, with(Items.titanium, 50, Items.plastanium, 40, Items.blastCompound, 40, Items.silicon, 30));
            size = 1;
            health = 100;
            range = 50f;
            cone = 120f;
            shots = 12;
            fuseTime = 14f;
            bullet = new BasicBulletType(7f, 90) {{
                lifetime = 22f;
                collidesAir = false;
                splashDamage = 42f;
                splashDamageRadius = 30f;
                width = 5f;
                height = 8f;
                hitColor = backColor = trailColor = Pal.blastAmmoBack;
                frontColor = Pal.blastAmmoFront;
                hitEffect = despawnEffect = IAFx.Explosions.stormBlast;
                trailWidth = 2f;
                trailLength = 5;
            }};
        }};

        impulse = new PowerTurret("impulse") {{
            requirements(Category.turret, with(
                    Items.copper, 200,
                    Items.silicon, 120,
                    Items.titanium, 80,
                    Items.metaglass, 40
            ));

            scaledHealth = 200;

            size = 3;
            range = 160f;
            reload = 165f;
            shootCone = 8f;
            rotateSpeed = 4f;
            targetAir = true;
            targetGround = false;

            shootSound = Sounds.shootLancer;
            shootSoundVolume = 1.2f;
            shootEffect = Fx.shootSmall;
            smokeEffect = Fx.none;
            heatColor = Pal.turretHeat;

            consumePower(360f / 60f);
            coolantMultiplier = 6f;

            drawer = new DrawTurret() {{
                parts.add(new RegionPart("-barrel") {{
                    progress = PartProgress.recoil;
                    mirror = false;
                    moveY = -2f;
                    heatColor = Pal.turretHeat;
                }});

                parts.add(new RegionPart("-side") {{
                    progress = PartProgress.warmup;
                    mirror = true;
                    x = 5f;
                    y = 1f;
                    moveX = 1f;
                    moveRot = -8f;
                    heatColor = Pal.turretHeat;
                }});
            }};

            shootType = new PushBulletType(5f, 36f) {{
                knockbackStrength = 5f;
                splashDamage = 20f;
                splashDamageRadius = 10f;
                slowAmount = 0.5f;
                slowDuration = 60f * 3f;
                sizeGrowth = 0.4f;
                maxSize = 35f;
                minSize = 8f;
                pushInterval = 10f;

                pushBackColor = Color.valueOf("6ec9ff");
                pushFrontColor = Color.white;

                trailWidth = 3f;
                trailLength = 8;
                trailColor = pushBackColor;
            }};
        }};

        sievert = new GammaChargeTurret("sievert") {{
            requirements(Category.turret, with(
                    Items.copper, 1200, Items.silicon, 400, Items.surgeAlloy, 200,
                    Items.phaseFabric, 150, InfernoItems.barium137m, 150, InfernoItems.celestine, 120
            ));

            size = 4;
            scaledHealth = 160;
            envEnabled |= Env.space;

            reload = 180f;
            range = 160f;
            rotateSpeed = 1.5f;
            recoil = 4f;
            shake = 5f;
            shootCone = 15f;

            shootSound = Sounds.shootBeamPlasma;
            chargeSound = Sounds.chargeCorvus;
            soundPitchMin = 1f;
            heatColor = Pal.turretHeat;

            liquidCapacity = 60f;
            consumePower(20f);
            coolant = consumeCoolant(0.3f);

            shoot.firstShotDelay = IAFx.Turrets.barium137mLaserCharge.lifetime;
            cooldownTime = 350f;

            moveWhileCharging = true;
            accurateDelay = true;

            chargePart.muzzleOffset = 14f;
            chargePart.color = IAPal.barium137mAmmoFront.cpy();
            chargePart.colorTo = Color.white.cpy();
            chargePart.sphereColor = IAPal.barium137mAmmoBack.cpy();
            chargePart.coreColor = Color.white.cpy();
            chargePart.spawnRadiusMax = 23f;
            chargePart.spiralTurns = 0.85f;
            chargePart.shapes = 16;
            chargePart.sphereRadius = 11f;

            shootType = new GammaLaserBulletType() {{
                length = 170f;
                damage = 1100f;
                width = 14f;
                lifetime = 65f;

                splashDamage = 1400f;
                splashDamageRadius = 45f;
                pierceArmor = true;

                plasmaColor1 = IAPal.barium137mAmmoFront.cpy();
                plasmaColor2 = IAPal.barium137mAmmoBack.cpy();
                hexColor = IAPal.barium137mAmmoFront.cpy();
                coreColor = Color.valueOf("0d0514");
                widthScale = 1.5f;
                coreWidthMult = 6;

                lightningSpacing = 35f;
                lightningLength = 5;
                lightningDelay = 1.1f;
                lightningLengthRand = 15;
                lightningDamage = 100;
                lightningAngleRand = 40f;
                largeHit = true;
                lightColor = lightningColor = IAPal.barium137mAmmoFront;

                chargeEffect = Fx.none;

                sideAngle = 15f;
                sideWidth = 0f;
                sideLength = 0f;

                hitEffect = Fx.hitMeltdown;
                hitColor = IAPal.barium137mAmmoFront;
                status = InfernoStatusEffects.radiationMedium;
                statusDuration = 120f;
                buildingDamageMultiplier = 0.6f;
                backLengthFract = 0.1f;
            }};

//            drawer = new DrawTurret() {{
//                parts.add(new RegionPart("-glow") {{
//                    progress = PartProgress.warmup;
//                    colorTo = IAPal.barium137mGlow.cpy().a(0.9f);
//                    color = Color.clear;
//                    blending = Blending.additive;
//                    under = true;
//                }});
//            }};
        }};

        singularityCannon = new PowerTurret("singularity-cannon") {{
            requirements(Category.turret, ItemStack.with(
                    Items.copper, 1500,
                    Items.silicon, 800,
                    Items.plastanium, 300,
                    Items.surgeAlloy, 250,
                    InfernoItems.refractoryBronze, 200,
                    InfernoItems.barium137m, 50
            ));

            size = 5;
            scaledHealth = 160;
            armor = 12f;

            reload = 60f * 20;
            range = 480f;
            shootCone = 2f;
            rotateSpeed = 1.5f;
            recoil = 6f;
            shake = 10f;

            consumePower(45f);
            consumeLiquid(Liquids.cryofluid, 0.3f);

            shootSound = Sounds.shootForeshadow;
            chargeSound = Sounds.chargeVela;
            shootSoundVolume = 2f;

            shootType = new BlackHoleBulletType(2.5f, 2200f) {{
                pierce = true;
                pierceCap = 999;
                pierceBuilding = true;
                pierceArmor = true;
                despawnHit = false;
                keepVelocity = true;

                lifetime = 60f * 5;

                pullRadius = 160f;
                pullForce = 45f;
                shaderRadius = 80f;
                innerRadius = 18f;
                ringRadius = 35f;

                splashDamage = 150f;
                splashDamageRadius = 60f;

                status = InfernoStatusEffects.gravityCollapse;
                statusDuration = 60f * 6;


            }};

            drawer = new DrawTurret("singularity-") {{
                parts.add(new RegionPart("-charge-ring") {{
                    progress = PartProgress.warmup;
                    moveRot = -360f;
                    rotateSpeed = 2f;
                    under = false;
                    layer = Layer.effect;
                }});

                parts.add(new RegionPart("-barrel") {{
                    progress = PartProgress.recoil;
                    moveY = -8f;
                    heatColor = Pal.turretHeat;
                }});
            }};

            shootEffect = Fx.none;
            smokeEffect = Fx.none;

            heatColor = Pal.turretHeat;

            limitRange(45f);

        }};
    }
}