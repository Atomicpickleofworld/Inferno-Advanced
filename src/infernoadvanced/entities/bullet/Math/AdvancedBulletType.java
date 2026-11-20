package infernoadvanced.entities.bullet.Math;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;

public class AdvancedBulletType extends BulletType {

    public boolean physicsMode = false;
    public float angleUp = 45f;
    public float gravity = -0.5f;
    public float baseSpeed = 3f;


    public boolean allowCollision = false;
    public TrajectoryCalculator trajectory = TrajectoryLib.straight(3f);

    public boolean drawShadow = true;
    public float zScale = 0.6f;

    public String bulletSpriteName = "bullet";
    public String shadowSpriteName = null;

    public Sound flightSound = Sounds.none;
    public float flightSoundVolume = 1f;

    public Color backColor = Pal.bulletYellowBack;
    public Color frontColor = Pal.bulletYellow;

    private TextureRegion bulletRegion;
    private TextureRegion shadowRegion;

    private float sinAngle, cosAngle;
    private static final Vec3 tmpOffset = new Vec3();


    public static class BulletState {
        public float startX, startY;
        public float posZ = 0f;
        public float velZ = 0f;
        public float time = 0f;
        public boolean hasLanded = false;
        public float lastX, lastY;
        public float dirX = 1f, dirY = 0f;
        public float perpX = 0f, perpY = 1f;
        public transient SoundLoop flightLoop;
    }

    public AdvancedBulletType(float damage) {
        super();
        this.damage = damage;
        this.speed = 0f;
        this.drag = 0f;
        this.lifetime = 60f * 10f;
        updateAngleCaches();
        collidesAir = true;
        collidesGround = false;
        collidesTiles = false;
    }

    public void updateAngleCaches() {
        sinAngle = (float) Math.sin(Math.toRadians(angleUp));
        cosAngle = (float) Math.cos(Math.toRadians(angleUp));
    }

    @Override
    public void load() {
        super.load();
        bulletRegion = Core.atlas.find(bulletSpriteName);
        shadowRegion = (shadowSpriteName != null)
                ? Core.atlas.find(shadowSpriteName)
                : bulletRegion;
    }

    protected void stopFlightSound(SoundLoop loop, float x, float y) {
        if (loop != null) loop.update(x, y, false);
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        if (b.data instanceof BulletState state) {
            stopFlightSound(state.flightLoop, b.x, b.y);
            state.flightLoop = null;
        }
    }

    @Override
    public void update(Bullet b) {
        BulletState state = (BulletState) b.data;
        if (state == null) {
            state = new BulletState();
            state.startX = b.x;
            state.startY = b.y;
            state.dirX = Mathf.cosDeg(b.rotation());
            state.dirY = Mathf.sinDeg(b.rotation());
            state.perpX = -state.dirY;
            state.perpY = state.dirX;
            state.velZ = baseSpeed * sinAngle;
            state.lastX = b.x;
            state.lastY = b.y;
            if (flightSound != Sounds.none) {
                state.flightLoop = new SoundLoop(flightSound, flightSoundVolume);
            }
            b.data = state;
        }

        if (state.hasLanded) return;

        state.time += Time.delta;

        if (!physicsMode) {
            trajectory.calculate(state.time, tmpOffset);

            float prevZ = state.posZ;
            b.x = state.startX + state.dirX * tmpOffset.x + state.perpX * tmpOffset.y;
            b.y = state.startY + state.dirY * tmpOffset.x + state.perpY * tmpOffset.y;
            state.posZ = tmpOffset.z;

            boolean expired = state.time > this.lifetime || state.posZ < -100f;
            boolean landed = state.posZ <= 0f && prevZ > 0f && state.time > 1f;
            if (expired || landed) {
                if (landed) state.posZ = 0f;
                stopFlightSound(state.flightLoop, b.x, b.y - state.posZ * zScale);
                state.flightLoop = null;
                hit(b, b.x, b.y - state.posZ * zScale);
                state.hasLanded = true;
                b.remove();
                return;
            }
        } else {
            float speedHor = baseSpeed * cosAngle;
            b.x += state.dirX * speedHor * Time.delta;
            b.y += state.dirY * speedHor * Time.delta;

            state.velZ += gravity * Time.delta;
            state.posZ += state.velZ * Time.delta;

            if (state.posZ <= 0f && state.time > 0.5f) {
                state.posZ = 0f;
                stopFlightSound(state.flightLoop, b.x, b.y);
                state.flightLoop = null;
                hit(b, b.x, b.y);
                state.hasLanded = true;
                b.remove();
                return;
            }
        }

        if (!Vars.headless && trailLength > 0) {
            if (b.trail == null) b.trail = new Trail(trailLength);
            b.trail.length = trailLength;
            b.trail.update(b.x, b.y - state.posZ * zScale, trailInterp.apply(b.fin()));
        }

        updateBulletInterval(b);
        updateTrailEffects(b);

        if (b.x != state.lastX || b.y != state.lastY) {
            b.rotation(Mathf.angle(b.x - state.lastX, b.y - state.lastY));
        }
        state.lastX = b.x;
        state.lastY = b.y;
    }

    @Override
    public void draw(Bullet b) {
        BulletState state = (BulletState) b.data;
        if (state == null || state.hasLanded) return;

        drawTrail(b);

        if (drawShadow && shadowRegion != null && shadowRegion.found()) {
            Draw.z(Layer.bullet - 1);
            Draw.color(Color.black, 0.3f);
            Draw.rect(shadowRegion, b.x, b.y, hitSize, hitSize * 0.2f);
            Draw.reset();
        }

        if (bulletRegion == null || !bulletRegion.found()) return;

        Draw.z(Layer.bullet);
        float bulletY = b.y - state.posZ * zScale;
        Draw.rect(bulletRegion, b.x, bulletY, b.rotation());
    }
}