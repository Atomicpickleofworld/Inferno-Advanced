package infernoadvanced.world.blocks;

import arc.math.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.graphics.*;
import mindustry.entities.*;
import mindustry.world.draw.*;
import arc.graphics.*;
import mindustry.world.*;
//created by atomic pickle - inf adv
import static mindustry.Vars.*;

public class AATurret extends ItemTurret {

    public AATurret(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
    }

    public class SwiftAABuild extends ItemTurretBuild {

        @Override
        public void updateTile() {
            super.updateTile();

            // Считаем врагов в радиусе
            int enemies = Groups.unit.count(u ->
                    u.team != team && u.isEnemy() && u.within(x, y, range) && u.isFlying()
            );

            if (enemies > 5) {
                // Режим ППО: +50% скорострельности, точность падает
                reload = 1f;  // 2.5 / 1.5 = примерно 9 выстрелов в сек
                inaccuracy = 8f;
            } else {
                reload = 2.5f;   // Обычный режим
                inaccuracy = 2f;
            }
        }
    }

    public Building build(Building prev) {
        return new SwiftAABuild();
    }
}