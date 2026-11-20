package infernoadvanced.content;

import arc.graphics.Color;
import arc.math.Interp;
import infernoadvanced.ai.types.AggressiveFighterAI;
import infernoadvanced.entities.abilities.BarrelRollAbility;
import infernoadvanced.entities.abilities.DashAbility;
import infernoadvanced.entities.bullet.BlackHoleBulletType;
import infernoadvanced.entities.units.RollUnitType;
import infernoadvanced.entities.units.RollWeapon;
import infernoadvanced.graphics.IAPal;
import infernoadvanced.loaders.IASounds;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.meta.BlockFlag;

import static arc.graphics.Gl.fastest;
import static arc.graphics.Gl.none;
import static infernoadvanced.content.InfernoBullets.*;

public class InfernoUnits {

    public static UnitType

            wren, swift, hornet, falcon, raptor,

            iskra, ember, flame
                    ;

    public static void load() {

        wren = new UnitType("wren") {{
            constructor = UnitEntity::create;
            flying = true;
            omniMovement = false;
            faceTarget = true;
            circleTarget = true;
            targetFlags = new BlockFlag[]{null};

            controller = u -> u.team.isAI() ? new AggressiveFighterAI() {{
                huntRangeRatio = 2f;
                retargetTime = 15f;

                tailDistance = 30f;
                maxLeadTime = 15f;
                speedGain = 0.1f;
                minSpeedRatio = 0.7f;
                aimCone = 50f;

                turnBrake = 0.15f;
                extraTurnSpeed = 5f;

                passDistanceRatio = 0.2f;
                extendRatio = 0.5f;
                extendMaxTime = 60f;

                avoidanceRadius = 20f;
                avoidanceGap = 3f;
                avoidanceStrength = 0.5f;
            }} : new CommandAI();

            health = 60f;
            armor = 0f;
            hitSize = 8f;

            speed = 4f;
            accel = 0.06f;
            drag = 0.02f;
            rotateSpeed = 3f;

            engineOffset = 5f;
            engineSize = 1.8f;
            useEngineElevation = true;
            itemCapacity = 0;

            trailLength = 3;

            weapons.add(new Weapon() {{
                x = 0f;
                y = 2f;
                mirror = false;
                rotate = false;
                shootCone = 30f;
                reload = 22f;
                shootSound = IASounds.shootWren;
                shootSoundVolume = 0.5f;

                bullet = new BasicBulletType(6f, 5f) {{
                    width = 1f;
                    height = 8f;
                    lifetime = 15f;
                    sprite = "casing";
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    hitEffect = Fx.hitBulletSmall;
                    despawnEffect = Fx.hitBulletSmall;
                }};
            }});
        }};

        swift = new UnitType("swift") {{
            constructor = UnitEntity::create;
            flying = true;
            omniMovement = false;
            faceTarget = false;
            circleTarget = true;
            targetFlags = new BlockFlag[]{null};

            controller = u -> u.team.isAI() ? new AggressiveFighterAI() {{
                huntRangeRatio = 2.5f;
                retargetTime = 12f;

                tailDistance = 40f;
                maxLeadTime = 20f;
                speedGain = 0.12f;
                minSpeedRatio = 0.7f;
                aimCone = 50f;

                turnBrake = 0.2f;
                extraTurnSpeed = 4.5f;

                passDistanceRatio = 0.2f;
                extendRatio = 0.6f;
                extendMaxTime = 60f;

                avoidanceRadius = 30f;
                avoidanceGap = 5f;
                avoidanceStrength = 0.8f;
            }} : new CommandAI();

            health = 130f;
            armor = 0f;
            hitSize = 11f;

            speed = 5f;
            accel = 0.05f;
            drag = 0.02f;
            rotateSpeed = 2.4f;

            engineOffset = 7f;
            engineSize = 2.2f;
            useEngineElevation = true;
            itemCapacity = 0;

            trailLength = 4;

            weapons.add(new Weapon() {{
                x = 6f;
                y = -1f;
                reload = 2.5f;
                mirror = true;
                alternate = true;
                rotate = false;
                shootCone = 45f;
                inaccuracy = 1.5f;
                shootSound = IASounds.shootSwift;
                shootSoundVolume = 0.1f;

                int speedbylife = 2;
                bullet = new BasicBulletType(12f*speedbylife, 7f) {{
                    width = 1f;
                    height = 13f;
                    lifetime = 15f/speedbylife;
                    sprite = "casing";
                    shootSound = IASounds.shootSwift;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    hitEffect = Fx.hitBulletSmall;
                    despawnEffect = Fx.hitBulletSmall;
                }};
            }});
        }};

        hornet = new RollUnitType("hornet") {{
            constructor = UnitEntity::create;
            flying = true;
            omniMovement = false;
            faceTarget =  true;
            circleTarget = true;
            targetFlags = new BlockFlag[]{null};

            controller = u -> u.team.isAI() ? new AggressiveFighterAI() {{
                huntRangeRatio = 3.5f;
                retargetTime = 45f;

                tailDistance = 80f;
                maxLeadTime = 60f;
                speedGain = 0.04f;
                minSpeedRatio = 0.4f;
                aimCone = 30f;

                turnBrake = 0.5f;
                extraTurnSpeed = 2f;

                passDistanceRatio = 0.4f;
                extendRatio = 1.1f;
                extendMaxTime = 150f;

                avoidanceRadius = 50f;
                avoidanceGap = 15f;
                avoidanceStrength = 1.5f;
            }} : new CommandAI();

            health = 480f;
            armor = 3f;
            hitSize = 23f;

            speed = 6f;
            accel = 0.025f;
            drag = 0.018f;
            rotateSpeed = 1.8f;

            engineOffset = 5f;
            engineSize = 3f;
            useEngineElevation = true;
            itemCapacity = 0;

            trailLength = 16;

            abilities.add(new ShieldArcAbility() {{
                radius = 30f;
                angle = 220f;
                angleOffset = 180f;
                x = 0f;
                y = -2f;
                max = 160f;
                regen = 0.25f;
                cooldown = 60f * 6f;
                width = 4f;
                whenShooting = false;
            }});

            abilities.add(new BarrelRollAbility() {{

                cooldown = 60f * 3f;
                duration = 60f;
                reflect = true;

                rollOnShot = true;
                shotRollHasCooldown = false;

                parryRadius = 28f;
                threatRadius = 45f;

                whenShooting = false;
            }});

            BulletType missile = hornetMissle;
            weapons.add(nozzle(8.5f, 12f, 180f, 3, missile, 8f));    // внутренние сопла
            weapons.add(nozzle(11.5f, 9f, 180f, 3, missile, 16f)); // внешние сопла
        }};

        falcon = new RollUnitType("falcon") {{
            constructor = UnitEntity::create;
            flying = true;
            omniMovement = false;
            faceTarget = false;
            circleTarget = true;
            targetFlags = new BlockFlag[]{null};

            health = 3600f;
            armor = 6f;
            hitSize = 30f;

            speed = 4.5f;
            accel = 0.35f;
            drag = 0.016f;
            rotateSpeed = 1.35f;

            engineOffset = 7f;
            engineSize = 4.5f;
            itemCapacity = 0;
            trailLength = 18;
            moveSound = Sounds.loopHover;

            abilities.add(new DashAbility() {{
                chargeTime = 60f * 0.8f;
                cooldown = 60f * 3f;

                dashSpeed = 20f;
                minRange = 140f;
                maxRange = 360f;
                overshoot = 80f;

                dashDamage = 750f;
                buildingDamageMultiplier = 1.1f;

                dashSound = IASounds.falconDash;
            }});

            abilities.add(new ForceFieldAbility(35f, 0.5f, 1800f, 240f, 10, 45f) {
            {
                  this.breakSound = Sounds.shieldBreak;
            }
            });

            abilities.add(new ShieldArcAbility() {{
                radius = 22f;
                angle = 360f;
                angleOffset = 0f;
                widthScale = 20f;

                whenShooting = true;

                x = 0f;
                y = 3f;
                max = 260f;
                regen = 0.4f;
                cooldown = 60f * 8f;
                width = 5f;
            }});

            weapons.add(new Weapon("inferno-advanced-falcon-aa") {{
                x = -8f;
                y = -10f;
                mirror = false;
                top = true;
                shootY = 5f;
                recoil = 1.5f;
                smoothReloadSpeed = 2f;
                shadow = 6f;

                rotate = true;
                rotateSpeed = 14f;
                shootCone = 8f;
                inaccuracy = 3f;
                shake = 0.3f;

                controllable = false;
                autoTarget = true;

                reload = 2.5f;
                shoot = new ShootAlternate(3f);
                ejectEffect = Fx.casing1;
                shootSound = Sounds.shootArtillery;
                shootSoundVolume = 0.5f;

                int speedbylife = 2;
                bullet = new BasicBulletType(12f * speedbylife, 10f) {{
                    width = 1f;
                    height = 13f;
                    lifetime = 10f;

                    sprite = "casing";

                    collidesGround = false;
                    collidesAir = true;

                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    hitEffect = Fx.hitBulletSmall;
                    despawnEffect = Fx.hitBulletSmall;

                    status = StatusEffects.slow;
                    statusDuration = 10f;
                }};
            }});

            BulletType missile = falconMissle;
            weapons.add(nozzle(8.5f, 12f, 180f, 3, missile, 8f));
            weapons.add(nozzle(11.5f, 9f, 180f, 3, missile, 16f));
        }};

        raptor = new UnitType("raptor") {{
            constructor = UnitEntity::create;
            flying = true;
            omniMovement = false;
            faceTarget = false;
            circleTarget = true;
            targetFlags = new BlockFlag[]{null};

            controller = u -> u.team.isAI() ? new AggressiveFighterAI() {{
                huntRangeRatio = 4.5f;
                retargetTime = 60f;

                tailDistance = 140f;
                maxLeadTime = 80f;
                speedGain = 0.04f;
                minSpeedRatio = 0.6f;
                aimCone = 45f;

                turnBrake = 0.3f;
                extraTurnSpeed = 1.5f;

                passDistanceRatio = 0.4f;
                extendRatio = 1.1f;
                extendMaxTime = 180f;

                avoidanceRadius = 60f;
                avoidanceGap = 15f;
                avoidanceStrength = 1.0f;
            }} : new CommandAI();

            health = 11000f;
            armor = 15f;
            hitSize = 40f;

            speed = 3.8f;
            accel = 0.03f;
            drag = 0.015f;
            rotateSpeed = 1.2f;

            engineOffset = 18f;
            engineSize = 5.5f;
            useEngineElevation = true;

            engines.add(new UnitEngine(20.5f, 1f, 3.5f, 280f));
            engines.add(new UnitEngine(-20.5f, 1f, 3.5f, 260f));

            itemCapacity = 0;
            trailLength = 16;
            loopSound = Sounds.loopHover;

            abilities.add(new DashAbility() {{
                chargeTime = 60f * 1.2f;
                cooldown = 60f * 4f;

                dashSpeed = 22f;
                minRange = 160f;
                maxRange = 400f;
                overshoot = 100f;

                dashDamage = 850f;
                buildingDamageMultiplier = 1.2f;

                dashSound = IASounds.falconDash;
            }});

            abilities.add(new ForceFieldAbility(60f, 1.0f, 3000f, 300f, 12, 50f) {{
                breakSound = Sounds.shieldBreak;
            }});

            abilities.add(new ShieldArcAbility() {{
                radius = 40f;
                angle = 140f;
                angleOffset = 0f;
                widthScale = 20f;

                whenShooting = true;

                x = 0f;
                y = 5f;
                max = 800f;
                regen = 0.6f;
                cooldown = 60f * 10f;
                width = 6f;
            }});

            weapons.add(new Weapon("inferno-advanced-hole-ejector") {{
                x = 0f;
                y = 6f;
                mirror = false;
                alternate = false;

                top = false;
                shadow = 12f;
                layerOffset = -20f;

                rotate = true;
                baseRotation = 0f;
                rotationLimit = 50f;
                rotateSpeed = 8f;
                shootCone = 20f;
                shootY = 8f;

                reload = 360f;
                recoil = 5f;
                shake = 8f;
                shootSound = Sounds.shootForeshadow;
                chargeSound = Sounds.chargeVela;
                shootSoundVolume = 0.8f;

                shootStatus = InfernoStatusEffects.battleHunger;
                shootStatusDuration = 60f * 0.75f;

                shoot = new ShootPattern() {{
                    firstShotDelay = 45f;
                    shots = 3;
                    shotDelay = 14f;
                }};

                bullet = new BlackHoleBulletType(5f, 250f) {{
                    pierce = true;
                    pierceCap = 999;
                    pierceBuilding = true;
                    pierceArmor = true;
                    despawnHit = false;
                    keepVelocity = true;

                    lifetime = 60f * 0.75f;

                    pullRadius = 90f;
                    pullForce = 22.5f;
                    shaderRadius = 40f;
                    innerRadius = 9f;
                    ringRadius = 17.5f;

                    splashDamage = 150f;
                    splashDamageRadius = 60f;

                    status = InfernoStatusEffects.gravityCollapse;
                    statusDuration = 60f * 3;

                }};
            }});

            weapons.add(new Weapon("inferno-advanced-raptor-minigun") {{
                x = 24f;
                y = 6f;
                mirror = true;
                alternate = false;

                rotate = true;
                baseRotation = 0f;
                rotationLimit = 180f;
                rotateSpeed = 8f;
                shootCone = 20f;
                shootY = 8f;
                shootX = -0.5f;

                top = false;
                shadow = 12f;
                layerOffset = -10f;

                reload = 3.5f;
                shootSound = Sounds.shootArtillery;
                ejectEffect = Fx.casing2;

                bullet = new FlakBulletType(10f, 20f) {{
                    sprite = "casing";
                    width = 1.3f;
                    height = 13f;

                    fragBullets = 8;
                    fragBullet = new BasicBulletType(3.5f, 7f) {{
                        sprite = "casing";
                        width = 0.6f;
                        height = 6f;
                        lifetime = 15f;
                        backColor = Pal.bulletYellowBack;
                        frontColor = Pal.bulletYellow;
                        hitEffect = despawnEffect = Fx.hitBulletSmall;
                        collidesAir = true;
                        collidesGround = true;
                    }};

                    splashDamage = 35f;
                    splashDamageRadius = 20f;

                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    hitEffect = Fx.flakExplosion;
                    smokeEffect = Fx.shootBigSmoke;

                    collidesAir = true;
                    collidesGround = true;
                    status = StatusEffects.burning;
                    statusDuration = 60f;
                }};
            }});
        }};

        //core-units
        iskra = new UnitType("iskra"){{
            controller = u -> u.team.isAI() ? new BuilderAI(true, 400f) : new CommandAI();
            isEnemy = false;
            targetBuildingsMobile = false;
            lowAltitude = true;
            flying = true;
            fogRadius = 0f;

            immunities.add(StatusEffects.burning);

            health = 170f;
            buildSpeed = 0.5f;
            mineSpeed = 6.5f;
            mineTier = 1;
            itemCapacity = 30;
            speed = 3f;
            rotateSpeed = 15f;
            accel = 0.1f;
            drag = 0.05f;
            engineOffset = 6f;
            hitSize = 8f;
            wreckSoundVolume = 0.8f;
            deathSoundVolume = 0.7f;

            weapons.add(new Weapon(){{
                y = 1f;
                x = 0f;
                shootCone = 10f;
                reload = 80f;
                shoot.shots = 3;
                shoot.shotDelay = 3f;
                ejectEffect = Fx.casing1;
                mirror = false;
                bullet = new BasicBulletType(2.5f, 9){{
                    inaccuracy = 4f;
                    width = 7f;
                    height = 9f;
                    lifetime = 32f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        ember = new UnitType("ember"){{
            controller = u -> u.team.isAI() ? new BuilderAI(true, 400f) : new CommandAI();
            isEnemy = false;
            targetBuildingsMobile = false;
            lowAltitude = true;
            flying = true;
            fogRadius = 0f;

            immunities.add(StatusEffects.burning);

            health = 200f;
            buildSpeed = 0.8f;
            // TODO: verify
            mineSpeed = 7f;
            mineTier = 1;
            itemCapacity = 50;
            // TODO: verify
            speed = 3.3f;
            rotateSpeed = 17f;
            accel = 0.1f;
            drag = 0.05f;
            engineOffset = 6f;
            hitSize = 9f;
            wreckSoundVolume = 0.8f;
            deathSoundVolume = 0.7f;

            abilities.add(new ForceFieldAbility(13f, 0.15f, 100f, 60f * 8f));

            weapons.add(
                    new Weapon(){{
                        top = false;
                        reload = 24f;
                        x = 3.5f;
                        y = 1f;
                        recoil = 1f;
                        mirror = true;
                        alternate = true;
                        shootSound = Sounds.shootAlpha;
                        bullet = new BasicBulletType(3f, 12){{
                            scaleKeepVelocity = true;
                            width = 1.5f;
                            height = 5f;
                            lifetime = 60f;
                            buildingDamageMultiplier = 0f;
                            homingPower = 0.03f;
                            hitEffect = despawnEffect = Fx.hitBulletColor;
                        }};
                    }}
            );
        }};

        flame = new UnitType("flame"){{
            controller = u -> u.team.isAI() ? new BuilderAI(true, 400f) : new CommandAI();
            isEnemy = false;
            targetBuildingsMobile = false;
            lowAltitude = true;
            flying = true;
            fogRadius = 0f;

            immunities.add(StatusEffects.burning);

            health = 270f;
            buildSpeed = 1.2f;
            mineSpeed = 8f;
            mineTier = 2;
            itemCapacity = 80;
            speed = 3.55f;
            rotateSpeed = 19f;
            accel = 0.11f;
            drag = 0.05f;
            engineOffset = 6f;
            hitSize = 11f;
            wreckSoundVolume = 0.8f;
            deathSoundVolume = 0.7f;

            abilities.add(
                    new ForceFieldAbility(16f, 0.2f, 320f, 60f * 8f),
                    new RepairFieldAbility(){{
                        amount = 6f;
                        reload = 60f * 7f;
                        range = 55f;
                    }}
            );

            weapons.add(new Weapon(){{
                x = y = 0f;
                mirror = false;
                reload = 55f;
                shootSound = Sounds.shootMissileShort;
                bullet = new MissileBulletType(3f, 18){{
                    width = 8f;
                    height = 8f;
                    shrinkY = 0f;
                    drag = -0.003f;
                    homingRange = 60f;
                    scaleKeepVelocity = true;
                    keepVelocity = false;
                    splashDamageRadius = 20f;
                    splashDamage = 12f;
                    lifetime = 50f;
                    buildingDamageMultiplier = 0f;
                    trailColor = Pal.unitBack;
                    backColor = Pal.unitBack;
                    frontColor = Color.white;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                }};
            }});
        }};
    }

    private static RollWeapon nozzle(float nx, float ny, float nreload, int salvo, BulletType missile, float ShotDelay) {
        return new RollWeapon() {{
            x = nx;
            y = ny;
            reload = nreload;
            mirror = true;
            alternate = true;
            rotate = false;
            shootCone = 40f;
            shootX = 2f;
            shootY = 4f;
            inaccuracy = 0f;
            velocityRnd = 0.15f;
            shootSound = Sounds.shootMissile;
            shoot.shots = salvo;
            shoot.shotDelay = ShotDelay;
            bullet = missile;
            top = false;
        }};
    }
}