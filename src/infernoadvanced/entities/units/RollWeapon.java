package infernoadvanced.entities.units;

import infernoadvanced.entities.abilities.BarrelRollAbility;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

/**
 * Оружие, которое «переворачивается» вместе с юнитом во время BarrelRollAbility.
 * <p>
 * Боковое смещение оружия умножается на масштаб переворота, поэтому оружие
 * скользит к оси юнита и переходит на другую сторону. Когда юнит
 * «перевёрнут» (масштаб < 0), спрайт оружия зеркалится.
 * Смещение применяется и при стрельбе, так что снаряды вылетают оттуда,
 * где оружие видно.
 */
public class RollWeapon extends Weapon {
    public RollWeapon() {
        super();
    }

    public RollWeapon(String name) {
        super(name);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount) {
        float s = BarrelRollAbility.scaleOf(unit);
        if (s == 1f) {
            super.draw(unit, mount);
            return;
        }

        float baseX = x;
        boolean baseFlip = flipSprite;
        x = baseX * s;
        if (s < 0f) flipSprite = !baseFlip;
        try {
            super.draw(unit, mount);
        } finally {
            x = baseX;
            flipSprite = baseFlip;
        }
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        float s = BarrelRollAbility.scaleOf(unit);
        if (s == 1f) {
            super.update(unit, mount);
            return;
        }

        float baseX = x;
        x = baseX * s;
        try {
            super.update(unit, mount);
        } finally {
            x = baseX;
        }
    }
}