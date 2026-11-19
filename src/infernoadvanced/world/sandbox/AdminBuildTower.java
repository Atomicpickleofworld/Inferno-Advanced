package infernoadvanced.world.sandbox;

import arc.math.Mathf;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.meta.BuildVisibility;
import mindustry.type.Category;
import mindustry.type.ItemStack;

/**
 * Админ-блок: автоматически восстанавливает постройки в настраиваемом радиусе.
 * Диапазон задаётся через ЛКМ (текстовое поле).
 * Только для песочницы. Не потребляет ресурсы. Практически неразрушаем.
 */
public class AdminBuildTower extends BuildTurret {

    public float minRange = 10f;
    public float maxRange = 500f;

    public AdminBuildTower(String name) {
        super(name);
        configurable = true;
        update = true;
        solid = true;

        health = 500_000;
        armor = 250f;

        buildSpeed = 20f;
        size = 2;

        buildVisibility = BuildVisibility.sandboxOnly;
        requirements(Category.effect, ItemStack.empty);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
        removeBar("power");
        removeBar("liquid");
    }


    public class AdminBuildTowerBuild extends BuildTurretBuild {

        public float customRange = range;

        @Override
        public float range() {
            return customRange;
        }


        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> {
                t.add("Радиус:").padRight(6f);

                TextField field = t.field(String.valueOf((int) customRange), val -> {
                    configure(Mathf.clamp(Strings.parseFloat(val), minRange, maxRange));
                }).width(100f).get();

//                field.setFilter(TextField.textFieldFilterFloat);

                t.button("-", () -> {
                    customRange = Mathf.clamp(customRange - 25f, minRange, maxRange);
                    configure(customRange);
                    field.setText(String.valueOf((int) customRange));
                }).size(40f).padLeft(6f);

                t.button("+", () -> {
                    customRange = Mathf.clamp(customRange + 25f, minRange, maxRange);
                    configure(customRange);
                    field.setText(String.valueOf((int) customRange));
                }).size(40f).padLeft(4f);

            }).growX().pad(6f);
        }

        @Override
        public void configured(Unit builder, Object value) {
            if (value instanceof Float f) {
                customRange = Mathf.clamp(f, minRange, maxRange);
            }
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(customRange);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                customRange = Mathf.clamp(read.f(), minRange, maxRange);
            }
        }
    }
}