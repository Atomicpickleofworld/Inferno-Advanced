//package infernoadvanced.graphics.other;
//
//import arc.*;
//import arc.graphics.*;
//import arc.graphics.g2d.*;
//import arc.graphics.gl.*;
//import arc.struct.*;
//import arc.util.*;
//import mindustry.Vars;
//import mindustry.game.EventType.*;
//import mindustry.graphics.*;
//
//public class ShockwaveRenderer {
//    private final Seq<Shockwave> waves = new Seq<>();
//    private static ShockwaveRenderer renderer;
//
//    private FrameBuffer buffer;
//    private ShockwaveShader shader;
//    private boolean isBufferActive = false; // Флаг для отслеживания состояния буфера
//
//    protected ShockwaveRenderer() {
//        if (!Vars.headless) {
//            shader = new ShockwaveShader();
//            buffer = new FrameBuffer();
//            Events.run(Trigger.draw, this::advancedDraw);
//        }
//    }
//
//    public static void init() {
//        if (renderer == null) renderer = new ShockwaveRenderer();
//    }
//
//    public static void addWave(float x, float y, float radius, float strength, float life) {
//        if (!Vars.headless) renderer.addWaveInternal(x, y, radius, strength, life);
//    }
//
//    private void advancedDraw() {
//        if (waves.isEmpty()) return;
//
//        // Обновляем волны
//        for (int i = 0; i < waves.size; i++) {
//            Shockwave w = waves.get(i);
//            w.life -= Time.delta;
//            w.radius += Time.delta * 300f;
//            w.time += Time.delta * 10f;
//
//            if (w.life <= 0f) {
//                waves.remove(i);
//                i--;
//            }
//        }
//
//        if (waves.isEmpty()) return;
//
//        // Начинаем захват в буфер на слое background - 1 (как в черной дыре)
//        Draw.draw(Layer.background - 1, () -> {
//            if (!isBufferActive) {
//                buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
//                buffer.begin();
//                isBufferActive = true;
//            }
//        });
//
//        // Применяем шейдер на слое max - 1 (как в черной дыре)
//        Draw.draw(Layer.max - 1, () -> {
//            if (isBufferActive) {
//                buffer.end();
//                isBufferActive = false;
//            }
//
//            // Подготавливаем данные волн для шейдера
//            float[] waveData = new float[waves.size * 4];
//            for (int i = 0; i < waves.size; i++) {
//                Shockwave w = waves.get(i);
//                waveData[i * 4] = w.x;
//                waveData[i * 4 + 1] = w.y;
//                waveData[i * 4 + 2] = w.radius;
//                waveData[i * 4 + 3] = w.strength;
//            }
//
//            shader.waveData = waveData;
//            shader.waveCount = waves.size;
//
//            // Применяем шейдер через blit
//            buffer.blit(shader);
//
//            // Очищаем волны после применения
//            waves.clear();
//        });
//    }
//
//    private void addWaveInternal(float x, float y, float radius, float strength, float life) {
//        waves.add(new Shockwave(x, y, radius, strength, life, 0f));
//    }
//
//    private static class Shockwave {
//        float x, y, radius, strength, life, time;
//
//        Shockwave(float x, float y, float radius, float strength, float life, float time) {
//            this.x = x;
//            this.y = y;
//            this.radius = radius;
//            this.strength = strength;
//            this.life = life;
//            this.time = time;
//        }
//    }
//}