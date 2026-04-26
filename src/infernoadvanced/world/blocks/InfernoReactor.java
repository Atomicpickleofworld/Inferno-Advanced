package infernoadvanced.world.blocks;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import infernoadvanced.content.InfernoBulletTypes;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.*;

public class InfernoReactor extends PowerGenerator {
    public Color colorStart = Color.valueOf("a3ff8c");
    public float warmupSpeed = 0.0008f; // Примерно 20 секунд (1 / (60 * 20))

    public InfernoReactor(String name){
        super(name);
        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        outputsPower = true;
        consumesPower = true; // Нужно электричество для старта

        // Настройка звука
        ambientSound = Sounds.loopElectricHum;
        ambientSoundVolume = 0.6f;
    }

    public class InfernoReactorBuild extends GeneratorBuild {
        public float warmup = 0f; // Прогресс разгона 0..1
        public float itemProgress = 0f;
        public float itemDuration = 180f;
        public float totalRotation = 0f; // Наша новая переменная для плавного вращения

        @Override
        public void updateTile(){
            // 1. Условие работы: Нужен стронций И внешнее питание 500 (power.status > 0)
            boolean hasResources = items.total() > 0;
            boolean isPowered = power.status > 0.1f;

            if(hasResources && isPowered){
                // Плавный разгон до 1.0 за 20 секунд
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);

                // Сжигание топлива (только если разогрелись хоть немного)
                if(warmup > 0.1f){
                    itemProgress += edelta() / itemDuration;
                    if(itemProgress >= 1f){
                        consume();
                        itemProgress = 0f;
                    }
                }
            } else {
                // Если нет тока или топлива - быстро остываем
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed * 2f);
            }

            // Эффективность зависит от уровня разгона
            productionEfficiency = warmup;
            totalRotation += warmup * 2f * edelta();

            super.updateTile();

        }

        @Override
        public float getPowerProduction(){
            // Выдаем энергию только если прогрелись выше 20%
            return (enabled && warmup > 0.2f) ? warmup * powerProduction : 0f;
        }



        @Override
        public void draw() {
            super.draw();

            if(warmup > 0.01f){
                float f = warmup;
                Draw.blend(Blending.additive);
                Draw.color(colorStart, f * 0.6f);

                // Теперь используем totalRotation вместо Time.time * f
                for(int i = 0; i < 4; i++){
                    Drawf.tri(x, y, 8f * f, 35f * f, i * 90 + totalRotation);
                }

                Draw.blend();
                Draw.reset();
            }
        }





        @Override
        public void onDestroyed(){
            // Взрыв увеличен (теперь он берется из deathExplosion, который мы настроим ниже)
            if(warmup > 0.5f || items.total() > 0){
                if(InfernoBulletTypes.deathExplosion != null){
                    InfernoBulletTypes.deathExplosion.create(null, team, x, y, 0, 0, 1f);
                }
                Sounds.explosionReactor2.at(x, y);
            }
            super.onDestroyed();
        }
    }
}