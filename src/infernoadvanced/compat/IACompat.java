package infernoadvanced.compat;

import arc.struct.ObjectFloatMap;
import arc.util.Log;
import infernoadvanced.content.InfernoItems;
import mindustry.content.Blocks;
import mindustry.world.consumers.ConsumeItemRadioactive;

public class IACompat {

    public static void apply() {
        patchRtgFuel();
    }

    private static void patchRtgFuel() {
        ConsumeItemRadioactive rad = (ConsumeItemRadioactive) Blocks.rtgGenerator
                .findConsumer(c -> c instanceof ConsumeItemRadioactive);

        if (rad == null) {
            Log.warn("IACompat: ConsumeItemRadioactive not found in rtg-generator, upstream API changed");
            return;
        }

        if (rad.itemDurationMultipliers == null) {
            rad.itemDurationMultipliers = new ObjectFloatMap<>();
        }
        rad.itemDurationMultipliers.put(InfernoItems.strontium, 0.5f / 3.5f);
        rad.itemDurationMultipliers.put(InfernoItems.cesium137, 0.5f / 3.3f);
        rad.itemDurationMultipliers.put(InfernoItems.barium133, 0.5f / 3.8f);
        rad.itemDurationMultipliers.put(InfernoItems.barium137m, 0.5f / 10.1f);
    }
}