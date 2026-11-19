package infernoadvanced.content;

import arc.graphics.Color;
import arc.util.Log;
import mindustry.game.Team;

public class InfernoTeams{
    public static final int CINIS_ID = 20;

    public static Team cinis;

    public static void load(){
        cinis = Team.get(CINIS_ID);
        cinis.name = "cinis";
        cinis.setPalette(
                Color.valueOf("a5f2fa"),
                Color.valueOf("2ad4e6"),
                Color.valueOf("15708a")
        );
        cinis.color.set(Color.valueOf("2ad4e6"));

        Log.info("[IA] team @ ready (id @, palette @)", cinis.name, cinis.id, cinis.hasPalette);
    }
}