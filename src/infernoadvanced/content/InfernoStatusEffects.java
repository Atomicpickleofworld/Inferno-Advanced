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

    empLow, empMedium, empHigh,

    gravityCollapse, battleHunger
    ;

    public static void load() {

        radiationWeak   = new TieredStatusEffect("radiation-weak",   "radiation", 1) {{
            color = Color.valueOf("b8d9a0"); // TODO: подобрать под IAPal
            show = true;

            healthMultiplier = 0.95f;

            effectChance = 0.05f;
            transitionDamage = 5f;
            effect = Fx.smokePuff;

            init(() -> {
                affinity(StatusEffects.wet, (unit, result, time) ->
                        result.time = 0f);
                affinity(StatusEffects.burning, (unit, result, time) ->
                        result.set(radiationMedium, time * 1.5f));
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        radiationMedium = new TieredStatusEffect("radiation-medium", "radiation", 2) {{
            color = Color.valueOf("8fbf60"); // TODO: подобрать под IAPal
            show = true;

            damage = 0.5f;
            healthMultiplier = 0.85f;
            speedMultiplier = 0.9f;

            effectChance = 0.15f;
            transitionDamage = 10f;
            effect = Fx.smoke;

            init(() -> {
                affinity(StatusEffects.wet, (unit, result, time) ->
                        result.set(radiationWeak, result.time));
                affinity(StatusEffects.blasted, (unit, result, time) ->
                        result.set(radiationStrong, time * 1.2f));
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        radiationStrong = new TieredStatusEffect("radiation-strong", "radiation", 3) {{
            color = IAPal.cesium137AmmoFront;
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

            effect = new Effect(40f, e -> {
                Draw.color(Color.gray, Color.lightGray, e.fin());
                Angles.randLenVectors(e.id, 3, 1f + e.fin() * 4f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.5f + e.fout() * 1.5f);
                });

                Draw.blend(Blending.additive);
                for(int i = 0; i < 4; i++){
                    float angle = Mathf.randomSeed(e.id + i, 360f);
                    float dist = Mathf.randomSeed(e.id + i + 100, 4f, 14f) * e.finpow();
                    Tmp.v1.trns(angle, dist);

                    int colorIndex = Mathf.randomSeed(e.id + i + 200, 0, 3);
                    Draw.color(radColors[colorIndex], 0.7f * e.fout());
                    Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * e.finpow() + 1f);
                }
                Draw.blend();
            }).layer(Layer.effect);

            init(() -> {
                opposite(StatusEffects.overclock);
            });

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        empLow    = new TieredStatusEffect("emp-low",    "emp", 1) {{
            color = Color.valueOf("c8e6ff");

            speedMultiplier = 0.75f;
            reloadMultiplier = 0.65f;
            damageMultiplier = 0.9f;
            buildSpeedMultiplier = 0.75f;
            dragMultiplier = 1.1f;

            show = true;
            outline = false;

            effectChance = 0.08f;
            effect = Fx.electrified;
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
            color = Color.valueOf("a3d9ff");

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
            effect = Fx.electrified;
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
            color = Color.valueOf("7ecbff");

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
            effect = Fx.electrified;
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

        gravityCollapse = new StatusEffect("gravity-collapse") {{
            color = Color.valueOf("#ff5533");
            speedMultiplier = 0.6f;
            reloadMultiplier = 0.7f;
            healthMultiplier = 0.85f;

            show = true;

            intervalDamage = 10f;
            intervalDamageTime = 60f;

            effect = IAFx.Turrets.singularityAccretion;
            effectChance = 0.3f;

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
            color = Pal.accent;

            show = true;

            speedMultiplier = 1.35F;
            damageMultiplier = 1.05F;
            reloadMultiplier = 2F;
            effectChance = 0.07F;
            effect = Fx.overclocked;

            techNode = new TechTree.TechNode(null, this, ItemStack.empty);
            alwaysUnlocked = true;
        }};

        for(StatusEffect s : new StatusEffect[]{

                radiationWeak, radiationMedium, radiationStrong,

                empLow, empMedium, empHigh,

                gravityCollapse, battleHunger

        }){
            s.allDatabaseTabs = true;
        }
    }
}