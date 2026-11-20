package infernoadvanced.content.planets.events;

import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec3;

/**
 * Расписание выбросов звезды. Выбросов много (WAVES), каждый живёт PERIOD секунд,
 * начала разнесены по золотому сечению, поэтому в любой момент в разных стадиях
 * находится сразу много выбросов и звезда покрыта ими целиком.
 */

public class SolarActivity {
    /**
     * Сколько выбросов идёт одновременно (в шейдере столько же ячеек массива).
     */
    public static final int WAVES = 28;
    /**
     * Длительность одного выброса, секунды.
     */
    public static final float PERIOD = 14f;
    /**
     * Максимальная высота выброса от центра звезды.
     */
    public static final float REACH = 4.6f;

    /**
     * Насколько выброс смещается от своей точки сетки при каждом новом извержении.
     */
    private static final float JITTER = 0.45f;
    /**
     * Диапазон резкости конуса: больше значение, уже и острее выброс.
     */
    private static final float SHARP_MIN = 14f, SHARP_MAX = 70f;
    /**
     * Диапазон высоты выброса в долях REACH.
     */
    private static final float HEIGHT_MIN = 0.35f, HEIGHT_MAX = 1f;

    /**
     * Интервал между геймплейными штормами (секунды вселенной).
     */
    private static final float STORM_INTERVAL_MIN = 300f, STORM_INTERVAL_MAX = 1800f;
    /**
     * Длительность геймплейного шторма.
     */
    private static final float STORM_DURATION_MIN = 240f, STORM_DURATION_MAX = 2100f;

    private static final long SEED = 6661301L;
    private static final float GOLDEN = 0.6180339887f;
    private static final float GOLDEN_ANGLE = 2.3999632f;

    private static final Rand rand = new Rand();
    private static final Vec3 dir = new Vec3(), jitter = new Vec3();

    /**
     * Заполняет данные для шейдера на момент seconds.
     * waves:  WAVES * 4, на каждый выброс xyz = направление, w = фаза 0..1
     * shapes: WAVES * 4, на каждый выброс x = резкость конуса, y = высота в долях REACH
     */

    public static void fill(float seconds, float[] waves, float[] shapes) {
        for (int i = 0; i < WAVES; i++) {
            float t = seconds / PERIOD + i * GOLDEN;
            int eruption = (int) Math.floor(t);
            float phase = t - eruption;

            rand.setSeed(SEED + eruption * 31L + i * 7L);

            float y = 1f - (i + 0.5f) * 2f / WAVES;
            float r = (float) Math.sqrt(Math.max(0f, 1f - y * y));
            float a = i * GOLDEN_ANGLE;
            dir.set(Mathf.cos(a) * r, y, Mathf.sin(a) * r);

            jitter.setToRandomDirection(rand).scl(JITTER);
            dir.add(jitter).nor();

            int w = i * 4;
            waves[w] = dir.x;
            waves[w + 1] = dir.y;
            waves[w + 2] = dir.z;
            waves[w + 3] = phase;

            shapes[w] = rand.random(SHARP_MIN, SHARP_MAX);
            shapes[w + 1] = rand.random(HEIGHT_MIN, HEIGHT_MAX);
            shapes[w + 2] = 0f;
            shapes[w + 3] = 0f;
        }
    }

    /**
     * Геймплейная интенсивность шторма 0..1 (для SolarStorm).
     * Детерминированная от сида, зависит только от universeSeconds.
     */
    public static float getStormIntensity(float universeSeconds) {
        float cycleTime = 0f;
        int cycleIndex = 0;

        while (cycleTime <= universeSeconds) {
            rand.setSeed(SEED + cycleIndex * 17L);
            float interval = rand.random(STORM_INTERVAL_MIN, STORM_INTERVAL_MAX);
            float duration = rand.random(STORM_DURATION_MIN, STORM_DURATION_MAX);

            float stormStart = cycleTime + interval;
            float stormEnd = stormStart + duration;

            if (universeSeconds >= stormStart && universeSeconds < stormEnd) {
                float progress = (universeSeconds - stormStart) / duration;

                if (progress < 0.15f) return progress / 0.15f;
                if (progress > 0.85f) return (1f - progress) / 0.15f;
                return 1f;
            }

            cycleTime = stormEnd;
            cycleIndex++;
        }

        return 0f;
    }
}