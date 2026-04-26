//package infernoadvanced.world.blocks;
//
//import arc.graphics.Color;
//import arc.graphics.g2d.*;
//import arc.math.*;
//import arc.util.Time;
//import mindustry.entities.Effect;
//import mindustry.entities.bullet.BulletType;
//import mindustry.gen.Building;
//import mindustry.graphics.*;
//import mindustry.world.blocks.defense.turrets.PowerTurret;
//import mindustry.world.draw.DrawTurret;
//
//public class ChargedTurret extends PowerTurret {
//
//    public Effect chargeEffect = null;
//    public float chargeTime = 60f;
//    public Color chargeColor = Color.valueOf("#ff5533");
//
//    public ChargedTurret(String name) {
//        super(name);
//        shoot.firstShotDelay = chargeTime;
//    }
//
//    public class ChargedTurretBuild extends PowerTurretBuild {
//        public float chargeProgress = 0f;
//
//        @Override
//        public void updateTile() {
//            super.updateTile();
//
//            // Обновляем прогресс зарядки (только если есть энергия)
//            if (charging() && power.status > 0.1f) {
//                chargeProgress = Mathf.approachDelta(chargeProgress, 1f, 1f / chargeTime);
//            } else {
//                chargeProgress = Mathf.approachDelta(chargeProgress, 0f, 0.05f);
//            }
//        }
//
//        @Override
//        public void draw() {
//            super.draw();
//
//            // Рисуем эффект зарядки
//            if (chargeProgress > 0.01f && chargeEffect != null) {
//                float x = this.x + Angles.trnsx(rotation - 90, shootX, shootY);
//                float y = this.y + Angles.trnsy(rotation - 90, shootX, shootY);
//                chargeEffect.at(x, y, rotation, chargeColor, chargeProgress);
//            }
//        }
//
//        @Override
//        public boolean hasAmmo() {
//            // Для PowerTurret — всегда есть аммуниция, если есть энергия
//            return shootType != null && power.status > 0.1f;
//        }
//
//        @Override
//        public BulletType peekAmmo() {
//            return shootType;
//        }
//
//        @Override
//        public BulletType useAmmo() {
//            return shootType;
//        }
//
//        public float chargeProgress() {
//            return chargeProgress;
//        }
//
//        public boolean isCharging() {
//            return charging() && chargeProgress >= 0.99f;
//        }
//    }
//
//}