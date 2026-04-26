package infernoadvanced.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;

import static mindustry.Vars.*;

public class PushBulletType extends BulletType {

    // ━━━ НАСТРОЙКИ ━━━
    public float knockbackStrength = 1.2f;
    public float sizeGrowth = 0.5f;
    public float maxSize = 40f;
    public float minSize = 8f;

    // ━━━ ЦВЕТА ━━━
    public Color pushBackColor;
    public Color pushFrontColor;
    public Color pushHitColor;
    public Color pushTrailColor;

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

        // ✅ ИНИЦИАЛИЗАЦИЯ ЦВЕТОВ
        this.pushBackColor = Color.valueOf("6ec9ff");
        this.pushFrontColor = Color.white;
        this.pushHitColor = Color.valueOf("6ec9ff");
        this.pushTrailColor = Color.valueOf("6ec9ff");

        // ✅ УБИРАЕМ ТРЕЙЛ
        this.trailWidth = 0;
        this.trailLength = 0;

        this.shootEffect = Fx.shootSmall;
        this.hitEffect = Fx.none;
        this.despawnEffect = Fx.none;

        this.despawnEffect = new Effect(20f, e -> {
            Draw.color(pushBackColor, e.fout());
            Lines.stroke(2f * e.fout());
            Lines.circle(e.x, e.y, e.fin() * 40f);

            // Искры при исчезновении
            Draw.color(pushFrontColor, e.fout());
            for(int i = 0; i < 4; i++){
                float angle = i * 90f + e.rotation;
                Drawf.tri(e.x, e.y, 1f, 15f * e.fout(), angle);
            }
        });
        // ✅ НЕЛЬЗЯ СБИТЬ/ПОГЛОТИТЬ
        this.hittable = false;
        this.absorbable = false;
        this.reflectable = false;

        this.keepVelocity = true;
        this.drag = 0f;

    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        // ✅ РОСТ РАЗМЕРА
        if (b.data == null) {
            b.data = minSize;
        }
        float currentSize = (Float)b.data;
        currentSize = Math.min(currentSize + sizeGrowth, maxSize);
        b.data = currentSize;

        // ✅ ОБНОВЛЯЕМ ХИТБОКС
        b.hitSize = currentSize;

        // ✅ ОТТАЛКИВАНИЕ ЮНИТОВ
        float pushRadius = currentSize * 1.5f;
        final float finalCurrentSize = currentSize;

        Units.nearbyEnemies(b.team, b.x, b.y, pushRadius, unit -> {
            float angle = Angles.angle(b.x, b.y, unit.x, unit.y);

            // Прогресс полёта: 0 = старт, 1 = конец
            float progress = b.time / b.lifetime;

            // Сила уменьшается с расстоянием
            float distanceFactor = 1f - (progress * 0.7f);

            // ✅ ИСПРАВЛЕНО: используем knockbackStrength вместо жёсткого значения!
            float baseForce = knockbackStrength;  // <-- Было: 2.5f (жёстко)

            // Учёт брони
            float armorFactor = 1f / (1f + unit.armor() * 0.2f);  // <-- Увеличил коэффициент для Т5

            // Итоговая сила
            float force = baseForce * distanceFactor * armorFactor;

            float vx = Angles.trnsx(angle, force);
            float vy = Angles.trnsy(angle, force);

            unit.vel.add(vx, vy);
        });
    }

    @Override
    public void draw(Bullet b) {
        float currentSize = b.data instanceof Float ? (Float)b.data : minSize;

        // Внешнее кольцо
        Draw.color(pushBackColor, 0.6f);
        Fill.circle(b.x, b.y, currentSize);

        // Внутреннее ядро
        Draw.color(pushFrontColor, 0.9f);
        Fill.circle(b.x, b.y, currentSize * 0.5f);

        // Пульсирующая аура
        Draw.color(pushBackColor, 0.3f + Mathf.absin(b.time, 8f, 0.2f));
        Lines.stroke(2f);
        Lines.circle(b.x, b.y, currentSize * 1.2f);

        Draw.reset();
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        Fx.hitLancer.at(x, y, pushHitColor);
    }
}