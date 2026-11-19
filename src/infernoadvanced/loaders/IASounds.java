package infernoadvanced.loaders;

import arc.audio.*;
import mindustry.*;

public class IASounds {
    public static Sound
            blackholeLoop,
            shoottempestAA,
            hailBang,
            shootSwift,
            shootSwift2,
            howitzerExplosion,
            howitzerShoot,
            hailLaunch,
            shotUnitSwift,
            falconDash,
            windyMovement,
            parry
                    ;

    //TODO ogg


    public static void load() {
        hailLaunch = Vars.tree.loadSound("hail-launch");
        hailBang = Vars.tree.loadSound("hail-bang");
        shootSwift = Vars.tree.loadSound("swift-shoot");
        shootSwift2 = Vars.tree.loadSound("swift-shoot2");
        shoottempestAA = Vars.tree.loadSound("tempestAA-shoot");
        blackholeLoop = Vars.tree.loadSound("blackhole-loop");
        howitzerExplosion = Vars.tree.loadSound("howitzer-exp"); //TODO изменить
        howitzerShoot = Vars.tree.loadSound("howitzer-shoot"); //TODO укоротить
        shotUnitSwift = Vars.tree.loadSound("shotUnitSwift");
        falconDash = Vars.tree.loadSound("falcon-dash");
        windyMovement =  Vars.tree.loadSound("windy-movement"); //TODO
        parry =  Vars.tree.loadSound("parry"); //TODO тише
    }
}