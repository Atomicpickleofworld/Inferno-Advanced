package infernoadvanced.world.meta;

import mindustry.graphics.Pal;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;

public class IAStats {

    public static final Stat
            loopRadius = new Stat("ia-loop-radius", StatCat.function),
            loopAdvance = new Stat("ia-loop-advance", StatCat.function),
            loopsPerFlight = new Stat("ia-loops", StatCat.function),

    rampDamage = new Stat("ia-ramp-damage", StatCat.function),

    apex = new Stat("ia-apex", StatCat.function),
            maxRange = new Stat("ia-max-range", StatCat.function),
            minRange = new Stat("ia-min-range", StatCat.function);

    public static StatValue ramp(float min, float max) {
        return table -> table.add(min + " → " + max).color(Pal.accent);
    }

    public static StatValue text(String value) {
        return table -> table.add(value).color(Pal.lancerLaser);
    }
}