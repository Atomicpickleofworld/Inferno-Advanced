package infernoadvanced.entities.status;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

/**
 * Статус-эффект с уровнями (слабый, средний, сильный): у юнита из одного семейства
 * одновременно может быть только один уровень, и это сильнейший.
 * <p>
 * Правила, когда на юнита с уровнем A накладывается уровень B того же семейства:
 * - B сильнее A: A заменяется на B (время берётся большее из двух);
 * - B слабее A: B игнорируется (или слегка продлевает A, см. refreshWeaker);
 * - при stackUp = true разные уровни складываются: результат на ступень выше
 * сильнейшего из двух (слабый + средний даёт сильный, как было у радиации).
 * Повторное наложение того же эффекта, как обычно, продлевает его.
 * <p>
 * Номер уровня (tier) задаётся явно: 1 слабый, 2 средний, 3 сильный. Порядок создания не важен.
 */
public class TieredStatusEffect extends StatusEffect {
    private static final ObjectMap<String, Seq<TieredStatusEffect>> families = new ObjectMap<>();

    public final String family;
    public final int tier;
    /**
     * Складывать разные уровни: результат на ступень выше сильнейшего.
     */
    public boolean stackUp = false;
    /**
     * Если слабый эффект наложен на сильный: доля его времени, которой продлевается сильный. 0 = игнорировать.
     */
    public float refreshWeaker = 0f;

    public TieredStatusEffect(String name, String family, int tier) {
        super(name);
        this.family = family;
        this.tier = tier;
        families.get(family, Seq::new).add(this);
    }

    private TieredStatusEffect byTier(int wanted) {
        Seq<TieredStatusEffect> levels = families.get(family);
        return levels == null ? null : levels.find(e -> e.tier == wanted);
    }

    private int maxTier() {
        int max = tier;
        Seq<TieredStatusEffect> levels = families.get(family);
        if (levels != null) {
            for (TieredStatusEffect e : levels) max = Math.max(max, e.tier);
        }
        return max;
    }
    /**
     * Вызывается, когда на юнита с этим эффектом накладывается другой (to).
     */
    @Override
    public boolean applyTransition(Unit unit, StatusEffect to, StatusEntry entry, float time) {
        if (to instanceof TieredStatusEffect other && other != this && other.family.equals(family)) {
            TieredStatusEffect result = other.tier > tier ? other : this;

            if (stackUp) {
                TieredStatusEffect up = byTier(Math.min(maxTier(), Math.max(tier, other.tier) + 1));
                if (up != null) result = up;
            }

            if (result == this) {
                if (refreshWeaker > 0f) entry.time = Math.max(entry.time, time * refreshWeaker);
            } else {
                entry.set(result, Math.max(entry.time, time));
                result.applied(unit, time, false);
            }
            return true;
        }

        return super.applyTransition(unit, to, entry, time);
    }
}