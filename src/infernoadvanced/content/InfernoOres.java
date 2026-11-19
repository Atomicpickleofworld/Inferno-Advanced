package infernoadvanced.content;

import arc.Events;
import arc.math.Rand;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.world.Tile;

public class InfernoOres {

    public static float sectorChance = 0.35f;
    public static int veinsPerSector = 5;
    public static int veinSize = 14;



    public static void load() {
        Events.on(WorldLoadEvent.class, e -> {
            if (!Vars.state.isCampaign()) return;

            int sectorId = Vars.state.getSector() != null ? Vars.state.getSector().id : 0;
            Rand rand = new Rand(sectorId);

            if (!rand.chance(sectorChance)) return;

            boolean hasOre = false;
            for (Tile tile : Vars.world.tiles) {
                if (tile.overlay() == InfernoBlocks.celestineOre) {
                    hasOre = true;
                    break;
                }
            }
            if (hasOre) return;

            for (int i = 0; i < veinsPerSector; i++) {
                growVein(rand.nextInt(Vars.world.width()), rand.nextInt(Vars.world.height()), rand);
            }
        });
    }

    private static void growVein(int cx, int cy, Rand rand) {
        float x = cx, y = cy;
        for (int i = 0; i < veinSize; i++) {
            int tx = (int) x, ty = (int) y;
            if (Vars.world.tiles.in(tx, ty)) {
                Tile tile = Vars.world.tiles.get(tx, ty);
                if (tile.block() == Blocks.air && !tile.floor().isLiquid && tile.drop() == null) {
                    tile.setOverlay(InfernoBlocks.celestineOre);
                }
            }
            x += rand.range(2.2f);
            y += rand.range(2.2f);
        }
    }
}