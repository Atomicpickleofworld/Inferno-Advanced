package infernoadvanced.world.blocks.defense.turrets;

import arc.math.Mathf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;


public class RampUpTurret extends ItemTurret {
    
    public float maxHeat = 100f;
    public float heatPerShot = 0.5f;
    public float coolDownPerTick = 0.2f;
    public float minHeatToShoot = 0f;
    public float maxReloadMultiplier = 3f;
    public boolean showHeatBar = true;

    public RampUpTurret(String name) {
        super(name);
        heatRequirement = -1f;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.reload, 60f / reload * shoot.shots, StatUnit.perSecond);
        stats.add(Stat.reload, "Ускорение до " + (int)maxReloadMultiplier + "x");
        stats.add(Stat.range, "Нагрев: " + (int)maxHeat);
    }

    @Override
    public void setBars() {
        super.setBars();
        
        if (showHeatBar) {
            addBar("heat", (RampUpBuild entity) ->
                new Bar(
                    () -> "Нагрев: " + (int)(entity.currentHeat / maxHeat * 100) + "%",
                    () -> Pal.lightOrange,
                    () -> entity.currentHeat / maxHeat
                )
            );
            
            addBar("speed", (RampUpBuild entity) ->
                new Bar(
                    () -> "Скорость: " + (int)(entity.getReloadMultiplier() * 100) + "%",
                    () -> Pal.accent,
                    () -> entity.getReloadMultiplier() / maxReloadMultiplier
                )
            );
        }
    }

    public class RampUpBuild extends ItemTurretBuild {
        public float currentHeat = 0f;
        public boolean wasShootingLastTick = false;
        public float currentReloadMultiplier = 1f;

        @Override
        public void updateTile() {
            updateHeat();
            
            float multiplier = getReloadMultiplier();
            reloadCounter += delta() * multiplier * baseReloadSpeed();
            reloadCounter = Math.min(reloadCounter, reload);
            
            super.updateTile();
        }

        protected void updateHeat() {
            boolean isShootingNow = isShooting() && hasAmmo() && shootWarmup >= minWarmup;
            
            if (isShootingNow && currentHeat < maxHeat) {
                currentHeat += heatPerShot * delta() * (1f + currentHeat / maxHeat * 0.5f);
                currentHeat = Math.min(currentHeat, maxHeat);
                wasShootingLastTick = true;
            } else if (!isShootingNow && currentHeat > 0) {
                float coolDown = coolDownPerTick * delta();
                if (currentHeat > maxHeat * 0.7f) {
                    coolDown *= 1.5f;
                }
                currentHeat = Math.max(0, currentHeat - coolDown);
                wasShootingLastTick = false;
            }
        }

        public float getReloadMultiplier() {
            if (currentHeat <= 0) return 1f;
            
            float progress = currentHeat / maxHeat;
            float multiplier = 1f + (maxReloadMultiplier - 1f) * (progress * progress);
            return Mathf.clamp(multiplier, 1f, maxReloadMultiplier);
        }

        @Override
        protected float ammoReloadMultiplier() {
            return super.ammoReloadMultiplier() * getReloadMultiplier();
        }

        @Override
        public float warmup() {
            return Mathf.clamp(super.warmup() + currentHeat / maxHeat * 0.5f);
        }

        @Override
        public boolean canConsume() {
            if (minHeatToShoot > 0 && currentHeat < minHeatToShoot) {
                return false;
            }
            return super.canConsume();
        }


        public float efficiency() {
            float baseEfficiency = efficiencyScale();
            if (currentHeat <= 0) return baseEfficiency;
            
            float heatBonus = 0.5f + 0.5f * (currentHeat / maxHeat);
            return baseEfficiency * heatBonus;
        }

        @Override
        public void write(arc.util.io.Writes write) {
            super.write(write);
            write.f(currentHeat);
        }

        @Override
        public void read(arc.util.io.Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                currentHeat = read.f();
            }
        }

        @Override
        public byte version() {
            return 1;
        }
    }
}