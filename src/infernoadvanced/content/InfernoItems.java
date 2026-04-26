package infernoadvanced.content;


import arc.graphics.Color;
import mindustry.type.Item;
public class InfernoItems {
    public static Item strontium;
    public static Item celestine;

    public static Item barium133;      // ✅ Новый: Барий-133
    public static Item barium137;      // ✅ Новый: Барий-137
    public static Item cesium137;

    public static Item refractoryBronze;

    public static void load() {

        // radioactivity
        celestine = new Item("celestine", Color.valueOf("91a3b0")){{

            localizedName = "Celestine";
            description = "A heavy, crystalline mineral. Used as a primary source for Strontium extraction. Due to its chemical properties, it burns with a faint crimson hue.";

            cost = 1f;
            hardness = 3;
            // Стоимость постройки (чуть дороже титана)
            cost = 1.2f;
            // Воспламеняемость (в реальности используется в сигнальных огнях)
            flammability = 0.4f;
            // Радиоактивность (сам по себе он почти не фонит, оставим 0.1 для порядка)
            radioactivity = 0.1f;
            // Взрывоопасность
            explosiveness = 0.0f;



        }};

        // ██████ БАРИЙ-133 (средняя радиоактивность) ██████
        barium133 = new Item("barium-133"){{
            localizedName = "Barium-133";
            description = "A moderately radioactive isotope of barium. Used in medical imaging and industrial radiography. Half-life: 10.5 years.";

            cost = 1.8f;
            alwaysUnlocked = false;

            radioactivity = 1.47f;  // ✅ Реальная радиоактивность изотопа 133
            explosiveness = 0.12f;
            flammability = 0.03f;

            color = Color.valueOf("#a8e6cf");  // ✅ Зелёно-серый (менее активный)
        }};





        // ██████ ЦЕЗИЙ-137 (ярко-жёлтый, светится) ██████
        cesium137 = new Item("cesium-137") {{
            localizedName = "Caesium-137";
            description = "A highly radioactive isotope of caesium. Produced from thorium in nuclear reactions. Emits powerful gamma radiation. Handle with care.";

            cost = 2.2f;
            alwaysUnlocked = false;

            // Радиоактивность (выше среднего)
            radioactivity = 2.8f;
            explosiveness = 0.35f;
            flammability = 0.02f;

            // Уникальный цвет: ЯРКО-ЖЁЛТЫЙ (как настоящий цезий)
            color = Color.valueOf("#f2b063");  // Тёплый жёлтый

        }};

        strontium = new Item("strontium"){{
            localizedName = "Strontium";
            description = "A highly radioactive alkaline earth metal. Essential for nuclear reactions. Requires careful handling.";

            cost = 1.6f;
            alwaysUnlocked = false;

            radioactivity = 2.68f;
            explosiveness = 0.15f;
            flammability = 0.05f;

            color = Color.valueOf("#48db79");
        }};


            // ██████ БАРИЙ-137 (высокая радиоактивность) ██████
            barium137 = new Item("barium-137"){{
                localizedName = "Barium-137";
                description = "A highly radioactive isotope of barium. Product of cesium-137 decay. Extremely dangerous without proper shielding. Half-life: 2.55 minutes (metastable).";

                cost = 2.4f;
                alwaysUnlocked = false;

                radioactivity = 3.17f;  // ✅ Реальная радиоактивность изотопа 137
                explosiveness = 0.22f;
                flammability = 0.02f;

                color = Color.valueOf("ff8b8b");  // ✅ Тёмно-зелёный (более активный)
            }};


        //basic

        refractoryBronze = new Item("refractory-bronze", Color.valueOf("#b57b4a")) {{
            localizedName = "Тугоплавкая бронза";
            description = "Усовершенствованный сплав меди, свинца и графита. Графит значительно повышает температуру плавления и жаростойкость материала, делая его идеальным для стволов турелей среднего уровня. Выдерживает до 1200°C без деформации.";
            cost = 1.5f; // Цена продажи (дороже титана)
            hardness = 3; // Уровень добычи (как титан)
            flammability = 0f; // ✅ Не горит (тугоплавкая!)
            explosiveness = 0f;
            radioactivity = 0f;
            charge = 0f;
            healthScaling = 0.3f; // Бонус к здоровью построек
        }};




        }
    }




