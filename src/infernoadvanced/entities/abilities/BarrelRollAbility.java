package infernoadvanced.entities.abilities;

import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import infernoadvanced.loaders.IASounds;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

/**
 * Переворот в воздухе («бочка»): во время переворота вражеские снаряды в
 * радиусе парирования отражаются (или поглощаются, если их нельзя отразить).
 * <p>
 * Запускается, когда юнит стреляет. С requireThreat = true переворот
 * включается только если рядом реально летит вражеский снаряд, чтобы не
 * тратить откат вхолостую.
 * <p>
 * Для визуального переворота спрайта используй RollUnitType.
 */
public class BarrelRollAbility extends Ability {
    /**
     * Откат после переворота, тики.
     */
    public float cooldown = 60f * 5f;
    /**
     * Длительность переворота, тики.
     */
    public float duration = 36f;
    /**
     * Радиус парирования от центра юнита.
     */
    public float parryRadius = 30f;
    /**
     * true: отражать снаряды обратно. false: только поглощать.
     */
    public boolean reflect = true;
    /**
     * Множитель урона отражённого снаряда.
     */
    public float reflectDamageMultiplier = 1f;
    /**
     * Запускать только при стрельбе.
     */
    public boolean whenShooting = true;
    /**
     * Запускать только если рядом есть вражеский снаряд.
     */
    public boolean requireThreat = true;
    /**
     * На каком расстоянии снаряд считается угрозой.
     */
    public float threatRadius = 90f;
    /**
     * Пассивный переворот: по whenShooting / requireThreat, как раньше.
     * Можно отключить, если нужен переворот только на выстрел.
     */
    public boolean passiveRoll = true;
    /**
     * Переворот на выстрел: как только любое управляемое оружие юнита делает
     * выстрел (или залп), стартует переворот. По умолчанию выключено.
     * Автоматические турели (controllable = false) не считаются.
     */
    public boolean rollOnShot = false;
    /**
     * Есть ли откат у переворота на выстрел.
     * true:  выстрельный переворот ждёт общий откат и запускает его после себя
     * (как пассивный, откат у них общий).
     * false: выстрельный переворот игнорирует откат и сам его не запускает,
     * так что он не мешает пассивным переворотам.
     * Пока переворот идёт, новый не стартует в любом случае.
     */
    public boolean shotRollHasCooldown = true;
    /**
     * Звук начала переворота.
     */
    public Sound rollSound = IASounds.windyMovement;
    /**
     * Звук парирования снаряда.
     */
    public Sound parrySound = IASounds.parry;

    protected float rollTime = 0f;
    protected float cooldownTime = 0f;
    /**
     * Текущий переворот запущен выстрелом (нужно для решения про откат).
     */
    protected boolean rollFromShot = false;
    /**
     * reload каждого оружия на прошлом тике, чтобы заметить выстрел по скачку вверх.
     */
    protected float[] lastReload;

    private static Unit paramUnit;
    private static BarrelRollAbility paramAbility;
    private static boolean threatFound;

    private static final Cons<Bullet> threatConsumer = b -> {
        if (b.team != paramUnit.team && b.damage > 0f
                && (b.type.reflectable || b.type.absorbable)
                && b.within(paramUnit, paramAbility.threatRadius)) {
            threatFound = true;
        }
    };

    private static final Cons<Bullet> parryConsumer = b -> {
        if (b.team == paramUnit.team) return;
        if (!b.within(paramUnit, paramAbility.parryRadius + b.hitSize / 2f)) return;

        if (paramAbility.reflect && b.type.reflectable) {
            b.vel.scl(-1f);
            b.team = paramUnit.team;
            b.owner = paramUnit;
            b.damage *= paramAbility.reflectDamageMultiplier;
            Fx.absorb.at(b.x, b.y);
            paramAbility.parrySound.at(b.x, b.y);
        } else if (b.type.absorbable) {
            b.absorb();
            Fx.absorb.at(b.x, b.y);
            paramAbility.parrySound.at(b.x, b.y);
        }
    };
    /**
     * Был ли выстрел: после выстрела reload оружия возвращается к максимуму.
     */
    protected boolean detectShot(Unit unit) {
        var mounts = unit.mounts;
        if (lastReload == null || lastReload.length != mounts.length) {
            lastReload = new float[mounts.length];
        }

        boolean shot = false;
        for (int i = 0; i < mounts.length; i++) {
            var mount = mounts[i];
            if (mount.weapon.controllable && !mount.weapon.noAttack
                    && mount.reload > lastReload[i] + 0.0001f) {
                shot = true;
            }
            lastReload[i] = mount.reload;
        }
        return shot;
    }

    @Override
    public void update(Unit unit) {
        boolean shot = rollOnShot && detectShot(unit);

        if (cooldownTime > 0f) cooldownTime -= Time.delta;

        if (rollTime > 0f) {
            rollTime -= Time.delta;

            paramUnit = unit;
            paramAbility = this;
            float r = parryRadius + 8f;
            Groups.bullet.intersect(unit.x - r, unit.y - r, r * 2f, r * 2f, parryConsumer);

            if (rollTime <= 0f) {
                rollTime = 0f;
                if (!rollFromShot || shotRollHasCooldown) cooldownTime = cooldown;
            }
            return;
        }

        if (shot && (!shotRollHasCooldown || cooldownTime <= 0f)) {
            startRoll(unit, true);
            return;
        }

        if (!passiveRoll || cooldownTime > 0f) return;
        if (whenShooting && !unit.isShooting()) return;

        if (requireThreat) {
            paramUnit = unit;
            paramAbility = this;
            threatFound = false;
            float r = threatRadius;
            Groups.bullet.intersect(unit.x - r, unit.y - r, r * 2f, r * 2f, threatConsumer);
            if (!threatFound) return;
        }

        startRoll(unit, false);
    }

    protected void startRoll(Unit unit, boolean fromShot) {
        rollTime = duration;
        rollFromShot = fromShot;
        rollSound.at(unit.x, unit.y);
    }
    /**
     * Масштаб спрайта по ширине: имитация вращения вокруг продольной оси.
     */
    public float scaleX() {
        if (rollTime <= 0f) return 1f;
        float p = 1f - rollTime / duration;
        float c = Mathf.cos(Interp.smooth.apply(p) * Mathf.PI2);
        // не даём спрайту схлопнуться в нулевую ширину
        return Math.abs(c) < 0.12f ? (c < 0f ? -0.12f : 0.12f) : c;
    }

    public boolean rolling() {
        return rollTime > 0f;
    }
    /**
     * Масштаб переворота для юнита: 1, если способности нет или переворот не идёт.
     */
    public static float scaleOf(Unit unit) {
        for (Ability a : unit.abilities) {
            if (a instanceof BarrelRollAbility roll) return roll.scaleX();
        }
        return 1f;
    }

    @Override
    public void draw(Unit unit) {
        if (rollTime <= 0f) return;

        float p = 1f - rollTime / duration;
        Draw.z(Layer.effect);
        Draw.color(unit.team.color, 0.35f * Mathf.slope(p));
        Lines.stroke(1.2f);
        Lines.circle(unit.x, unit.y, parryRadius);
        Draw.reset();
    }
}