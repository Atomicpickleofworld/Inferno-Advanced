package infernoadvanced.entities.bullet;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;

import static infernoadvanced.graphics.IAFx.rand;

public class PushBulletType extends BulletType {

    public float knockbackStrength = 3f;
    public float damage = 5f;
    public float slowAmount = 0.5f;
    public float slowDuration = 60f * 3f;
    public float sizeGrowth = 0.5f;
    public float maxSize = 40f;
    public float minSize = 8f;

    public float pushInterval = 10f;

    public Color pushBackColor = Color.valueOf("6ec9ff");
    public Color pushFrontColor = Color.white;

    private static final Rand rand = new Rand();

    public int arcSegments = 3;
    public int streamParticles = 10;
    public float pulseSpeed = 6f;
    public float pulseMag = 0.12f;
    public float waveInterval = 14f;
    public float streamTime = 30f;

    public PushBulletType(float speed, float lifetime) {

        super(speed, 0f);
        this.lifetime = lifetime;
        this.collidesAir = true;
        this.collidesGround = true;
        this.collidesTiles = false;
        this.hittable = false;
        this.absorbable = false;
        this.pierce = true;
        this.keepVelocity = true;
        this.drag = 0f;
        this.collides = false;

        this.trailWidth = 3f;
        this.trailLength = 8;
        this.trailColor = pushBackColor;
        this.trailInterval = 5f;
        this.trailEffect = new Effect(22f, e -> {
            Draw.color(pushBackColor, e.fout() * 0.4f);
            Lines.stroke(1.4f * e.fout() + 0.2f);
            Lines.circle(e.x, e.y, 3f + e.fin() * 9f);
        });

        this.shootEffect = Fx.shootSmall;
        this.hitEffect = Fx.none;
        this.despawnEffect = new Effect(20f, e -> {
            Draw.color(pushBackColor, e.fout());
            Lines.stroke(2f * e.fout());
            Lines.circle(e.x, e.y, e.fin() * 40f);
            Draw.color(pushFrontColor, e.fout());
            for (int i = 0; i < 4; i++) {
                float angle = i * 90f + e.rotation;
                Drawf.tri(e.x, e.y, 1f, 15f * e.fout(), angle);
            }
        });
        this.reflectable = false;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if (b.data == null) {
            b.data = minSize;
        }

        float currentSize = (Float) b.data;
        currentSize = Math.min(currentSize + sizeGrowth, maxSize);
        b.data = currentSize;
        b.hitSize = currentSize;

        float pushRadius = currentSize * 1.5f;
        float progress = b.time / b.lifetime;

        float forwardX = Angles.trnsx(b.rotation(), 1f);
        float forwardY = Angles.trnsy(b.rotation(), 1f);

        Units.nearbyEnemies(b.team, b.x, b.y, pushRadius, unit -> {
            float dx = unit.x - b.x;
            float dy = unit.y - b.y;
            float dist = Mathf.len(dx, dy);
            if(dist < 0.01f) return;

            float dot = dx * forwardX + dy * forwardY;

            float perpX = dx - forwardX * dot;
            float perpY = dy - forwardY * dot;
            float perpLen = Mathf.len(perpX, perpY);

            if(perpLen > 0.01f){
                perpX /= perpLen;
                perpY /= perpLen;
            }

            float distanceFactor = 1f - Mathf.clamp(dist / pushRadius, 0f, 1f);
            float armorFactor = 1f / (1f + unit.armor() * 0.2f);
            float timeFactor = 1f - progress * 0.6f;
            float force = knockbackStrength * distanceFactor * armorFactor * timeFactor;

            float pushX = perpX * force + forwardX * force * 0.35f;
            float pushY = perpY * force + forwardY * force * 0.35f;

            unit.vel.add(pushX, pushY);

            if(b.timer(1, pushInterval)){
                unit.damage(damage);
                unit.apply(StatusEffects.slow, slowDuration);
            }
        });
    }

    @Override
    public void draw(Bullet b) {
        float size = b.data instanceof Float f ? f : minSize;
        float rot = b.rotation();
        float pulse = 1f + Mathf.absin(b.time, pulseSpeed, pulseMag);

        for (int i = 0; i < 2; i++) {
            float t = ((b.time + i * waveInterval / 2f) % waveInterval) / waveInterval;
            Draw.color(pushBackColor, (1f - t) * 0.45f);
            Lines.stroke(2.2f * (1f - t) + 0.3f);
            Lines.circle(b.x, b.y, size * (0.9f + t * 1.7f));
        }

        Draw.blend(Blending.additive);

        Draw.color(pushBackColor, 0.3f);
        Fill.circle(b.x, b.y, size * 1.3f * pulse);

        Draw.color(pushBackColor, 0.85f);
        Lines.stroke(2f);
        for (int i = 0; i < arcSegments; i++) {
            Lines.arc(b.x, b.y, size * 1.15f, 0.22f, b.time * 4f + i * 360f / arcSegments);
        }
        Draw.color(pushFrontColor, 0.6f);
        Lines.stroke(1.4f);
        for (int i = 0; i < 2; i++) {
            Lines.arc(b.x, b.y, size * 0.85f, 0.3f, -b.time * 6f + i * 180f);
        }

        Draw.color(pushBackColor, 0.8f);
        Fill.circle(b.x, b.y, size * 0.72f);
        Draw.color(pushFrontColor, 0.95f);
        Fill.circle(b.x, b.y, size * 0.38f * pulse);

        Draw.color(pushFrontColor, 0.55f);
        Lines.stroke(2f);
        Lines.arc(b.x, b.y, size * 1.55f, 0.16f, rot - 28.8f);
        Draw.color(pushBackColor, 0.35f);
        Lines.stroke(1.4f);
        Lines.arc(b.x, b.y, size * 1.85f, 0.22f, rot - 39.6f);

        rand.setSeed(b.id);
        for (int i = 0; i < streamParticles; i++) {
            float speed = rand.random(0.6f, 1.4f);
            float phase = rand.random(1f);
            float baseAngle = rand.random(360f);
            float cyc = (b.time * speed / streamTime + phase) % 1f;
            float angle = baseAngle + b.time * 1.5f;
            float dist = size * (0.9f + cyc * 1.5f);

            Draw.color(pushFrontColor, pushBackColor, cyc);
            Draw.alpha((1f - cyc) * Mathf.clamp(cyc / 0.15f) * 0.7f);
            Lines.stroke(1.6f * (1f - cyc) + 0.4f);
            Lines.lineAngle(b.x + Angles.trnsx(angle, dist), b.y + Angles.trnsy(angle, dist), angle, 3.5f * (1f - cyc) + 1f);
        }

        Drawf.light(b.x, b.y, size * 3.2f, pushBackColor, 0.55f);

        Draw.blend();
        Draw.reset();
    }

    @Override
    public void hit(Bullet b, float x, float y) {
    }
}