package infernoadvanced;

import arc.Events;
import arc.util.Log;
import arc.util.Time;
import infernoadvanced.compat.IACompat;
import infernoadvanced.content.planets.events.InfernoEnv;
import infernoadvanced.graphics.IAScreenFX;
import infernoadvanced.graphics.SolarHeatShader;
import infernoadvanced.loaders.ContentLoader;
import infernoadvanced.loaders.IAMeta;
import infernoadvanced.loaders.IASounds;
import inferno.ui.InfernoUI;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;

public class InfernoAdvancedMod extends Mod {

    public InfernoAdvancedMod() {
//        Log.info("Loaded Inferno Advanced.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, InfernoUI::init);
        });
    }

    @Override
    public void loadContent() {
//        Log.info("Loading Inferno Advanced main files...");

        IASounds.load();
        ContentLoader.load();
    }

    @Override
    public void init() {
        IAMeta.load();
        IACompat.apply();
        InfernoEnv.load();
        IAScreenFX.load();
        SolarHeatShader.load();
    }

    /*
    TODO add global changes warn
    TODO Ui settings integration
    TODO eng bundle

    TODO integration ores to serpulo
    TODO build cost, vanilla conf blocks(costs, time, health and etc.)
    TODO fix co2 vent indicator
    TODO IAPal color for mines
    TODO hail bullet visual better
    TODO sievert, remake+redesign(shoot part)

    TODO find to use all new items
    TODO normal GP rotation 4 item, from RL
    TODO Rbronze find normal use
    TODO clear cm

    TODO bltyps Push, BH, Rico to stable vers
    TODO wrld RampUp
    TODO cont statusE

    TODO Т-90М secret unit(?)
    */

    /*src/infernoadvanced/content/InfernoBullets.java
    TODO colors by vanilla pal, fix effects
     {
     hail, downpour, skyfall, reactorBlast(add fire pods)
     }
     */

    /*src/infernoadvanced/entities/abilities/DashAbility.java + Roll
    TODO infocard in infomenu
    */

    /*src/infernoadvanced/content/InfernoLiquids.java
    TODO fix hardcode colors
    */

    /*src/infernoadvanced/content/InfernoOres.java
    TODO ts shit, dead code btw
    */

    /*src/infernoadvanced/content/InfernoStatusEffects.java
    TODO fix effects gravityCollapse and texture
    */

    /*src/infernoadvanced/content/InfernoUnits.java
    TODO Reconstructors
    TODO вынести в отдельный мод Inferno Advanced: Aircraft.
    TODO move sound
     */

}