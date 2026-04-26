//package inferno.ui;
//
//import arc.*;
//import arc.scene.*;
//import arc.scene.ui.layout.*;
//import arc.util.*;
//import infernoadvanced.content.InfernoPlanets;
//import mindustry.content.Planets;
//import mindustry.gen.*;
//import mindustry.ui.*;
//import static mindustry.Vars.*;
//
//public class InfernoUI {
//    public boolean showCaves = false;
//
//    public void build(Group parent) {
//        // Добавляем таблицу прямо в родительскую группу
//        parent.addChild(new Table(){{
//            name = "inferno-layer-button";
//
//            // Заставляем таблицу растянуться на весь экран, чтобы top().right() работали
//            setFillParent(true);
//
//            // Позиционирование кнопки
//            top().right().margin(150f, 0f, 0f, 30f);
//
//            button(Icon.downOpen, Styles.cleari, () -> {
//                showCaves = !showCaves;
//
//                // Простое переключение видимости
//                Planets.serpulo.visible = !showCaves;
//
//                ui.showInfoFade(showCaves ? "[accent]Вход в недра..." : "[lightgray]Поверхность");
//            }).size(70f).name("inferno-btn");
//
//            // Упрощаем проверку видимости: пусть пока висит всегда,
//            // чтобы мы поняли, что она вообще работает!
//            visible(() -> ui.planet != null && ui.planet.isShown());
//        }});
//    }
//}