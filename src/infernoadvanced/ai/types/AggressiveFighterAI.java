package infernoadvanced.ai.types;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.FlyingAI;
import mindustry.entities.Units;
import mindustry.gen.Rotc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.Velc;

/**
 * ИИ истребителя.
 * - Охотится в первую очередь на воздушные цели.
 * - Подвижную воздушную цель ловит на хвост: держится позади неё на
 * tailDistance, подгоняет скорость под цель, а когда встаёт сзади,
 * целится носом прямо в неё (для неподвижных носовых орудий).
 * - Наземные цели и здания атакует заходами: пролетел, ушёл прямо, развернулся.
 * - Не врезается в других юнитов: отталкивание от всех летающих юнитов любой
 * команды (цель при этом подпускает почти вплотную).
 * - Манёвренность: тормозит перед крутым поворотом (радиус разворота
 * ~ скорость / угловая скорость, медленнее значит круче), плюс добавочная
 * скорость поворота extraTurnSpeed поверх rotateSpeed юнита.
 * <p>
 * Подключение: aiController = AggressiveFighterAI::new;
 * С параметрами:  aiController = () -> new AggressiveFighterAI() {{ tailDistance = 60f; }};
 */
public class AggressiveFighterAI extends FlyingAI {
    /**
     * Воздушные цели ищутся в радиусе range * huntRangeRatio.
     */
    public float huntRangeRatio = 3f;
    /**
     * Таймер ретаргетинга: чем меньше, тем агрессивнее.
     */
    public float retargetTime = 25f;

    /**
     * Расстояние позади цели (не больше 70% дальности оружия).
     */
    public float tailDistance = 70f;
    /**
     * Максимум тиков упреждения при расчёте точки за целью.
     */
    public float maxLeadTime = 45f;
    /**
     * Насколько скорость растёт с расстоянием до точки за целью.
     */
    public float speedGain = 0.06f;
    /**
     * Минимальная доля скорости (самолёт не может висеть на месте).
     */
    public float minSpeedRatio = 0.55f;
    /**
     * Угол до цели, в пределах которого целимся прямо в неё.
     */
    public float aimCone = 40f;

    /**
     * Насколько тормозим при угле поворота 120 и больше (0..1).
     */
    public float turnBrake = 0.6f;
    /**
     * Добавочная скорость поворота, градусов за тик, поверх rotateSpeed юнита.
     */
    public float extraTurnSpeed = 2.5f;

    /**
     * Когда считаем, что пролетели цель: расстояние как доля дальности.
     */
    public float passDistanceRatio = 0.35f;
    /**
     * Улетаем прямо, пока не отдалимся на такую долю дальности.
     */
    public float extendRatio = 0.9f;
    /**
     * Максимум тиков прямого ухода после пролёта.
     */
    public float extendMaxTime = 120f;

    /**
     * Дальность влияния отталкивания, сверх размеров юнитов.
     */
    public float avoidanceRadius = 45f;
    /**
     * Минимальный зазор до других юнитов.
     */
    public float avoidanceGap = 10f;
    /**
     * Сила отталкивания (в долях скорости).
     */
    public float avoidanceStrength = 1.2f;
    /**
     * Учитывать наземных юнитов (летающие с ними не сталкиваются).
     */
    public boolean avoidGround = false;

    protected final Vec2 avoid = new Vec2();
    protected float tvx, tvy, tspeed, theading;
    protected boolean extending;
    protected float extendTimer;

    @Override
    public void updateMovement() {
        unloadPayloads();

        if (target == null || !unit.hasWeapons()) {
            extending = false;
            super.updateMovement();
            return;
        }

        float speed = unit.speed();
        float range = Math.max(unit.type.range, 40f);
        float tx = target.getX(), ty = target.getY();
        float dst = unit.dst(tx, ty);
        float err = Angles.angleDist(unit.rotation, unit.angleTo(tx, ty));

        readTargetMotion();

        boolean airTarget = target instanceof Unit u && u.isFlying() && tspeed > 0.3f;

        if (airTarget) {
            extending = false;
            chaseTail(speed, range, dst, err);
        } else {
            attackRun(speed, range, dst, err);
        }

        float turnErr = Angles.angleDist(unit.rotation, vec.angle());
        float brake = Mathf.lerp(1f, 1f - turnBrake, Mathf.clamp(turnErr / 120f));
        vec.setLength(Math.max(vec.len() * brake, speed * 0.35f));

        computeAvoidance();
        vec.add(avoid.x * speed * avoidanceStrength, avoid.y * speed * avoidanceStrength);
        vec.limit(speed);

        unit.movePref(vec);

        if (!unit.type.omniMovement && extraTurnSpeed > 0f && !vec.isZero()) {
            unit.rotation = Angles.moveToward(unit.rotation, vec.angle(), extraTurnSpeed * Time.delta);
        }
    }

    /**
     * Погоня за подвижной воздушной целью: сесть на хвост.
     */
    protected void chaseTail(float speed, float range, float dst, float err) {
        float tail = Math.min(tailDistance, range * 0.7f);

        float lead = Math.min(dst / Math.max(speed, 0.1f), maxLeadTime);
        float px = target.getX() + tvx * lead;
        float py = target.getY() + tvy * lead;
        Tmp.v1.trns(theading, tail);
        float tailX = px - Tmp.v1.x, tailY = py - Tmp.v1.y;
        float dTail = unit.dst(tailX, tailY);

        boolean lined = dst < range * 0.9f && err < aimCone && dTail < tail * 1.5f;
        float aimX = lined ? px : tailX;
        float aimY = lined ? py : tailY;

        float desired = Mathf.clamp(tspeed + dTail * speedGain, speed * minSpeedRatio, speed);

        if (err > 100f && dst < tail * 2.5f) {
            aimX = target.getX();
            aimY = target.getY();
            desired = speed * minSpeedRatio;
        }

        vec.set(aimX - unit.x, aimY - unit.y).setLength(desired);
    }

    /**
     * Заход на наземную цель или здание: подлёт, пролёт, прямой уход, разворот.
     */
    protected void attackRun(float speed, float range, float dst, float err) {
        if (extending) {
            extendTimer += Time.delta;
            vec.trns(unit.rotation, speed);
            if (dst > range * extendRatio || extendTimer > extendMaxTime) extending = false;
            return;
        }

        vec.set(target.getX() - unit.x, target.getY() - unit.y).setLength(speed);

        if (err > 90f && dst < range * passDistanceRatio) {
            extending = true;
            extendTimer = 0f;
        }
    }

    protected void readTargetMotion() {
        tvx = tvy = 0f;
        if (target instanceof Velc v) {
            tvx = v.vel().x;
            tvy = v.vel().y;
        }
        tspeed = Mathf.len(tvx, tvy);

        if (tspeed > 0.2f) {
            theading = Tmp.v1.set(tvx, tvy).angle();
        } else if (target instanceof Rotc r) {
            theading = r.rotation();
        } else {
            theading = unit.angleTo(target);
        }
    }

    /**
     * Отталкивание от близких юнитов: чем ближе, тем сильнее.
     */
    protected void computeAvoidance() {
        avoid.setZero();

        float x = unit.x, y = unit.y;
        float r = avoidanceRadius + unit.hitSize + 40f;

        Units.nearby(null, x - r, y - r, r * 2f, r * 2f, other -> {
            if (other == unit || !other.isValid()) return;
            if (!avoidGround && !other.isFlying()) return;

            float d = unit.dst(other);
            if (d < 0.001f) return;

            boolean isTarget = other == target;
            float minDst = (unit.hitSize + other.hitSize) / 2f + (isTarget ? 2f : avoidanceGap);
            // цель подпускаем почти вплотную, остальных обходим заранее
            float zone = isTarget ? 12f : avoidanceRadius;

            float k = 1f - Mathf.clamp((d - minDst) / zone);
            if (k <= 0f) return;
            k *= k;

            avoid.add((x - other.x) / d * k, (y - other.y) / d * k);
        });

        avoid.limit(1.5f);
    }

    @Override
    public boolean retarget() {
        return timer.get(timerTarget, target == null ? retargetTime : retargetTime * 2f);
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        if (air) {
            Unit u = Units.closestEnemy(unit.team, x, y, range * huntRangeRatio,
                    e -> e.isFlying() && e.checkTarget(true, false));
            if (u != null) return u;
        }
        return super.findMainTarget(x, y, range, air, ground);
    }
}