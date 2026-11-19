package inferno.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
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
        table.button("@mod.inferno-advanced.info", Icon.info, InfernoUI::showModInfo)
                .size(240f, 60f)
                .row();
    }

    //TODO wiki page, full redesign

    public static void showModInfo() {
        BaseDialog dialog = new BaseDialog(Core.bundle.get("mod.inferno-advanced.name"));

        Table contentArea = new Table();
        ButtonGroup<TextButton> tabGroup = new ButtonGroup<>();
        tabGroup.setMinCheckCount(1);

        TextButton overviewTab = tab(Core.bundle.get("mod.inferno-advanced.tab.overview"), tabGroup);
        TextButton helpTab     = tab(Core.bundle.get("mod.inferno-advanced.tab.help"),     tabGroup);
        overviewTab.setChecked(true);

        dialog.cont.table(tabs -> {
            tabs.defaults().size(200f, 50f).pad(2f);
            tabs.add(overviewTab);
            tabs.add(helpTab);
        }).padBottom(10f).row();

        dialog.cont.add(contentArea).grow().pad(10f);

        overviewTab.clicked(() -> showOverview(contentArea));
        helpTab.clicked(() -> showHelp(contentArea));

        showOverview(contentArea);

        dialog.buttons.button("@ok", dialog::hide).size(120f, 50f).pad(10f);
        dialog.show();
    }

    private static TextButton tab(String text, ButtonGroup<TextButton> group) {
        TextButton button = new TextButton(text, Styles.flatTogglet);
        group.add(button);
        return button;
    }

    /* ------------------------- OVERVIEW ------------------------- */

    private static void showOverview(Table content) {
        content.clear();
        content.pane(Styles.noBarPane, pane -> {
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
    }

    /* --------------------------- HELP --------------------------- */

    private static void showHelp(Table content) {
        content.clear();
        content.pane(Styles.noBarPane, pane -> {
            pane.table(help -> {
                help.defaults().left().padBottom(6f);

                help.add(Core.bundle.get("mod.inferno-advanced.help.title"))
                        .style(Styles.outlineLabel).fontScale(1.25f).padBottom(14f).row();

                helpSection(help, "getting-started");
                helpSection(help, "items");
                helpSection(help, "turrets");
                helpSection(help, "blackhole");
                helpSection(help, "radiation");
                helpSection(help, "mines");
                helpSection(help, "units");
                helpSection(help, "tips");
            }).pad(10f).grow();
        }).grow();
    }

    private static void helpSection(Table parent, String key) {
        parent.add(Core.bundle.get("mod.inferno-advanced.help." + key + ".title"))
                .style(Styles.outlineLabel).padTop(8f).padBottom(2f).row();

        parent.add(Core.bundle.get("mod.inferno-advanced.help." + key + ".desc"))
                .wrap().width(500f).padBottom(10f).row();
    }
}