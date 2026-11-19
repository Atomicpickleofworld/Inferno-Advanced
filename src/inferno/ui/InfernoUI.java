package inferno.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import infernoadvanced.content.planets.events.SolarStorm;
import infernoadvanced.loaders.IAMeta;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class InfernoUI {

    public static void init() {
        ui.settings.addCategory("@mod.inferno-advanced.name", Icon.info, InfernoUI::buildSettings);
    }

    private static void buildSettings(Table table) {
        table.button("@mod.inferno-advanced.about", Icon.info, InfernoUI::showAboutMod)
                .size(240f, 60f).row();

        table.button("@mod.inferno-advanced.wiki", Icon.book, InfernoUI::openWiki)
                .size(240f, 60f).row();

        table.button("@mod.inferno-advanced.settings", Icon.settings, InfernoUI::showModSettings)
                .size(240f, 60f).row();
    }

    public static void showAboutMod() {
        BaseDialog dialog = new BaseDialog(Core.bundle.get("mod.inferno-advanced.about"));

        dialog.cont.pane(Styles.noBarPane, pane -> {
            pane.table(info -> {
                info.image(Core.atlas.find("inferno-advanced-icon", "inferno-advanced-frog"))
                        .size(96f).pad(20f).left();

                info.table(text -> {
                    text.add(Core.bundle.get("mod.inferno-advanced.title"))
                            .style(Styles.outlineLabel).fontScale(1.4f).padBottom(8f).row();

                    text.add(Core.bundle.format("mod.inferno-advanced.version", IAMeta.versionString()))
                            .padBottom(4f).row();

                    text.add(Core.bundle.format("mod.inferno-advanced.author", IAMeta.authorString()))
                            .padBottom(12f).row();

                    text.add(Core.bundle.get("mod.inferno-advanced.slogan"))
                            .padBottom(8f).row();

                    text.add(Core.bundle.get("mod.inferno-advanced.description"))
                            .wrap().width(400f).padBottom(8f).row();

                    text.add(Core.bundle.get("mod.inferno-advanced.features.title"))
                            .padBottom(4f).row();

                    text.table(features -> {
                        features.defaults().left().padBottom(4f);
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.blackhole")).row();
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.elements")).row();
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.turrets")).row();
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.statuses")).row();
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.mines")).row();
                        features.add(Core.bundle.get("mod.inferno-advanced.feature.units")).row();
                    }).padBottom(12f).left();

                    text.add(Core.bundle.get("mod.inferno-advanced.wip"))
                            .color(Color.valueOf("#ffaa66")).padTop(8f);
                }).pad(10f).left();
            }).pad(10f);
        }).grow();

        dialog.buttons.button("@ok", dialog::hide).size(120f, 50f).pad(10f);
        dialog.show();
    }

    public static void openWiki() {
    }


    public static void showModSettings() {
        BaseDialog dialog = new BaseDialog(Core.bundle.get("mod.inferno-advanced.settings"));

        dialog.cont.pane(Styles.noBarPane, pane -> {
            pane.table(settings -> {
                settings.defaults().left().padBottom(6f).width(400f);

                settings.add(Core.bundle.get("mod.inferno-advanced.settings.title"))
                        .style(Styles.outlineLabel).fontScale(1.25f).padBottom(14f).row();

                var cell = settings.check("Уведомления о солнечной активности",
                        val -> Core.settings.put(SolarStorm.NOTIFICATIONS_SETTING, val));
                cell.get().setChecked(Core.settings.getBool(SolarStorm.NOTIFICATIONS_SETTING, true));
                cell.padBottom(10f).row();

            }).pad(10f).grow();
        }).grow();

        dialog.buttons.button("@ok", dialog::hide).size(120f, 50f).pad(10f);
        dialog.show();
    }
}