//package infernoadvanced.entities.bullet;
//
//import arc.graphics.*;
//import arc.graphics.g2d.*;
//import arc.math.*;
//import arc.util.*;
//import infernoadvanced.graphics.other.ShockwaveRenderer;
//import mindustry.entities.*;
//import mindustry.entities.bullet.*;
//import mindustry.gen.*;
//import mindustry.graphics.*;
//
//public class ShockwaveBulletType extends BombBulletType {
//    /** Радиус волны, в котором наносится урон (пиксели) */
//    public float waveRadius = 100f;
//    /** Базовый урон волны */
//    public float waveDamage = 150f;
//    /** Сила отбрасывания */
//    public float knockback = 10f;
//    /** Как долго волна будет «жить» (сек) */
//    public float waveDuration = 0.5f;
//    /** Сила искажения/свечения в шейдере */
//    public float strength = 1.5f;
//
//    /** -------------------------------------------------------------
//     *  Конструктор, позволяющий задать типичные параметры бомбы.
//     *  speed – скорость полёта (можно 0, если снаряд сразу «взрывается»)
//     *  damage – прямой урон при попадании
//     *  splash – радиус splash‑damage (не используется, но оставлен)
//     * ------------------------------------------------------------- */
//    public ShockwaveBulletType(float speed, float damage, float splash) {
//        super(speed, damage);
//        splashDamageRadius = splash;   // если нужен splash‑урон
//        // типичные настройки бомбы
//        lifetime = 1f;
//        collides = true;               // чтобы снаряд мог удариться о цель/землю
//        hittable = true;
//        absorbable = false;
//        keepVelocity = false;
//        // визуальная часть (можно задать sprite, если хотите)
//        // frontSprite = "bullet";
//    }
//
//    /** Пустой конструктор – нужен для сериализации/рефлексии */
//    public ShockwaveBulletType() {
//        this(0f, 0f, 0f);
//    }
//
//    /** -------------------------------------------------------------
//     *  Вызывается, когда снаряд попадает в цель/землю.
//     *  Здесь мы создаём шок‑вэйв, наносим урон и показываем
//     *  визуальные эффекты.
//     * ------------------------------------------------------------- */
//    @Override
//    public void hit(Bullet b, float x, float y) {
//        super.hit(b, x, y);               // стандартный взрыв/звук
//
//        // 1️⃣ Шок‑вэйв‑шейдер
//        ShockwaveRenderer.addWave(x, y, 0f, strength, waveDuration);
//
//        // 2️⃣ Урон и отбрасывание по юнитам
//        applyWaveDamage(b, x, y);
//
//        // 3️⃣ Визуальный «кольцевой» эффект (для тех, у кого шейдер отключён)
//        createRingEffect(x, y);
//    }
//
//    /** -------------------------------------------------------------
//     *  Вызывается, когда снаряд исчезает без удара (например,
//     *  таймаут). Мы всё равно хотим, чтобы волна отработала.
//     * ------------------------------------------------------------- */
//    @Override
//    public void despawned(Bullet b) {
//        super.despawned(b);
//        // Если снаряд исчез без удара, всё равно создаём волну.
//        ShockwaveRenderer.addWave(b.x, b.y, 0f, strength, waveDuration);
//        applyWaveDamage(b, b.x, b.y);
//        createRingEffect(b.x, b.y);
//    }
//
//    /** -------------------------------------------------------------
//     *  Наносит урон и отбрасывает юнитов в радиусе `waveRadius`.
//     * ------------------------------------------------------------- */
//    private void applyWaveDamage(Bullet b, float cx, float cy) {
//        float radius = waveRadius;
//
//        // Урон по юнитам
//        Units.nearbyEnemies(b.team, cx, cy, radius, unit -> {
//            if (!unit.within(cx, cy, radius)) return;
//
//            float dist = unit.dst(cx, cy);
//            float dmgMul = 1f - (dist / radius);          // линейное затухание
//            float dmg = waveDamage * dmgMul;
//
//            unit.damage(dmg);
//
//            // Отбрасывание
//            float angle = unit.angleTo(cx, cy);
//            float force = knockback * dmgMul;
//            unit.impulse(Tmp.v1.trns(angle, force));
//        });
//
//        // Урон по зданиям (используем готовый метод Damage)
//        Damage.damage(b.team, cx, cy, radius, waveDamage, true);
//
//        // Тряска экрана и звук
//        Effect.shake(4f, 4f, b);
//        Sounds.explosion.at(cx, cy, 1f, 0.8f);
//    }
//
//    /** -------------------------------------------------------------
//     *  Кольцевой визуальный эффект, который будет виден даже без
//     *  шейдера (просто рисует несколько кругов, постепенно исчезающих).
//     * ------------------------------------------------------------- */
//    private void createRingEffect(float x, float y) {
//        // Три круга, каждый появляется с небольшим смещением во времени
//        for (int i = 0; i < 3; i++) {
//            float delay = i * 2f;
//            Time.run(delay, () -> {
//                float radius = waveRadius;
//                // Плавно уменьшаем альфа за 0.4 сек
//                Effect effect = new Effect(0.4f, e -> {
//                    float prog = e.fout();                     // 1 → 0
//                    Draw.color(Color.valueOf("#ff8844"), prog);
//                    Lines.stroke(4f * prog);
//                    Lines.circle(x, y, radius * prog);
//                });
//                effect.at(x, y);
//            });
//        }
//    }
//}
