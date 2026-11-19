package infernoadvanced.content;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class InfernoLiquids {

    public static Liquid

            oilWaste, carbonDioxide

    ;

    public static void load() {
        oilWaste = new Liquid("oil-waste", Color.valueOf("2b1f0f")) {{
            temperature = 0.7f;
            viscosity = 0.9f;
            flammability = 0.8f;
            explosiveness = 0.6f;
            heatCapacity = 0.5f;
            effect = StatusEffects.tarred;
            barColor = Color.valueOf("4a3520");
            gasColor = Color.valueOf("3d2b1a");
            boilPoint = 0.8f;
        }};

        carbonDioxide = new Liquid("carbon-dioxide", Color.valueOf("8a8a8a")) {{
            gas = true;
            temperature = 0.4f;
            heatCapacity = 0.3f;
            flammability = 0f;
            explosiveness = 0f;
            coolant = true;
            gasColor = Color.valueOf("a0a0a0");
            barColor = Color.valueOf("6e6e6e");
            boilPoint = 0.3f;
        }};
    }
}
