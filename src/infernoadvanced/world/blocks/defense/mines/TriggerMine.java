package infernoadvanced.world.blocks.defense.mines;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class TriggerMine extends Block {
    public float teamAlpha = 0.5f;
    public BulletType bullet;
    public int shots = 1;
    public float cone = 360f;
    public boolean triggerAir = false;
    public boolean oneShot = true;
    public float wearDamage = 20f;
    public float empRadius = 0f, empDamage = 0f;
    public StatusEffect status = StatusEffects.none;
    public float statusDuration = 60f;
    public boolean breakShields = false;

    public TriggerMine(String name) {
        super(name);
        update = false;
        destructible = true;
        solid = false;
        targetable = false;
        hasShadow = false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.targetsGround, true);
        stats.add(Stat.targetsAir, triggerAir);

        if (bullet != null) {
            stats.add(Stat.shots, shots);
            stats.add(Stat.damage, bullet.damage, StatUnit.perShot);
        }

        if (empRadius > 0f) {
            stats.add(Stat.range, empRadius / tilesize);
            stats.add(Stat.damage, empDamage);
        }

        if (!oneShot) {
            stats.add(Stat.health, health);
        }
    }

    public class TriggerMineBuild extends Building {
        @Override
        public void drawTeam() {
        }

        @Override
        public void draw() {
            Draw.rect(block.region, x, y);
            if (block.teamRegion.found()) {
                Draw.color(team.color, teamAlpha);
                Draw.rect(block.teamRegion, x, y);
                Draw.color();
            }
        }

        @Override
        public void unitOn(Unit unit) {
            if (!enabled || unit.team == team) return;
            if (!triggerAir && !unit.isGrounded()) return;
            detonate(angleTo(unit));
        }

        protected void detonate(float angle) {
            if (empRadius > 0f) {
                Damage.damage(team, x, y, empRadius, empDamage);
                Units.nearbyEnemies(team, x, y, empRadius, u -> {
                    if (triggerAir || u.isGrounded()) {
                        if (status != StatusEffects.none) u.apply(status, statusDuration);
                        if (breakShields && u.shield > 0f) {
                            u.shield(0f);
                            Fx.unitShieldBreak.at(u.x, u.y, u.hitSize, u.type.shieldColor(u), u);
                        }
                    }
                });
                Fx.shockwave.at(x, y);
            }
            if (bullet != null) {
                for (int i = 0; i < shots; i++) {
                    bullet.create(this, team, x, y, angle + Mathf.range(cone / 2f));
                }
            }
            if (oneShot) {
                Fx.blastExplosion.at(x, y);
                kill();
            } else {
                damage(wearDamage);
            }
        }
    }
}