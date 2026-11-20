package infernoadvanced.world.blocks.defense.turrets;

import infernoadvanced.graphics.parts.GammaChargePart;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.draw.DrawTurret;

public class GammaChargeTurret extends PowerTurret {

    public final GammaChargePart chargePart = new GammaChargePart();
    public boolean addChargePart = true;

    public GammaChargeTurret(String name) {
        super(name);
    }

    @Override
    public void init() {
        if (addChargePart && drawer instanceof DrawTurret) {
            DrawTurret dt = (DrawTurret) drawer;
            if (!dt.parts.contains(chargePart)) dt.parts.add(chargePart);
        }
        super.init();
    }
}