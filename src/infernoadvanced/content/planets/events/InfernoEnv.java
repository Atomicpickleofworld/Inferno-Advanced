package infernoadvanced.content.planets.events;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.gl.Shader;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Layer;

import static arc.Core.camera;

public class InfernoEnv {
    private static final Color hazeColor = Color.valueOf("ff5a1a");
    private static final float MAX_ALPHA = 0.3f;

    public static void load() {
        Events.run(Trigger.draw, InfernoEnv::drawHaze);
    }

    private static void drawHaze() {
        if (SolarStorm.intensity <= 0.02f) return;
        float in = SolarStorm.intensity;

        Draw.draw(Layer.weather + 1f, () -> {
            Draw.color(hazeColor, Math.min(in * MAX_ALPHA, MAX_ALPHA));
            Fill.rect(camera.position.x, camera.position.y, camera.width, camera.height);
            Draw.reset();
        });

        Shader s = HeatHaze.get();
        if (s != null && in > 0.15f) {
            HeatHaze.intensity = in;
            Draw.draw(Layer.weather + 2f, () -> {
                Draw.shader(s);
                Draw.color(hazeColor, 0.22f);
                Draw.rect(Core.atlas.find("white"),
                        camera.position.x, camera.position.y, camera.width, camera.height);
                Draw.reset();
            });
        }
    }
}