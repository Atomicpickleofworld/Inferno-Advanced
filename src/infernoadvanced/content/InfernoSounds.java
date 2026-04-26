package infernoadvanced.content;

import arc.audio.*;
import mindustry.*;

public class InfernoSounds {
    // Объявляем звуки
    public static Sound
            blackholeLoop,
            shoottempestAA,
            hailBang,
            shootSwift,
            shootSwift2,
            hailLaunch;


    public static void load() {
        // Загружаем звуки из папки assets/sounds/ через Vars
        hailLaunch = Vars.tree.loadSound("hail-launch");
        hailBang = Vars.tree.loadSound("hail-bang");
        shootSwift = Vars.tree.loadSound("swift-shoot");
        shootSwift2 = Vars.tree.loadSound("swift-shoot2");
        shoottempestAA = Vars.tree.loadSound("tempestAA-shoot");
        blackholeLoop = Vars.tree.loadSound("blackhole");

    }
}