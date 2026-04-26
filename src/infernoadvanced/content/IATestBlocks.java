package infernoadvanced.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.*;
import mindustry.entities.part.*;
import infernoadvanced.entities.bullet.*;
import infernoadvanced.graphics.*;

import static mindustry.Vars.*;

public class IATestBlocks {
    public static Block horizon;
    //    public static Block singularityCannon;
    public static Block shockwaveTest;

    public static void load() {
        horizon = new ItemTurret("horizon") {{
            requirements(Category.turret, ItemStack.with(Items.copper, 150, Items.graphite, 100));

            ammo(
                    Items.graphite, new BlackHoleBulletType(1f, 50f) {{
                        pullRadius = 150f;
                        pullForce = 3f;
                        shaderRadius = 50f;
                        innerRadius = 20f;
                        ringRadius = 40f;
                        lifetime = 10f * 60f;
                    }}
            );

            size = 3;
            range = 280f;
            reload = 100f;
        }};

        // ========== SINGULARITY CANNON — POWER TURRET ==========
//        singularityCannon = new PowerTurret("singularity-cannon") {{
//            requirements(Category.turret, ItemStack.with(
//                    Items.surgeAlloy, 800,
//                    Items.phaseFabric, 500,
//                    Items.thorium, 1000,
//                    Items.silicon, 1200,
//                    Items.plastanium, 400
//            ));
//
//            localizedName = "Singularity Cannon";
//            description = "Заряжает сингулярность чистой энергией и выпускает чёрную дыру, проходящую сквозь врагов. Игнорирует броню и искажает пространство.";
//
//            size = 6;
//            health = 8500;
//            armor = 15;
//
//
//
//            reload = 420f;
//            range = 520f;
//            shootCone = 2f;
//            rotateSpeed = 1.2f;
//            recoil = 8f;
//            shake = 12f;
//
////            chargeEffect = IAFx.chargeSingularity;
////            chargeTime = 60f;
////            chargeColor = Color.valueOf("#ff5533");
//
//            consumePower(180f);
//
//            shootSound = Sounds.shootForeshadow;
//            chargeSound = Sounds.chargeVela;
//            shootSoundVolume = 2f;
//            float chargeSoundVolume = 1.5f;
//
//            shootType = new BlackHoleBulletType(0.5f, 15500f) {{
//                pierce = true;
//                pierceCap = 15;
//                pierceBuilding = true;
//                pierceArmor = true;
//                despawnHit = false;
//                keepVelocity = true;
//
//                lifetime = 60f * 60;
//
//            }};
//
//            drawer = new DrawTurret("singularity-") {{
//                parts.add(new RegionPart("-charge-ring") {{
//                    progress = PartProgress.warmup;
//                    moveRot = -360f;
//                    rotateSpeed = 2f;
//                    under = false;
//                    layer = Layer.effect;
//                }});
//
//                parts.add(new RegionPart("-barrel") {{
//                    progress = PartProgress.recoil;
//                    moveY = -8f;
//                    heatColor = Color.valueOf("#ff4422");
//                }});
//            }};
//
//            shootEffect = IAFx.shootSingularity;
//            smokeEffect = IAFx.shootSingularityAlt;
//
//            outlineColor = Color.valueOf("#2a1a0a");
//            heatColor = Color.valueOf("#ff4422");
//
//            limitRange(45f);
//        }};




//        shockwaveTest = new ItemTurret("shockwave-Test") {{
//            requirements(Category.turret, ItemStack.with(
//                    Items.titanium, 300,
//                    Items.silicon, 200,
//                    Items.blastCompound, 150
//            ));
//
//            size = 3;
//            range = 240f;
//            reload = 180f;
//
//            ammo(
//                    Items.blastCompound, new ShockwaveBulletType(120f, 200f, 12f) {{
//                        waveRadius = 500f;
//                        waveDamage = 20000f;
//                        knockback = 12f;
//                        waveDuration = 20f;
//                        strength = 1f;
//                    }}
//            );
//
//            shootSound = Sounds.explosion;
//        }};
    }
}