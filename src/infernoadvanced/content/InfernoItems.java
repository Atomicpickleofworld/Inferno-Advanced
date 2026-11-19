package infernoadvanced.content;

import arc.graphics.Color;
import infernoadvanced.graphics.IAPal;
import mindustry.content.StatusEffects;
import mindustry.type.Item;
import mindustry.type.Liquid;

public class InfernoItems {
    public static Item
    ferrite,

    refractoryBronze,

    celestine, strontium,

    barium133, barium137m,
    cesium137,

    cermet, strontiumTitanate,

    sitall

    ;

    //TODO progression sort

    public static void load() {
        refractoryBronze = new Item("refractory-bronze", IAPal.refractoryBronze) {{

            cost = 1.5f;
            healthScaling = 0.3f;

            flammability = 0f;
            explosiveness = 0f;
            radioactivity = 0f;

            buildable = true;
            alwaysUnlocked = false;
        }};

        celestine = new Item("celestine", IAPal.celestine) {{

            hardness = 3;
            cost = 1.2f;

            flammability = 0.1f;
            radioactivity = 0.12f;
            explosiveness = 0f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        strontium = new Item("strontium", IAPal.strontium) {{

            cost = 1.6f;

            radioactivity = 3.5f; //1.9
            explosiveness = 0.15f;
            flammability = 0.05f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        barium133 = new Item("barium-133", IAPal.barium133) {{

            cost = 1.8f;

            radioactivity = 3.8f; //1.8
            explosiveness = 0.12f;
            flammability = 0.03f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        barium137m = new Item("barium-137m", IAPal.barium137m) {{

            cost = 2.4f;

            radioactivity = 10.1f; //2.4
            explosiveness = 0.22f;
            flammability = 0.02f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        cesium137 = new Item("cesium-137", IAPal.cesium137) {{

            cost = 2.2f;

            radioactivity = 3.3f; //1.6
            explosiveness = 0.35f;
            flammability = 0.35f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        ferrite = new Item("ferrite", IAPal.ferrite) {{

            hardness = 1;
            cost = 1f;
            healthScaling = 0.2f;

            flammability = 0f;
            explosiveness = 0f;
            radioactivity = 0f;

            buildable = true;
            alwaysUnlocked = false;
        }};

        cermet = new Item("cermet", IAPal.cermet) {{
            cost = 1.9f;
            healthScaling = 0.45f;

            flammability = 0f;
            explosiveness = 0f;
            radioactivity = 0f;

            buildable = true;
            alwaysUnlocked = false;
        }};

        strontiumTitanate = new Item("strontium-titanate", IAPal.strontiumTitanate) {{
            cost = 2.1f;

            radioactivity = 1.4f;
            explosiveness = 0f;
            flammability = 0f;

            buildable = false;
            alwaysUnlocked = false;
        }};

        sitall = new Item("sitall", IAPal.sitall) {{
            cost = 1.9f;
            healthScaling = 0.35f;

            flammability = 0f;
            explosiveness = 0f;
            radioactivity = 0f;

            buildable = true;
            alwaysUnlocked = false;
        }};


    }
}

