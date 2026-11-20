package infernoadvanced.graphics;

import arc.Core;
import arc.Events;
import arc.func.Boolp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import mindustry.game.EventType.Trigger;

import static mindustry.Vars.state;

public class IAScreenFX {
    private static class Pass {
        final Shader shader;
        final Boolp active;

        Pass(Shader shader, Boolp active) {
            this.shader = shader;
            this.active = active;
        }
    }

    private static final Seq<Pass> passes = new Seq<>();
    private static final Seq<Shader> live = new Seq<>();
    private static FrameBuffer a, b;
    private static boolean capturing;

    public static void add(Shader shader, Boolp active) {
        passes.add(new Pass(shader, active));
    }

    public static void load() {
        Events.run(Trigger.preDraw, IAScreenFX::begin);
        Events.run(Trigger.postDraw, IAScreenFX::end);
    }

    private static void begin() {
        live.clear();
        if (state.isGame()) {
            for (Pass p : passes) {
                if (p.active.get()) live.add(p.shader);
            }
        }

        capturing = live.size > 0;
        if (!capturing) return;

        if (a == null) a = new FrameBuffer();
        a.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        a.begin(Color.clear);
    }

    private static void end() {
        if (!capturing) return;
        capturing = false;

        a.end();
        Draw.blend(Blending.disabled);

        if (live.size == 1) {
            a.blit(live.first());
        } else {
            if (b == null) b = new FrameBuffer();
            int w = Core.graphics.getWidth(), h = Core.graphics.getHeight();
            FrameBuffer src = a, dst = b;

            for (int i = 0; i < live.size; i++) {
                if (i == live.size - 1) {
                    src.blit(live.get(i));
                } else {
                    dst.resize(w, h);
                    dst.begin(Color.clear);
                    src.blit(live.get(i));
                    dst.end();
                    FrameBuffer t = src;
                    src = dst;
                    dst = t;
                }
            }
        }

        Draw.blend();
        live.clear();
    }
}