package infernoadvanced.entities.units;

import arc.graphics.g2d.Draw;
import infernoadvanced.entities.abilities.BarrelRollAbility;
import infernoadvanced.entities.abilities.DashAbility;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

/**
 * UnitType с визуалом для способностей ветки:
 *  - BarrelRollAbility: сжимает спрайт корпуса по ширине (переворот);
 *  - DashAbility: мигающий цветом команды спрайт во время зарядки рывка.
 *
 * Всё ставится в applyColor: он вызывается прямо перед отрисовкой корпуса
 * и контура, а оружие выставляет свой Draw.xscl само.
 */
public class RollUnitType extends UnitType {
    public RollUnitType(String name) {
        super(name);
    }

    @Override
    public void applyColor(Unit unit) {
        super.applyColor(unit);

        float s = 1f;
        for (Ability a : unit.abilities) {
            if (a instanceof BarrelRollAbility roll) {
                s = roll.scaleX();
            } else if (a instanceof DashAbility dash && dash.charging() && unit.hitTime <= 0f) {
                Draw.mixcol(dash.blinkColor(unit), dash.blinkAlpha());
            }
        }
        Draw.xscl = s;
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
        Draw.xscl = 1f;
    }
}