package infernoadvanced.entities.abilities;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Velc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import arc.struct.Seq;
import mindustry.graphics.Drawf;

import static mindustry.Vars.indexer;

/**
 * Рывок: юнит заряжает его, затем летит по прямой к цели и дальше на
 * overshoot, нанося урон всему на пути.
 *
 * Старт: как только юнит СТРЕЛЯЕТ (и откат прошёл), рывок нацеливается на цель
 *   его стрельбы. Никаких своих поисков цели и ограничений по дальности:
 *   для ИИ это цель оружия, а если её нет, то точка прицела, для игрока точка прицела.
 * Зарядка: угловой указатель направления + мигающий спрайт цветом команды
 *   (мигание рисует RollUnitType). Указатель следит за целью.
 * Рывок: курс фиксируется и не меняется. Здания не мешают, вражеские здания и
 *   юниты получают урон, юниты расталкиваются в стороны. Управление и
 *   стрельба заблокированы (статус dash-lock + принудительная скорость).
 */
public class DashAbility extends Ability {
    /** Время зарядки, тики. */
    public float chargeTime = 60f * 1.4f;
    /** Откат после рывка, тики. */
    public float cooldown = 60f * 10f;
    /** Скорость рывка, юнитов за тик. */
    public float dashSpeed = 11f;
    /**
     * Необязательные ограничения дальности до цели стрельбы.
     * 0 = отключено (по умолчанию): рывок зависит только от стрельбы.
     */
    public float minRange = 0f, maxRange = 0f;
    /** Сколько пролетает за целью. */
    public float overshoot = 80f;
    /** Упреждение: целиться туда, где окажется движущаяся цель к моменту попадания. */
    public boolean predictTargets = true;
    /** Максимальный вынос точки упреждения от текущей позиции цели. */
    public float predictMaxLead = 200f;
    /** Максимальная длина рывка. */
    public float maxDistance = 480f;

    /** Урон за удар и интервал ударов, тики. */
    public float dashDamage = 70f;
    public float hitInterval = 5f;
    public float buildingDamageMultiplier = 0.5f;
    /** Радиус поражения = hitSize / 2 + hitPadding. */
    public float hitPadding = 14f;
    /** Сила расталкивания юнитов (зависит от массы цели). */
    public float pushForce = 420f;
    public boolean pushAllies = false;

    /** Цвет мигания и указателя; null = цвет команды. */
    public Color color = null;
    public float indicatorRadius = 64f;
    public float indicatorAngle = 50f;

    /** Постизображения */
    public float ghostInterval = 3f;
    public float ghostLifetime = 45f;
    public float ghostAlpha = 0.65f;

    /** Звук зарядки (играет с интервалом). */
    public Sound chargeSound = Sounds.chargeVela;
    /** Интервал воспроизведения звука зарядки, тики. */
    public float chargeSoundInterval = 12f;
    /** Звук начала рывка. */
    public Sound dashSound = Sounds.none;
    /** Звук попадания по врагу/зданию во время рывка. */
    public Sound hitSound = Sounds.shieldHit;

    /** Статус, блокирующий управление. Создаётся в load(). */
    public static StatusEffect lockStatus;

    public static void load() {
        lockStatus = new StatusEffect("dash-lock") {{
            show = false;
            speedMultiplier = 0f; // ввод игрока и ИИ не может менять скорость
            disarm = true;        // оружие не стреляет
        }};
    }

    protected static final int IDLE = 0, CHARGING = 1, DASHING = 2;

    protected int phase = IDLE;
    protected float timer, cooldownTime, dir, traveled, distance, hitTimer;
    protected float tx, ty;
    protected Teamc target;
    protected float ghostTimer;
    protected float chargeSoundTimer;

    protected static class DashGhost {
        float x, y, rotation;
        float lifetime;
        float scaleX;

        DashGhost(float x, float y, float rotation,
                  float lifetime, float scaleX) {
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.lifetime = lifetime;
            this.scaleX = scaleX;
        }
    }

    protected final Seq<DashGhost> ghosts = new Seq<>();

    protected float bodyScale(Unit unit) {
        for (Ability ability : unit.abilities) {
            if (ability instanceof BarrelRollAbility roll) {
                return roll.scaleX();
            }
        }
        return 1f;
    }

    @Override
    public void update(Unit unit) {
        for (int i = ghosts.size - 1; i >= 0; i--) {
            DashGhost ghost = ghosts.get(i);
            ghost.lifetime -= Time.delta;

            if (ghost.lifetime <= 0f) {
                ghosts.remove(i);
            }
        }
        switch (phase) {
            case IDLE -> updateIdle(unit);
            case CHARGING -> updateCharging(unit);
            case DASHING -> updateDashing(unit);
        }
    }

    protected Teamc shootTarget(Unit unit) {
        for (var mount : unit.mounts) {
            if (!mount.weapon.controllable) continue;
            if (mount.shoot && mount.target != null
                    && !Units.invalidateTarget(mount.target, unit.team, unit.x, unit.y)) {
                return mount.target;
            }
        }
        return null;
    }

    protected void updateIdle(Unit unit) {
        if (cooldownTime > 0f) {
            cooldownTime -= Time.delta;
            return;
        }
        if (!unit.isShooting()) return;

        Teamc t = shootTarget(unit);
        float x, y;
        if (t != null) {
            x = t.getX();
            y = t.getY();
        } else {
            x = unit.aimX;
            y = unit.aimY;
        }

        if ((Mathf.zero(x) && Mathf.zero(y)) || unit.within(x, y, 1f)) return;

        if (minRange > 0f && unit.within(x, y, minRange)) return;
        if (maxRange > 0f && !unit.within(x, y, maxRange)) return;

        target = t;
        tx = x;
        ty = y;
        dir = Angles.angle(unit.x, unit.y, tx, ty);
        timer = 0f;
        phase = CHARGING;
    }

    protected void updateAimPoint(Unit unit, Teamc t) {
        float px = t.getX(), py = t.getY();
        tx = px;
        ty = py;

        if (!predictTargets || !(t instanceof Velc mover)) return;

        Vec2 vel = mover.vel();
        if (vel.isZero(0.001f)) return;

        Vec2 p = Predict.intercept(unit.x, unit.y, px, py, vel.x, vel.y, dashSpeed);
        Tmp.v4.set(p.x - px, p.y - py).limit(predictMaxLead);
        tx = px + Tmp.v4.x;
        ty = py + Tmp.v4.y;
    }

    protected void updateCharging(Unit unit) {
        timer += Time.delta;
        chargeSoundTimer += Time.delta;

        if (chargeSoundTimer >= chargeSoundInterval) {
            chargeSoundTimer = 0f;
            chargeSound.at(unit.x, unit.y, 1f + chargeProgress() * 0.5f);
        }

        if (target != null) {
            if (!Units.invalidateTarget(target, unit.team, unit.x, unit.y)) {
                updateAimPoint(unit, target);
            }
        } else if (unit.isShooting()) {

            tx = unit.aimX;
            ty = unit.aimY;
        }
        dir = Angles.angle(unit.x, unit.y, tx, ty);

        if (timer >= chargeTime) {
            distance = Math.min(Mathf.dst(unit.x, unit.y, tx, ty) + overshoot, maxDistance);
            traveled = 0f;
            hitTimer = hitInterval;
            ghostTimer = 0f;
            chargeSoundTimer = 0f;
            phase = DASHING;
            dashSound.at(unit.x, unit.y);
            Fx.shockwave.at(unit.x, unit.y);
        }
    }

    protected void updateDashing(Unit unit) {
        unit.apply(lockStatus, 3f);
        unit.isShooting = false;
        for (var mount : unit.mounts) mount.shoot = false;

        float turnSpeed = Math.max(unit.type.rotateSpeed, 6f) * 2f;
        unit.rotation = Angles.moveToward(unit.rotation, dir, turnSpeed * Time.delta);

        unit.vel.trns(dir, dashSpeed);
        traveled += dashSpeed * Time.delta;

        hitTimer += Time.delta;
        boolean hitNow = hitTimer >= hitInterval;
        if (hitNow) hitTimer = 0f;

        float r = unit.hitSize / 2f + hitPadding;
        float px = -Mathf.sinDeg(dir), py = Mathf.cosDeg(dir);
        float dx = Mathf.cosDeg(dir), dy = Mathf.sinDeg(dir);

        Units.nearby(null, unit.x - r, unit.y - r, r * 2f, r * 2f, other -> {
            if (other == unit || !other.within(unit, r + other.hitSize / 2f)) return;
            boolean enemy = other.team != unit.team;

            if (enemy && hitNow) {
                other.damage(dashDamage);
                hitSound.at(unit.x, unit.y);
            }

            if (enemy || pushAllies) {
                float cross = dx * (other.y - unit.y) - dy * (other.x - unit.x);
                float side = cross < 0f ? -1f : 1f;
                other.impulse(px * side * pushForce, py * side * pushForce);
            }
        });

        if (hitNow) {
            indexer.allBuildings(unit.x, unit.y, r, b -> {
                if (b.team != unit.team) {
                    b.damage(dashDamage * buildingDamageMultiplier);
                    hitSound.at(b.x, b.y);
                }
            });
        }

        ghostTimer += Time.delta;

        if (ghostInterval > 0f && ghostTimer >= ghostInterval) {
            ghostTimer %= ghostInterval;
            ghosts.add(new DashGhost(
                    unit.x,
                    unit.y,
                    unit.rotation,
                    ghostLifetime,
                    bodyScale(unit)
            ));
        }

        if (traveled >= distance) {
            phase = IDLE;
            cooldownTime = cooldown;
            unit.vel.scl(0.4f);
        }
    }

    public boolean charging() {
        return phase == CHARGING;
    }

    public float chargeProgress() {
        return Mathf.clamp(timer / chargeTime);
    }

    public Color blinkColor(Unit unit) {
        return color != null ? color : unit.team.color;
    }

    /** Сила мигания спрайта: частота растёт к концу зарядки. */
    public float blinkAlpha() {
        return Mathf.absin(Time.time * (1f + 3f * chargeProgress()), 3f, 1f) * 0.8f;
    }

    @Override
    public void draw(Unit unit) {

        drawGhosts(unit);

        if (phase != CHARGING) return;

        float p = chargeProgress();
        Color c = blinkColor(unit);
        float rad = indicatorRadius * (0.6f + 0.4f * p);
        float half = indicatorAngle / 2f;

        Draw.z(Layer.effect);

        Draw.color(c, 0.15f + 0.3f * p);
        Fill.arc(unit.x, unit.y, rad, indicatorAngle / 360f, dir - half);

        Draw.color(c, 0.9f);
        Lines.stroke(1.5f + 1.5f * p);
        Lines.arc(unit.x, unit.y, rad, indicatorAngle / 360f, dir - half);

        Draw.reset();
    }

    protected void drawGhosts(Unit unit) {
        if (ghostLifetime <= 0f || ghosts.isEmpty()) return;

        float oldZ = Draw.z();
        float oldXscl = Draw.xscl;
        float oldYscl = Draw.yscl;

        Draw.z(Layer.flyingUnit - 0.01f);

        try {
            for (DashGhost ghost : ghosts) {
                float alpha = ghostAlpha * Mathf.clamp(ghost.lifetime / ghostLifetime);
                Draw.color(Color.white, alpha);
                Draw.xscl = ghost.scaleX;
                Draw.yscl = 1f;
                Draw.rect(unit.type.region, ghost.x, ghost.y, ghost.rotation - 90f);
            }
        } finally {
            Draw.color();
            Draw.xscl = oldXscl;
            Draw.yscl = oldYscl;
            Draw.z(oldZ);
        }
    }
}