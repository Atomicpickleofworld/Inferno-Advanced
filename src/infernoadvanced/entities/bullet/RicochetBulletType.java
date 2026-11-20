package infernoadvanced.entities.bullet;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;
import mindustry.graphics.Pal;

import static mindustry.Vars.indexer;

public class RicochetBulletType extends BasicBulletType {

    public int maxBounces = 3;
    public float bounceRange = 150f;
    public float damageMultiplier = 0.8f;  // ← ИЗМЕНЕНО: урон падает, а не растёт
    public Effect bounceEffect = Fx.hitBulletBig;

    public boolean bounceBuildings = true;
    public boolean bounceUnits = true;

    public RicochetBulletType() {
        super();

        damage = 20f;
        speed = 5f;
        lifetime = 80f;
        hitSize = 6f;
        width = 6f;
        height = 8f;

        hitEffect = Fx.hitBulletSmall;
        shootEffect = Fx.shootSmall;

        collides = true;
        collidesTiles = true;
        collidesAir = true;
        collidesGround = true;
        pierce = false;  // ← ИСПРАВЛЕНО: отключаем pierce чтобы избежать бесконечного цикла
        pierceCap = 1;   // ← один проход максимум
        keepVelocity = false;
        reflectable = false;

        trailColor = Pal.missileYellowBack;
        trailChance = 0.5f;
        trailParam = 4f;

        buildingDamageMultiplier = 1f;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        b.data = new int[]{maxBounces, (int) damage};
        b.damage = damage;
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);

        if (entity instanceof Unit && b.data instanceof int[] data && data[0] > 0) {
            data[0]--;

            Object target = findNextTarget(b, entity);
            if (target instanceof Unit unit) {
                doBounceToUnit(b, entity, unit, data);
            } else if (target instanceof Building building) {
                doBounceToBuilding(b, entity, building, data);
            }
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        super.hitTile(b, build, x, y, initialHealth, direct);

        if (b.data instanceof int[] data && data[0] > 0 && bounceBuildings) {
            data[0]--;

            Object target = findNextTarget(b, build);

            if (target != null) {
                if (target instanceof Unit unit) {
                    doBounceToUnit(b, build, unit, data);
                } else if (target instanceof Building building) {
                    doBounceToBuilding(b, build, building, data);
                }
            }
        }
    }

    protected Object findNextTarget(Bullet b, Object currentTarget) {
        float range = bounceRange;
        float rangeSq = range * range;

        Unit nearestUnit = null;
        float nearestUnitDist = Float.MAX_VALUE;
        Building nearestBuilding = null;
        float nearestBuildingDist = Float.MAX_VALUE;

        if (bounceUnits) {
            nearestUnit = Units.closestEnemy(b.team, b.x, b.y, range,
                    u -> u != null
                            && u != currentTarget
                            && !u.dead()
                            && u.team != b.team
                            && u.checkTarget(collidesAir, collidesGround)
            );
            if (nearestUnit != null) {
                nearestUnitDist = b.dst2(nearestUnit);
            }
        }

        if (bounceBuildings && collidesGround) {
            final float[] bestDist = {Float.MAX_VALUE};
            final Building[] bestBuild = {null};

            indexer.eachBlock(b.team, b.x, b.y, range,
                    building -> building.team != b.team && !building.dead(),
                    building -> {
                        float dist = b.dst2(building);
                        if (dist < rangeSq && dist < bestDist[0]) {
                            bestDist[0] = dist;
                            bestBuild[0] = building;
                        }
                    }
            );

            nearestBuilding = bestBuild[0];
            nearestBuildingDist = bestDist[0];
        }

        if (nearestUnit != null && nearestBuilding != null) {
            return nearestUnitDist < nearestBuildingDist ? nearestUnit : nearestBuilding;
        }
        if (nearestUnit != null) return nearestUnit;
        if (nearestBuilding != null) return nearestBuilding;

        return null;
    }

    protected void doBounceToUnit(Bullet b, Object from, Unit to, int[] data) {
        float fx = from instanceof Hitboxc h ? h.x() : b.x;
        float fy = from instanceof Hitboxc h ? h.y() : b.y;

        bounceEffect.at(fx, fy, b.rotation(), hitColor);
        Fx.hitLaser.at(to.x, to.y, 0f, Pal.surge);
        Fx.lightning.at(fx, fy, 0f, Pal.surge);

        int bouncesLeft = data[0];
        int baseDmg = data[1];
        b.damage = baseDmg * Mathf.pow(damageMultiplier, maxBounces - bouncesLeft);

        float angle = b.angleTo(to);
        b.rotation(angle);
        b.vel.trns(angle, speed * 1.1f);

        b.set(to.x, to.y);
        b.time = 0f;
        b.lifetime = lifetime;

        to.apply(StatusEffects.shocked, 30f);
        Sounds.shoot.at(to.x, to.y, 1f + Mathf.range(0.2f));
        hitEffect.at(to.x, to.y, angle, hitColor);
    }

    protected void doBounceToBuilding(Bullet b, Object from, Building to, int[] data) {
        float fx = from instanceof Hitboxc h ? h.x() : b.x;
        float fy = from instanceof Hitboxc h ? h.y() : b.y;

        bounceEffect.at(fx, fy, b.rotation(), hitColor);
        Fx.hitLaser.at(to.x, to.y, 0f, Pal.surge);
        Fx.breakProp.at(to.x, to.y, 0f, Pal.surge);
        Fx.lightning.at(fx, fy, 0f, Pal.surge);

        int bouncesLeft = data[0];
        int baseDmg = data[1];
        b.damage = baseDmg * Mathf.pow(damageMultiplier, maxBounces - bouncesLeft);

        float angle = b.angleTo(to);
        b.rotation(angle);
        b.vel.trns(angle, speed * 1.1f);

        b.set(to.x, to.y);
        b.time = 0f;
        b.lifetime = lifetime;

        Sounds.shoot.at(to.x, to.y, 0.8f + Mathf.range(0.2f));
        hitEffect.at(to.x, to.y, angle, hitColor);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        // Используем Mathf.equal для надёжной проверки float
        if (Mathf.equal(b.time % 5f, 0f, 0.1f) && b.data instanceof int[] data && data[0] > 0) {
            if (data[0] > 1) {
                trailEffect.at(b.x + Mathf.range(3f), b.y + Mathf.range(3f), 0f, Pal.surge);
            }
        }
    }
}