package infernoadvanced.world.blocks.power;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.graphics.IAPal;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.EventType.Trigger;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.power.NuclearReactor;

import static mindustry.Vars.tilesize;

public class StrontiumReactor extends NuclearReactor {

    //TODO full redesign and swich mechanic

    public BulletType fragBullet = new BasicBulletType(6f, 50f) {{
        width = 8f;
        height = 8f;
        lifetime = 60f;
        splashDamage = 60f;
        splashDamageRadius = 35f;
        hitEffect = IAFx.Explosions.reactorFragBlast;
        despawnEffect = IAFx.Explosions.reactorFragBlast;
        backColor = IAPal.strontiumGlow;
        frontColor = Color.white;
        shrinkY = 0.5f;
    }};

    public StrontiumReactor(String name) {
        super(name);
        explodeEffect = new MultiEffect(
                IAFx.Explosions.strontiumReactorExplosion,
                IAFx.Explosions.reactorBlast
        );
        lightColor = IAPal.strontiumGlow;
        hotColor = Color.valueOf("ff6b5e");
    }

    public class StrontiumReactorBuild extends NuclearReactorBuild {
        float pulse;

        @Override
        public void updateTile() {
            int fuel = items.get(fuelItem);
            float fullness = (float) fuel / itemCapacity;
            productionEfficiency = fullness;

            if (fuel > 0 && enabled) {
                heat += heatLastFrame = fullness * heating * Math.min(Time.delta, 4f);

                if (timer(timerFuel, itemDuration / (timeScale + (heat > heatLastFrame ? heat * heatConsumeRate : 0f)))) {
                    consume();
                }
            } else {
                productionEfficiency = 0f;
                heat = Math.max(0f, heat - Time.delta / ambientCooldownTime);
            }

            if (heat > 0) {
                float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquids.current(), maxUsed);
            }

            if (heat > smokeThreshold) {
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold);
                if (Mathf.chance(smoke / 20.0 * Time.delta)) {
                    IAFx.Particles.reactorSmokeStrontium.at(
                            x + Mathf.range(size * tilesize / 2f),
                            y + Mathf.range(size * tilesize / 2f)
                    );
                }
            }

            heat = Mathf.clamp(heat, 0f, 1f);
            heatProgress = heatOutput > 0f
                    ? Mathf.approachDelta(heatProgress, heat * heatOutput * ((enabled && productionEfficiency > 0) ? 1f : 0f), heatWarmupRate * Time.delta)
                    : 0f;

            if (heat >= 0.999f) {
                Events.fire(Trigger.thoriumReactorOverheat);
                kill();
            }

            pulse += Time.delta * (0.02f + heat * 0.06f);
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            if (heat > 0.4f && Mathf.chance(heat)) {
                for (int i = 0; i < 10; i++) {
                    float angle = Mathf.random(360f);
                    fragBullet.create(this, Team.derelict, x, y, angle, 1f, 1f);
                }
            }
        }

        @Override
        public void drawLight() {
            super.drawLight();
            float ringAlpha = 0.25f + Mathf.absin(pulse, 6f, 0.15f);
            Drawf.light(x, y, 60f + heat * 40f, IAPal.strontiumGlow, ringAlpha * (0.4f + productionEfficiency * 0.6f));
        }
    }
}