package infernoadvanced.loaders;

import arc.audio.*;
import mindustry.*;
import mindustry.gen.Sounds;

public class IASounds {
    public static Sound
            blackholeLoop,
            hailBang,
            shootWren,
            shootSwift,
            hailLaunch,
            falconDash,
            windyMovement,
            parry
                    ;

    public static void load() {
        hailLaunch = Vars.tree.loadSound("hail-launch");
        hailBang = Vars.tree.loadSound("hail-bang");
        blackholeLoop = Vars.tree.loadSound("blackhole-loop");
        shootSwift = Vars.tree.loadSound("shootSwift");
        falconDash = Vars.tree.loadSound("falcon-dash");
        windyMovement =  Vars.tree.loadSound("windy-movement");
        parry =  Vars.tree.loadSound("parry");
        shootWren = Sounds.shootAlpha;
    }
}