package infernoadvanced.world.blocks.defense.mines;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class TripwireMine extends Block {
    public float teamAlpha = 0.5f;
    public float range = 70f;
    public float cone = 40f;
    public int shots = 7;
    public float fuseTime = 10f;
    public BulletType bullet;
    public Sound tripSound = Sounds.explosion;
    public float tripSoundVolume = 0.8f;
    public float tripSoundPitchMin = 0.9f, tripSoundPitchMax = 1.1f;

    public TripwireMine(String name) {
        super(name);
        update = true;
        destructible = true;
        solid = false;
        targetable = false;
        hasShadow = false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range / tilesize);
        stats.add(Stat.shots, shots);
        stats.add(Stat.targetsGround, true);
        stats.add(Stat.targetsAir, false);

        if (bullet != null) {
            stats.add(Stat.damage, bullet.damage, StatUnit.perShot);
        }

        stats.add(Stat.reload, fuseTime / 60f, StatUnit.seconds);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class TripwireMineBuild extends Building {
        public float fuse = -1f;
        public float fuseAngle = 0f;

        @Override
        public void drawTeam() {
        }

        @Override
        public void drawSelect() {
            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void draw() {
            if (fuse >= 0f) {
                Draw.mixcol(Color.red, Mathf.absin(3f, 0.6f));
            }
            Draw.rect(block.region, x, y);
            if (block.teamRegion.found()) {
                Draw.color(team.color, teamAlpha);
                Draw.rect(block.teamRegion, x, y);
                Draw.color();
            }
            Draw.reset();
        }

        @Override
        public void updateTile() {
            if (fuse >= 0f) {
                fuse -= Time.delta;
                if (fuse <= 0f) detonate(fuseAngle);
                return;
            }

            if (timer.get(0, 4f)) {
                Unit t = Units.closestEnemy(team, x, y, range, u -> u.isGrounded());
                if (t != null) {
                    fuseAngle = angleTo(t);
                    fuse = fuseTime;
                    tripSound.at(x, y, Mathf.random(tripSoundPitchMin, tripSoundPitchMax), tripSoundVolume);
                }
            }
        }

        protected void detonate(float angle) {
            for (int i = 0; i < shots; i++) {
                bullet.create(this, team, x, y, angle + Mathf.range(cone / 2f));
            }
            Fx.blastExplosion.at(x, y);
            kill();
        }
    }
}