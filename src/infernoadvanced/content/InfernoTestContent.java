package infernoadvanced.content;

import arc.struct.Seq;
import arc.struct.StringMap;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.content.Items;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.game.Schematic;
import mindustry.game.Schematic.Stile;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.type.UnitType;
import mindustry.type.weapons.BuildWeapon;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

public class InfernoTestContent{
    public static UnitType testDrone;
    public static CoreBlock testCore;

    //TODO delete

    public static void load(){
        testDrone = new UnitType("inferno-test-drone"){{
            controller = u -> u.team.isAI() ? new BuilderAI(true, 400f) : new CommandAI();
            coreUnitDock = true;
            isEnemy = false;
            targetBuildingsMobile = false;

            envEnabled = Env.any;
            envDisabled = Env.none;

            flying = true;
            lowAltitude = true;
            health = 320f;
            armor = 1f;
            speed = 3.6f;
            accel = 0.1f;
            drag = 0.05f;
            rotateSpeed = 15f;
            hitSize = 9f;
            engineOffset = 6f;
            engineSize = 2.5f;
            itemCapacity = 60;
            fogRadius = 12f;

            buildSpeed = 1f;
            mineSpeed = 7f;
            mineTier = 2;
            mineWalls = true;
            mineFloor = true;
            drawBuildBeam = true;

            faceTarget = true;
            range = 60f;
            targetPriority = -2f;

            weapons.add(new BuildWeapon("build-weapon"){{
                rotate = true;
                rotateSpeed = 7f;
                x = 0f;
                y = 2f;
                layerOffset = -0.001f;
                shootY = 3f;
            }});

            abilities.add(new ForceFieldAbility(15f, 0.1f, 300f, 240f, 6, 0f) {
                {
                    this.breakSound = Sounds.shieldBreak;
                }
            });
        }};

        testCore = new CoreBlock("inferno-test-core"){{
            size = 3;
            health = 1400;
            itemCapacity = 900;
            hasItems = true;
            hasLiquids = true;

            unitType = testDrone;
            unitCapModifier = 8;
            isFirstTier = true;

            envEnabled = Env.any;
            envDisabled = Env.none;

            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.shown;
            category = Category.effect;
            requirements(Category.effect, ItemStack.with(Items.copper, 80, Items.lead, 80));
        }};
    }

    public static Schematic loadout(){
        return new Schematic(
                Seq.with(new Stile(testCore, 0, 0, null, (byte)0)),
                new StringMap(), testCore.size, testCore.size
        );
    }

    public static void assignPlanet(Planet planet){
        testCore.shownPlanets.add(planet);
        testDrone.shownPlanets.add(planet);
    }
}