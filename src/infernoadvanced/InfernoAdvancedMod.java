package infernoadvanced;

import arc.*;
import arc.graphics.Color;
import arc.util.*;
import infernoadvanced.content.*;
import infernoadvanced.graphics.blackhole.IABlackholeRenderer;
import infernoadvanced.graphics.*;
//import infernoadvanced.graphics.other.ShockwaveRenderer;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.Styles;

public class InfernoAdvancedMod extends Mod {

    public InfernoAdvancedMod() {
        Log.info("Loaded Inferno Advanced.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("@mod.inferno-advanced.name");

                // Основной контент
                dialog.cont.table(content -> {
                    // Левая часть — иконка
                    content.image(Core.atlas.find("inferno-advanced-icon", "inferno-advanced-gear"))
                            .size(96f)
                            .pad(20f)
                            .left();

                    // Правая часть — текст
                    content.table(info -> {
                        info.add("[scarlet]Inferno Advanced[]")
                                .style(Styles.outlineLabel)
                                .fontScale(1.2f)
                                .padBottom(8f)
                                .row();

                        info.add("[lightgray]Version: [accent]alpha build 2.5[]")
                                .padBottom(4f)
                                .row();

                        info.add("[lightgray]Author: [accent][#FF5733]AtomicPickle[]")
                                .padBottom(12f)
                                .row();

                        info.add("[orange]🔥 Пылающий ад технологий 🔥[]")
                                .padBottom(8f)
                                .row();

                        info.add("[lightgray]Добавляет новые турели, реакторы, предметы и эффекты.[]")
                                .wrap()
                                .width(400f)
                                .padBottom(8f)
                                .row();

                        info.add("[lightgray]Особенности:[]")
                                .padBottom(4f)
                                .row();

                        info.table(features -> {
                            features.add("[green]✓[] Чёрная дыра с искажением пространства").left().padBottom(2f).row();
                            features.add("[green]✓[] Новые элементы").left().padBottom(2f).row();
                            features.add("[green]✓[] Мощные турели").left().padBottom(2f).row();
                            features.add("[green]✓[] Уникальные статус-эффекты").left().row();
                        }).padBottom(12f).left();

                        info.add("[yellow]⚠️ Мод в разработке, возможны баги! ⚠️[]")
                                .color(Color.valueOf("#ffaa66"))
                                .padTop(8f);
                    }).pad(10f).left();
                }).pad(10f);

                // Кнопки внизу
                dialog.buttons.button("@ok", dialog::hide)
                        .size(120f, 50f)
                        .pad(10f)
                        .center();

                dialog.show();
            });
        });
    }

    @Override
    public void loadContent() {
        Log.info("Loading Inferno Advanced content...");

        IABlackholeRenderer.init();
//        ShockwaveRenderer.init();
        IATestBlocks.load();

        InfernoSounds.load();
        InfernoStatusEffects.load();
        InfernoItems.load();
        InfernoBulletTypes.load();
        InfernoBlocks.load();
        // InfernoUnits.load();

//        infernoadvanced.content.nuclear.InfernoNuclearLoader.load();
    }
}