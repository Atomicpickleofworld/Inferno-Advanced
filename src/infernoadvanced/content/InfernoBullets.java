package infernoadvanced.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.graphics.IAPal;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;

public class InfernoBullets {

    public static BulletType
    archimedesCarbideFrag, archimedesSurgeFrag,

    skyfallLead, skyfallPyratite,
    downpourGraphite, downpourTitanium, downpourBlast,

    hailBlastMissile, hailTitaniumMissile, hailStrontiumMissile,
    hailBlastFrag, hailTitaniumFrag, hailStrontiumFrag,

    reactorBlastFrag,

    vanillaDuoRefractoryBronze,
    vanillaSalvoRefractoryBronze,
    vanillaSalvoBarium133,
    vanillaFuseStrontium,
    vanillaRippleBarium133,
    vanillaSwarmerCesium137,

    hornetMissle, falconMissle;

    public static void load() {
        //region Archimedes Frags
        archimedesCarbideFrag = new BasicBulletType(8f, 200) {{
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

        archimedesSurgeFrag = new BasicBulletType(6f, 300) {{
            width = 10f;
            height = 18f;
            lifetime = 30f;
            splashDamage = 150f;
            splashDamageRadius = 50f;
            trailColor = Pal.surgeAmmoBack;
            hitEffect = Fx.blastExplosion;
            status = StatusEffects.burning;
            statusDuration = 60f * 3;
        }};
        //endregion

        //region MLRS
        skyfallLead = new BasicBulletType(10f, 42f) {{

            width = 8f;
            height = 12f;

            shootSound = Sounds.shootScatter;

            lifetime = 45f;
            drag = 0.002f;

            homingPower = 0f;
            pierce = false;
            pierceArmor = false;

            splashDamage = 20f;
            splashDamageRadius = 30f;
            ammoMultiplier = 4;

            lightRadius = 30f;
            lightOpacity = 0.5f;
        }};

        skyfallPyratite = new MissileBulletType(7f, 10f) {{
            backColor = trailColor = Pal.lightPyraFlame;
            frontColor = Pal.lightPyraFlame;

            width = 4f;
            height = 10f;
            sprite = "missile-large";

            lifetime = 50f;
            drag = 0.004f;

            homingPower = 0.1f;
            homingRange = 20f;

            pierce = false;
            pierceArmor = false;

            splashDamage = 45f;
            splashDamageRadius = 40f;

            trailWidth = 2.5f;
            trailLength = 15;
            trailEffect = Fx.missileTrail;

            status = StatusEffects.burning;
            statusDuration = 600f;
            makeFire = true;

            hitEffect = new MultiEffect(
                    Fx.blastExplosion,
                    Fx.fireSmoke,
                    Fx.ballfire
            );

            lightRadius = 40f;
            lightColor = Pal.lightPyraFlame;
            lightOpacity = 0.7f;
        }};

        // === Downpour Missiles ===
        downpourGraphite = new BasicBulletType(9f, 85f) {{
            backColor = trailColor = hitColor = Pal.graphiteAmmoBack;
            frontColor = Pal.graphiteAmmoFront;

            width = 8f;
            height = 14f;
            sprite = "missile";

            reloadMultiplier = 0.8f;
            ammoMultiplier = 4f;

            lifetime = 55f;
            drag = 0.003f;

            pierce = true;
            pierceCap = 2;

            splashDamage = 45f;
            splashDamageRadius = 35f;

            trailWidth = 2f;
            trailLength = 12;
            trailEffect = Fx.artilleryTrail;
            trailInterval = 3f;

            hitEffect = despawnEffect = Fx.blastExplosion;

            lightRadius = 30f;
            lightColor = Pal.graphiteAmmoBack;
            lightOpacity = 0.5f;
        }};

        downpourTitanium = new BasicBulletType(10f, 110f) {{
//            backColor = trailColor = hitColor = Pal.titaniumAmmoBack;
//            frontColor = Pal.titaniumAmmoFront;

            width = 8f;
            height = 14f;
            sprite = "missile";

            ammoMultiplier = 2f;

            lifetime = 50f;
            drag = 0.002f;

            pierce = true;
            pierceCap = 3;
            pierceArmor = true;

            splashDamage = 20f;
            splashDamageRadius = 40f;

            trailWidth = 2f;
            trailLength = 12;
            trailEffect = Fx.artilleryTrail;
            trailInterval = 3f;

            hitEffect = despawnEffect = new MultiEffect(
                    Fx.blastExplosion,
                    new WaveEffect() {{
//                        colorFrom = Pal.titaniumAmmoBack;
//                        colorTo = Pal.titaniumAmmoFront;
                        sizeTo = 35f;
                        lifetime = 12f;
                        strokeFrom = 3f;
                    }}
            );

            lightRadius = 35f;
//            lightColor = Pal.titaniumAmmoFront;
            lightOpacity = 0.6f;
        }};

        downpourBlast = new MissileBulletType(8f, 100f) {{
            backColor = trailColor = hitColor = Pal.blastAmmoBack;
            frontColor = Pal.blastAmmoFront;

            width = 9f;
            height = 16f;
            sprite = "missile-large";

            lifetime = 60f;
            drag = 0.004f;

            reloadMultiplier = 0.9f;

            homingPower = 0.02f;
            homingRange = 40f;

            splashDamage = 140f;
            splashDamageRadius = 45f;

            status = StatusEffects.blasted;
            statusDuration = 900f;

            trailWidth = 2.5f;
            trailLength = 15;
            trailEffect = Fx.missileTrail;

            hitEffect = despawnEffect = new MultiEffect(
                    Fx.blastExplosion,
                    Fx.shockwave,
                    new Effect(25f, e -> {
                        Draw.color(Pal.blastAmmoBack);
                        Lines.stroke(2.5f * e.fout());
                        Lines.circle(e.x, e.y, 40f * e.finpow());
                    })
            );

            lightRadius = 45f;
            lightColor = Pal.blastAmmoBack;
            lightOpacity = 0.7f;
        }};
        //endregion

        //region MLRS Hail Frags
        hailBlastFrag = new BombBulletType(100f, 25f) {{
            width = 8f;
            height = 12f;
            lifetime = 40f;
            splashDamage = 450f;
            splashDamageRadius = 35f;
            collidesTiles = true;
            collidesAir = true;
            buildingDamageMultiplier = 1f;

            trailColor = Pal.blastAmmoBack;
            trailWidth = 2.5f;
            trailLength = 12;

//            hitEffect = despawnEffect = IAFx.Explosions.hailBlast;

        }};

        hailTitaniumFrag = new BasicBulletType(5f, 100f) {{
            width = 5f;
            height = 8f;
            lifetime = 25f;
            splashDamage = 70f;
            splashDamageRadius = 20f;
            buildingDamageMultiplier = 0.5f;
            trailWidth = 1.5f;
            trailLength = 8;
            backColor = Pal.blastAmmoBack;
            frontColor = Color.white;
            hitEffect = Fx.explosion;
        }};

        hailStrontiumFrag = new BombBulletType(120f, 40f) {{
            width = 9f;
            height = 14f;
            lifetime = 40f;
            splashDamage = 600f;
            splashDamageRadius = 45f;
            collidesTiles = true;
            collidesAir = true;
            buildingDamageMultiplier = 1.2f;

            trailColor = IAPal.strontiumAmmoBack;
            trailWidth = 3f;
            trailLength = 14;

            status = InfernoStatusEffects.radiationMedium;
            statusDuration = 600f;

            hitEffect = despawnEffect = new MultiEffect(
//                    IAFx.Explosions.reactorFragBlast,
                    Fx.fire
            );
        }};
        //endregion

        //region MLRS Hail Missiles
        hailBlastMissile = new BasicBulletType(12f, 250f) {{
            shootEffect = Fx.shootBig;
            smokeEffect = Fx.shootSmokeMissile;
            hitColor = Pal.blastAmmoFront;

            reloadMultiplier = 1f;
            ammoMultiplier = 1f;

            width = 9f;
            height = 21f;
            sprite = "missile-large";

            pierce = true;
            pierceCap = 5;
            pierceBuilding = true;
            pierceArmor = true;

            lifetime = 80f;
            drag = 0f;
            keepVelocity = true;

            trailColor = Pal.blastAmmoBack;
            trailWidth = 2.8f;
            trailLength = 20;
            trailEffect = Fx.artilleryTrail;
            trailInterval = 3f;

            splashDamage = 100f;
            splashDamageRadius = 130f;
            buildingDamageMultiplier = 0.6f;

            status = StatusEffects.blasted;
            statusDuration = 1200f;

            hitEffect = despawnEffect = IAFx.Explosions.hailBlast;

            fragOnHit = false;
            fragBullets = 4;
            fragRandomSpread = 15f;
            fragSpread = 20f;
            fragVelocityMin = 0.8f;
            fragVelocityMax = 1.2f;
            fragLifeMin = 0.2f;
            fragBullet = hailBlastFrag;

            hitSound = Sounds.explosion;
            hitSoundVolume = 2f;

            lightRadius = 40f;
            lightColor = Pal.blastAmmoFront;
            lightOpacity = 0.7f;

            backColor = Pal.blastAmmoBack;
            frontColor = Color.white;
        }};

        hailTitaniumMissile = new BasicBulletType(12f, 200f) {{
            shootEffect = Fx.shootBig;
            smokeEffect = Fx.shootSmokeMissile;

            reloadMultiplier = 0.8f;

            width = 9f;
            height = 21f;
            sprite = "missile";

            pierce = true;
            pierceCap = 5;
            pierceBuilding = true;
            pierceArmor = true;

            lifetime = 80f;
            drag = 0f;
            keepVelocity = true;

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

            fragOnHit = false;
            fragBullets = 2;
            fragRandomSpread = 20f;
            fragSpread = 30f;
            fragVelocityMin = 0.8f;
            fragVelocityMax = 1.2f;
            fragLifeMin = 0.2f;
            fragBullet = hailTitaniumFrag;

            hitSound = Sounds.explosion;
            hitSoundVolume = 2f;

            lightRadius = 40f;
            lightColor = Color.valueOf("#ffd37f");
            lightOpacity = 0.7f;

            frontColor = Color.white;
        }};

        hailStrontiumMissile = new BasicBulletType(13f, 350f) {{
            shootEffect = Fx.shootBig;
            smokeEffect = Fx.shootSmokeMissile;
            hitColor = IAPal.strontiumAmmoFront;

            reloadMultiplier = 0.55f;
            ammoMultiplier = 1f;

            width = 10f;
            height = 23f;
            sprite = "missile-large";

            pierce = true;
            pierceCap = 7;
            pierceBuilding = true;
            pierceArmor = true;

            lifetime = 75f;
            drag = 0f;
            keepVelocity = true;

            trailColor = IAPal.strontiumAmmoBack;
            trailWidth = 3f;
            trailLength = 22;
            trailEffect = Fx.artilleryTrail;
            trailInterval = 3f;

            splashDamage = 150f;
            splashDamageRadius = 160f;
            buildingDamageMultiplier = 0.7f;

            status = StatusEffects.blasted;
            statusDuration = 1500f;

            hitEffect = despawnEffect = new MultiEffect(
                    IAFx.Explosions.hailBlast,
                    IAFx.Explosions.reactorFragBlast
            );

            fragOnHit = true;
            fragBullets = 6;
            fragRandomSpread = 20f;
            fragSpread = 25f;
            fragVelocityMin = 0.8f;
            fragVelocityMax = 1.3f;
            fragLifeMin = 0.25f;
            fragBullet = hailStrontiumFrag;

            hitSound = Sounds.explosion;
            hitSoundVolume = 2.5f;

            lightRadius = 40f;
            lightColor = IAPal.strontiumAmmoBack;
            lightOpacity = 0.85f;

            backColor = IAPal.strontiumAmmoBack;
            frontColor = IAPal.strontiumAmmoFront;
        }};
        //endregion
        reactorBlastFrag = new BasicBulletType(10f, 25f) {{
            width = 8f;
            height = 12f;
            lifetime = 15f;
            splashDamage = 450f;
            splashDamageRadius = 35f;
            collidesTiles = true;
            collidesAir = true;
            buildingDamageMultiplier = 1f;
            backColor = IAPal.strontiumAmmoBack;
            frontColor = IAPal.strontiumAmmoFront;

            trailColor = Pal.blastAmmoBack;
            trailWidth = 2.5f;
            trailLength = 12;

            hitEffect = despawnEffect = IAFx.Explosions.reactorFragBlast;

        }};

        //region Vanilla Integration
        vanillaDuoRefractoryBronze = new BasicBulletType(3f, 14) {{
            width = 7f;
            height = 10f;
            lifetime = 60f;
            ammoMultiplier = 2;
//            hitColor = backColor = trailColor = Color.valueOf("#b57b4a");
//            frontColor = Color.valueOf("#d9a57a");
            hitEffect = despawnEffect = Fx.hitBulletColor;
        }};

        vanillaSalvoRefractoryBronze = new BasicBulletType(4f, 26) {{
            width = 9f;
            height = 12f;
            lifetime = 45f;
            ammoMultiplier = 4;
//            hitColor = backColor = trailColor = Color.valueOf("#b57b4a");
//            frontColor = Color.valueOf("#d9a57a");
            hitEffect = despawnEffect = Fx.hitBulletColor;
        }};

        vanillaSalvoBarium133 = new BasicBulletType(4.5f, 32) {{
            width = 9f;
            height = 12f;
            lifetime = 45f;
            ammoMultiplier = 3;
            hitColor = backColor = trailColor = IAPal.barium133AmmoBack;
            frontColor = Color.white;
            hitEffect = despawnEffect = Fx.hitBulletColor;
            status = StatusEffects.melting;
            statusDuration = 60f * 1f;
        }};

        vanillaFuseStrontium = new ShrapnelBulletType() {{
            length = 120f;
            damage = 130f;
            ammoMultiplier = 3f;
            toColor = IAPal.strontiumAmmoFront;
            fromColor = IAPal.strontiumAmmoBack;
            shootEffect = smokeEffect = Fx.thoriumShoot;
            status = StatusEffects.melting;
            statusDuration = 60f * 3f;
        }};

        vanillaRippleBarium133 = new ArtilleryBulletType(3.2f, 100) {{
            hitEffect = Fx.flakExplosion;
            knockback = 1f;
            lifetime = 80f;
            width = 12f;
            height = 14f;
            splashDamageRadius = 35f;
            splashDamage = 45f;
            hitColor = backColor = trailColor = IAPal.barium133AmmoBack;
            frontColor = Color.white;
            status = StatusEffects.melting;
            statusDuration = 60f * 1f;
        }};

        vanillaSwarmerCesium137 = new MissileBulletType(3.7f, 11) {{
            width = 8f;
            height = 12f;
            hitSize = 6f;
            lifetime = 55f;
            ammoMultiplier = 4;
            hitColor = backColor = trailColor = IAPal.cesium137AmmoBack;
            frontColor = IAPal.cesium137AmmoFront;
            hitEffect = Fx.blastExplosion;
            despawnEffect = Fx.hitBulletColor;
            splashDamageRadius = 25f;
            splashDamage = 50f;
            homingPower = 0.1f;
            homingRange = 120f;
            status = StatusEffects.burning;
            statusDuration = 60f * 2f;
        }};
        //endregion

        //region units
        hornetMissle = new MissileBulletType(8f, 20f) {{
            backColor = trailColor = hitColor = Pal.unitBack;
            frontColor = Pal.unitFront;

            width = 5f;
            height = 13f;
            sprite = "missile";

            lifetime = 40f;
            drag = 0.004f;

            homingPower = 0.2f;
            homingRange = 40f;

            splashDamage = 30f;
            splashDamageRadius = 35f;

            status = StatusEffects.burning;
            statusDuration = 120f;

            trailWidth = 2.5f;
            trailLength = 10;

            hitEffect = despawnEffect = new MultiEffect(
                    Fx.blastExplosion,
                    Fx.shockwave
            );

            lightRadius = 25f;
            lightColor = Pal.lightPyraFlame;
            lightOpacity = 0.7f;
        }};

        falconMissle = new MissileBulletType(6f, 150f) {{
            backColor = trailColor = hitColor = Pal.unitBack;
            frontColor = Pal.unitFront;

            width = 7f;
            height = 19f;
            sprite = "missile";

            lifetime = 40f;
            drag = 0.004f;

            homingPower = 0.4f;
            homingRange = 120f;

            splashDamage = 30f;
            splashDamageRadius = 35f;

            status = StatusEffects.burning;
            statusDuration = 130f;

            trailWidth = 2.5f;
            trailLength = 6;

            hitEffect = despawnEffect = new MultiEffect(
                    Fx.blastExplosion,
                    Fx.shockwave
            );

            lightRadius = 25f;
            lightColor = Pal.lightPyraFlame;
            lightOpacity = 0.7f;
        }};
    }
}