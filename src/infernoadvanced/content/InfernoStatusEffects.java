package infernoadvanced.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import infernoadvanced.entities.status.TieredStatusEffect;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.graphics.IAPal;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.content.TechTree;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;

public class InfernoStatusEffects {

    public static StatusEffect

    radiationWeak, radiationMedium, radiationStrong,

    empLow, empMedium, empHigh, empShock,

    blindingWeak, blindingMedium, blindingStrong,

    servoPartial, servoFull,

    gravityCollapse, battleHunger
    ;

    public static void load() {

        radiationWeak = new TieredStatusEffect("radiation-weak", "radiation", 1) {{
            color = Pal.thoriumPink;
            show = true;

            healthMultiplier = 0.95f;

            effectChance = 0.03f;
            transitionDamage = 5f;
            effect = IAFx.Radiation.weak;

            init(() -> {
                opposite(StatusEffects.wet);

                affinity(StatusEffects.blasted, (unit, result, time) -> {
                    result.set(radiationMedium, time * 1.5f);
                });
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    result.set(radiationMedium, time * 1.3f);
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        radiationMedium = new TieredStatusEffect("radiation-medium", "radiation", 2) {{
            color = IAPal.barium133;
            show = true;

            damage = 0.5f;
            healthMultiplier = 0.85f;
            speedMultiplier = 0.9f;

            effectChance = 0.15f;
            transitionDamage = 10f;
            effect = Fx.smoke;

            init(() -> {
                opposite(StatusEffects.wet);

                affinity(StatusEffects.blasted, (unit, result, time) -> {
                    result.set(radiationStrong, time * 1.4f);
                });
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    result.set(radiationStrong, time * 1.2f);
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        radiationStrong = new TieredStatusEffect("radiation-strong", "radiation", 3) {{
            color = IAPal.barium137m;
            show = true;

            damage = 1.2f;
            healthMultiplier = 0.7f;
            speedMultiplier = 0.75f;
            damageMultiplier = 0.8f;

            effectChance = 0.2f;
            transitionDamage = 20f;

            final Color[] radColors = {
                    Color.valueOf("3a3a3a"),
                    Color.valueOf("c82020"),
                    Color.valueOf("6b0000"),
                    Color.valueOf("a83800")
            };

            effect = IAFx.Radiation.strong;

            init(() -> {
                opposite(StatusEffects.overclock);

                affinity(StatusEffects.blasted, (unit, result, time) -> {
                    unit.damage(transitionDamage);
                    result.time = Math.min(time + result.time, 400f);
                });
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    unit.damage(transitionDamage);
                    result.time = Math.min(time + result.time, 300f);
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        empLow    = new TieredStatusEffect("emp-low",    "emp", 1) {{
            color = IAPal.empLow;

            speedMultiplier = 0.75f;
            reloadMultiplier = 0.65f;
            damageMultiplier = 0.9f;
            buildSpeedMultiplier = 0.75f;
            dragMultiplier = 1.1f;

            show = true;
            outline = false;

            effectChance = 0.08f;
            effect = Fx.overclocked;
            applyEffect = Fx.none;

            init(() -> {
                affinity(StatusEffects.wet, (unit, result, time) -> {
                    unit.damagePierce(2f);
                    result.time += time * 0.2f;
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        empMedium = new TieredStatusEffect("emp-medium", "emp", 2) {{
            color = IAPal.empMedium;

            speedMultiplier = 0.5f;
            reloadMultiplier = 0.4f;
            damageMultiplier = 0.75f;
            buildSpeedMultiplier = 0.5f;
            dragMultiplier = 1.2f;

            show = true;
            outline = false;

            intervalDamageTime = 60f;
            intervalDamage = 1f;
            intervalDamagePierce = false;

            effectChance = 0.12f;
            effect = Fx.overclocked;
            applyEffect = Fx.hitEmpSpark;

            init(() -> {
                affinity(StatusEffects.wet, (unit, result, time) -> {
                    unit.damagePierce(4f);
                    result.time += time * 0.3f;
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        empHigh   = new TieredStatusEffect("emp-high",   "emp", 3) {{
            color = IAPal.empHigh;

            speedMultiplier = 0.25f;
            reloadMultiplier = 0.2f;
            damageMultiplier = 0.5f;
            buildSpeedMultiplier = 0.2f;
            dragMultiplier = 1.5f;

            show = true;
            outline = false;

            intervalDamageTime = 30f;
            intervalDamage = 2f;
            intervalDamagePierce = true;

            effectChance = 0.2f;
            effect = Fx.overclocked;
            applyEffect = Fx.hitEmpSpark;

            init(() -> {
                affinity(StatusEffects.wet, (unit, result, time) -> {
                    unit.damagePierce(8f);
                    result.time += time * 0.5f;
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        empShock   = new TieredStatusEffect("emp-shock",   "emp", 4) {{
            color = IAPal.empHigh;

            disarm = true;
            buildSpeedMultiplier = 0f;
            speedMultiplier = 0f;

            show = true;
            outline = false;

            effectChance = 0.2f;
            effect = Fx.overclocked;
            applyEffect = Fx.hitEmpSpark;

            init(() -> {
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        gravityCollapse = new StatusEffect("gravity-collapse") {{
            color = IAPal.blackHoleArc;
            speedMultiplier = 0.6f;
            reloadMultiplier = 0.7f;
            healthMultiplier = 0.85f;

            show = true;

            intervalDamage = 10f;
            intervalDamageTime = 60f;

            effect = IAFx.Particles.gravityCollapse;
            effectChance = 0.05f;

            init(() -> {
                opposite(StatusEffects.freezing);
                opposite(StatusEffects.wet);
                affinity(StatusEffects.burning, (unit, result, time) -> {
                    unit.damage(25f);
                    result.set(gravityCollapse, Math.min(time + result.time, 120f));
                });
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        battleHunger = new StatusEffect("battle-hunger") {{
            color = Color.scarlet;

            show = true;

            speedMultiplier = 1.35F;
            damageMultiplier = 1.05F;
            reloadMultiplier = 2F;
            effectChance = 0.07F;
            effect = Fx.overclocked;

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};






//sync with initialization
        for(StatusEffect s : new StatusEffect[]{

                radiationWeak, radiationMedium, radiationStrong,

                empLow, empMedium, empHigh, empShock,

                blindingWeak, blindingMedium, blindingStrong,

                servoPartial, servoFull,

                gravityCollapse, battleHunger
        }){
            s.allDatabaseTabs = true;
        }
    }
}