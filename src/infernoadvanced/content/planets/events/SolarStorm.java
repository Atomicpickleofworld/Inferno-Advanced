package infernoadvanced.content.planets.events;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.struct.Seq;
import arc.util.Time;
import infernoadvanced.content.InfernoPlanets;
import infernoadvanced.content.InfernoStatusEffects;
import mindustry.content.Fx;
import mindustry.game.EventType.Trigger;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.logic.LAccess;
import mindustry.type.Sector;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.BaseShield;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;

/**
 * Влияние солнечных выбросов на кампанию на Infernos.
 * <p>
 * Время: universe.secondsf() (время кампании, сохраняется вместе с сохранением),
 * а не Time.globalTime. Расписание штормов берётся из SolarActivity.getStormIntensity.
 * <p>
 * Сила шторма в секторе = интенсивность шторма * освещённость сектора.
 * Освещённость даёт Sector.getLight(): она считается из sector.tile.v и позиции звезды и
 * меняется вместе с вращением планеты (день/ночь). Ночная сторона защищена.
 */
public class SolarStorm {
    // --- интерфейс ---
    public static final String NOTIFICATIONS_SETTING = "inferno-solar-notifications";
    // --- ориентация сектора ---
    /**
     * getLight() ниже этого значения: сектор в тени, шторма нет.
     */
    public static final float SHADOW_LIGHT = 0.5f;
    /**
     * getLight() выше этого значения: полный эффект.
     */
    public static final float FULL_LIGHT = 0.85f;

    // --- шторм ---
    /**
     * Базовый нагрев: постоянный фон на дневной стороне, 0..1 от полного эффекта.
     */
    public static final float PASSIVE_HEAT = 0.12f;
    private static float lastIntensityLevel = 0f;

    // --- радиация ---
    public static final float DAMAGE_INTERVAL = 30f; // тиков между ударами по целям
    public static final float UNIT_DPS_MIN = 0.5f, UNIT_DPS_MAX = 2f;
    /**
     * Доля максимального здоровья панели в секунду при полной силе шторма.
     */
    public static final float PANEL_DAMAGE_PER_SEC = 0.022f;
    /**
     * Падение выработки солнечных панелей.
     */
    public static final float SOLAR_DROP_MIN = 0.4f, SOLAR_DROP_MAX = 0.6f;
    /**
     * Утечка заряда батарей в секунду, доля ёмкости при полной силе шторма.
     */
    public static final float BATTERY_LEAK_PER_SEC = 0.002f;
    /**
     * Множитель солнечных панелей без шторма.
     */
    public static final float BASE_SOLAR = 1f;

    // --- ЭМИ ---
    public static final float EMP_THRESHOLD = 0.7f;
    public static final float EMP_COOLDOWN = 60f * 20f;
    public static final float EMP_UNIT_TIME = 60f * 5f;
    /**
     * Длительность эффектов radiationWeak/empLow, должна быть > DAMAGE_INTERVAL.
     */
    public static final float EFFECT_DURATION = 60f;

    /**
     * Текущие значения, можно читать из UI.
     */
    public static float exposure, intensity, passiveHeat;

    public static StatusEffect empStatus;

    private static final Seq<Building> tmp = new Seq<>();

    private static float damageTimer, empTimer, toastTimer;
    private static Rules modifiedRules;

    /**
     * Вызывать при загрузке контента, после создания планет.
     */
    public static void load() {
        if (!Core.settings.has(NOTIFICATIONS_SETTING)) {
            Core.settings.put(NOTIFICATIONS_SETTING, true);
        }

        empStatus = new mindustry.type.StatusEffect("solar-emp") {{
            show = false;
            speedMultiplier = 0.6f;
            reloadMultiplier = 0.5f;
        }};

        Events.run(Trigger.update, SolarStorm::update);
    }

    private static boolean active() {
        if (!state.isGame() || !state.isCampaign()) return false;
        Sector sector = state.rules.sector;
        return sector != null && sector.planet == InfernoPlanets.infernos;
    }

    private static void update() {
        if (state.isPaused()) return;

        if (!active()) {
            reset();
            return;
        }

        Sector sector = state.rules.sector;
        float seconds = universe.secondsf();

        exposure = Mathf.clamp(Mathf.map(sector.getLight(), SHADOW_LIGHT, FULL_LIGHT, 0f, 1f));
        passiveHeat = PASSIVE_HEAT * exposure;

        float target = SolarActivity.getStormIntensity(seconds) * exposure;
        intensity = Mathf.lerpDelta(intensity, target, 0.15f);

        applySolarRule();

        if (net.client()) return;

        if (Core.settings.getBool(NOTIFICATIONS_SETTING, true)) {
            float currentLevel = intensity < 0.2f ? 0f :
                    intensity < 0.5f ? 0.2f :
                            intensity < 0.7f ? 0.5f : 0.7f;

            if (currentLevel != lastIntensityLevel && !headless) {
                if (currentLevel > lastIntensityLevel) {
                    String text = currentLevel < 0.5f ? "Слабая солнечная активность" :
                            currentLevel < 0.7f ? "Умеренная солнечная активность" :
                                    "Сильная солнечная активность!";
                    Drawable icon = Icon.up;
                    ui.hudfrag.showToast(icon, text);
                } else {
                    ui.hudfrag.showToast(Icon.down, "Снижение солнечной активности");
                }
                lastIntensityLevel = currentLevel;
            }
        }

        damageTimer += Time.delta;
        if (damageTimer >= DAMAGE_INTERVAL) {
            damageTimer -= DAMAGE_INTERVAL;
            radiationPulse(DAMAGE_INTERVAL / 60f);
        }

        empTimer -= Time.delta;
        if (intensity >= EMP_THRESHOLD && empTimer <= 0f) {
            empTimer = EMP_COOLDOWN;
            emp();
        }

    }

    private static void reset() {
        intensity = exposure = passiveHeat = 0f;
        if (modifiedRules != null) {
            modifiedRules.solarMultiplier = BASE_SOLAR;
            modifiedRules = null;
        }
    }

    /**
     * Эффективность солнечных панелей через глобальный множитель правил.
     */
    private static void applySolarRule() {
        float stormDrop = intensity > 0.05f
                ? Mathf.lerp(SOLAR_DROP_MIN, SOLAR_DROP_MAX, intensity) * Mathf.clamp(intensity / 0.25f)
                : 0f;
        float drop = Math.min(0.9f, stormDrop + passiveHeat * 0.5f);

        state.rules.solarMultiplier = BASE_SOLAR * (1f - drop);
        modifiedRules = state.rules;
    }

    private static void radiationPulse(float dt) {
        if (intensity < 0.05f && passiveHeat <= 0f) return;

        if (intensity >= 0.05f) {
            float dps = Mathf.lerp(UNIT_DPS_MIN, UNIT_DPS_MAX, intensity);
            Groups.unit.each(u -> {
                if (u.dead() || shielded(u)) return;

                u.damagePierce(dps * dt);

                if (intensity >= 0.5f) {
                    u.apply(InfernoStatusEffects.empLow, EFFECT_DURATION);
                    u.apply(InfernoStatusEffects.radiationWeak, EFFECT_DURATION);
                } else if (intensity >= 0.2f) {
                    u.apply(InfernoStatusEffects.radiationWeak, EFFECT_DURATION);
                }
            });
        }

        for (TeamData data : state.teams.present) {
            if (intensity >= 0.05f) {
                tmp.clear().addAll(indexer.getFlagged(data.team, BlockFlag.generator));
                for (Building b : tmp) {
                    if (!(b.block instanceof SolarGenerator) || !b.isValid() || covered(b.team, b.x, b.y)) continue;
                    b.damage(b.block.health * PANEL_DAMAGE_PER_SEC * intensity * dt);
                }
            }

            float leak = BATTERY_LEAK_PER_SEC * (intensity + passiveHeat * 0.5f) * dt;
            if (leak > 0f) {
                tmp.clear().addAll(indexer.getFlagged(data.team, BlockFlag.battery));
                for (Building b : tmp) {
                    if (b.power == null || !b.isValid() || covered(b.team, b.x, b.y)) continue;
                    b.power.status = Mathf.clamp(b.power.status - leak);
                }
            }
        }
    }

    /**
     * ЭМИ на пике: замедление и разоружение открытых юнитов, отключение узлов питания.
     */
    private static void emp() {
        Groups.unit.each(u -> {
            if (u.dead() || shielded(u)) return;
            u.apply(empStatus, EMP_UNIT_TIME);
        });

        Groups.build.each(b -> {
            if (!(b.block instanceof PowerNode) || covered(b.team, b.x, b.y)) return;
            b.control(LAccess.enabled, 0, 0, 0, 0);
            Fx.shockwave.at(b.x, b.y);
        });
    }

    /**
     * Защищена ли точка работающим силовым куполом своей команды.
     */
    private static boolean covered(Team team, float x, float y) {
        Seq<Building> shields = indexer.getFlagged(team, BlockFlag.shield);
        for (int i = 0; i < shields.size; i++) {
            Building b = shields.get(i);
            if (b instanceof ForceProjector.ForceBuild f) {
                if (!f.broken && f.realRadius() > 1f && Mathf.within(x, y, f.x, f.y, f.realRadius())) return true;
            } else if (b instanceof BaseShield.BaseShieldBuild s) {
                if (!s.broken && s.radius() > 1f && Mathf.within(x, y, s.x, s.y, s.radius())) return true;
            }
        }
        return false;
    }

    /**
     * Юнит защищён личным щитом-абилкой или куполом команды.
     */
    private static boolean shielded(Unit u) {
        return u.shield() > 0f || covered(u.team, u.x, u.y);
    }
}