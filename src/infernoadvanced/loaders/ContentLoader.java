package infernoadvanced.loaders;

import arc.util.Log;
import infernoadvanced.content.*;
import infernoadvanced.content.planets.infernos.InfernosTechTree;
import infernoadvanced.graphics.blackhole.IABlackholeRenderer;

public class ContentLoader {
    public static void load() {
        Log.info("Loading Inferno Advanced content...");

        IAAtlas.load();
        IABlackholeRenderer.init();

        InfernoTeams.load();

        InfernoStatusEffects.load();
        InfernoItems.load();
        InfernoLiquids.load();
        InfernoBullets.load();

        InfernoBlocks.load();
        try {
            Class<?> c = Class.forName("infernoadvanced.content.IATestBlocks");
            c.getMethod("load").invoke(null);
        } catch (ClassNotFoundException e) {
            Log.info("IATestBlocks absent — skipping dev-only content.");
        } catch (ReflectiveOperationException e) {
            Log.err("IATestBlocks failed to load", e);
        }
        InfernoUnits.load();
        InfernoOres.load();

        InfernoTestContent.load();
        InfernoPlanets.load();

        InfernoTechTree.load();
        InfernosTechTree.load();
    }
}