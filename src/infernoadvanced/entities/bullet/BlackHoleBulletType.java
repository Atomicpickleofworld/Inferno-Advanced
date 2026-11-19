package infernoadvanced.entities.bullet;

import arc.audio.Sound;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import infernoadvanced.content.InfernoStatusEffects;
import infernoadvanced.graphics.IAFx;
import infernoadvanced.graphics.IAPal;
import infernoadvanced.graphics.blackhole.IABlackholeRenderer;
import infernoadvanced.loaders.IASounds;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class BlackHoleBulletType extends BasicBulletType {
    public float pullRadius = 120f;
    public float pullForce = 2.5f;
    public float shaderRadius = 35f;
    public float innerRadius = 20f;
    public float ringRadius = 55f;

    public Sound loopSound = Sounds.none;
    public float loopSoundVolume = 0.8f;
    public float loopSoundPitch = 0.7f;

    public Color coreColor = IAPal.blackHoleCore;
    public Color innerRingColor = IAPal.blackHoleInnerRing;
    public Color outerRingColor = IAPal.blackHoleOuterRing;
    public Color arcColor = IAPal.blackHoleArc;
    public Color glowColor = IAPal.blackHoleGlow;

    public BlackHoleBulletType(float speed, float damage) {
        super(speed, damage);
        sprite = "circle";
        width = 28f;
        height = 28f;
        frontColor = Color.black;
        backColor = Color.purple;
        hitSize = 100f;
        lifetime = 20f * 60f;

        pullRadius = 320f;
        pullForce = 100f;
        shaderRadius = 140f;
        innerRadius = 24f;
        ringRadius = 50f;

        splashDamageRadius = 120f;

        if (status == null) {
            status = InfernoStatusEffects.gravityCollapse;
        }
        statusDuration = 60f * 10f;

        pierce = true;
        pierceCap = 10;
        pierceBuilding = true;
        pierceArmor = true;
        despawnHit = false;
        keepVelocity = true;

        loopSound = IASounds.blackholeLoop;
        loopSoundVolume = 0.7f;
        loopSoundPitch = 0.6f;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if (!Vars.headless && loopSound != null && loopSound != Sounds.none) {
            Vars.control.sound.loop(loopSound, b, loopSoundVolume, loopSoundPitch);
        }
        if (!Vars.headless) {
            IAFx.Turrets.singularityUnfold.at(b.x, b.y);
        }
    }


    @Override
    public void update(Bullet b) {
        super.update(b);

        if (!Vars.headless && loopSound != null && loopSound != Sounds.none) {
            Vars.control.sound.loop(loopSound, b, loopSoundVolume, loopSoundPitch);
        }

        if (!Vars.headless && b.timer(2, 4f)) {
            IAFx.Turrets.singularityAccretion.at(b.x, b.y);
        }

        if (b.timer(1, 2f)) {
            Units.nearbyEnemies(b.team, b.x - pullRadius, b.y - pullRadius, pullRadius * 2, pullRadius * 2, unit -> {
                if (unit.within(b.x, b.y, pullRadius)) {
                    float dist = unit.dst(b);
                    float strength = pullForce * (1f + (pullRadius - dist) / pullRadius * 3f);
                    float angle = unit.angleTo(b);

                    unit.impulse(Tmp.v1.trns(angle, strength * Time.delta));
                    unit.vel.scl(0.92f);

                    if (dist < pullRadius * 0.3f) {
                        unit.damage(5f * Time.delta);
                    }

                    if (status != null) {
                        unit.apply(status, statusDuration);
                    }
                }
            });
        }
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);

        if (!Vars.headless) {
            IAFx.Turrets.singularityCollapse.at(b.x, b.y);

            // Мощная тряска камеры
            Effect.shake(6f, 6f, b);

            Sounds.explosion.at(b.x, b.y, 1.5f, 0.5f);
        }
    }

    @Override
    public void draw(Bullet b) {
        IABlackholeRenderer.addBlackHole(b.x, b.y, innerRadius, shaderRadius, 1f);

        float life = b.time / b.lifetime;
        float rot = b.time * 3.8f;
        float pulse = Mathf.absin(b.time, 5f, 0.12f) + 0.88f;

        Draw.blend(Blending.additive);
        Draw.color(glowColor, life * 0.6f);
        Fill.circle(b.x, b.y, width / 1.2f);
        Draw.blend();

        Draw.color(innerRingColor, life * 0.15f);
        Lines.stroke(1.2f * life);
        Lines.circle(b.x, b.y, shaderRadius * 0.9f);

        Draw.color(outerRingColor, life * 0.1f);
        Lines.stroke(0.8f * life);
        Lines.circle(b.x, b.y, shaderRadius * 1.05f);

        Draw.color(innerRingColor, life * 0.9f);
        Lines.stroke(4.5f * life);
        Lines.circle(b.x, b.y, width * 0.95f);

        Draw.color(outerRingColor, life * 0.75f);
        Lines.stroke(4f * life);
        Lines.circle(b.x, b.y, ringRadius * 0.9f * pulse);

        Draw.color(arcColor, life * 0.6f);
        Lines.stroke(3.5f * life);
        Lines.circle(b.x, b.y, ringRadius * 1.05f * pulse);

        for (int i = 0; i < 6; i++) {
            float angle = rot * 1.6f + i * 60f;
            float arcSize = 90f;

            Draw.color(arcColor, innerRingColor, life * 0.8f);
            Lines.stroke(4.2f * life);
            Lines.arc(b.x, b.y, ringRadius * 0.98f, arcSize / 360f, angle - arcSize / 2);

            Draw.color(arcColor, life * 0.65f);
            Lines.stroke(3.4f * life);
            Lines.arc(b.x, b.y, ringRadius * 1.02f, arcSize / 360f, angle - arcSize / 2);
        }

        for (int i = 0; i < 6; i++) {
            float angle = rot * 1.6f + i * 60f + 30f;
            float arcSize = 90f;

            Draw.color(outerRingColor, innerRingColor, life * 0.7f);
            Lines.stroke(3.8f * life);
            Lines.arc(b.x, b.y, ringRadius * 1.0f, arcSize / 360f, angle - arcSize / 2);
        }

        Draw.color(outerRingColor, life * 0.5f);
        Lines.stroke(3f * life);
        Lines.circle(b.x, b.y, ringRadius * 0.99f);

        Draw.color(outerRingColor, life * 0.3f);
        Lines.stroke(2.2f * life);
        Lines.circle(b.x, b.y, ringRadius * 1.2f);

        Draw.color(innerRingColor, life * 0.22f);
        Lines.stroke(1.8f * life);
        Lines.circle(b.x, b.y, ringRadius * 1.3f);

        Draw.z(Layer.effect + 0.2f);
        Draw.color(coreColor);
        Fill.circle(b.x, b.y, width / 1.65f);

        Draw.blend(Blending.additive);
        Draw.color(innerRingColor, life * 0.4f);
        Fill.circle(b.x, b.y, width / 3.5f);
        Draw.blend();

        Draw.reset();
    }
}