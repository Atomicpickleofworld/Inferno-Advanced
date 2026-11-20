package infernoadvanced.world.blocks.defense.other;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import infernoadvanced.content.InfernoLiquids;
import infernoadvanced.graphics.IAFx;
import mindustry.audio.SoundLoop;
import mindustry.entities.Fires;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//TODO Показывать красный когда сам знаешь когда👍

public class GasVent extends Block {
    public Liquid ventLiquid = InfernoLiquids.carbonDioxide;
    public float extinguishRadius = 80f;
    public float extinguishPower = 60f;
    public float useAmount = 30f;
    public float scanInterval = 6f;
    public float cloudChance = 0.15f;

    public Stat extinguishStat = new Stat("co2-extinguish");

    public GasVent(String name) {
        super(name);
        update = true;
        solid = true;
        hasLiquids = true;
        liquidCapacity = 60f;
        group = BlockGroup.liquids;
        canOverdrive = false;
    }

    @Override
    public void init() {
        consume(new ConsumeLiquid(ventLiquid, useAmount / 60f));
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, Strings.autoFixed(extinguishRadius / tilesize, 1), StatUnit.blocks);
        stats.add(extinguishStat, Strings.autoFixed(extinguishPower, 1), StatUnit.perSecond);
    }

    public class GasVentBuild extends Building {
        public final Seq<Tile> targets = new Seq<>();
        public final SoundLoop soundLoop = new SoundLoop(Sounds.loopSpray, 0.3f);

        public float scanTimer = 0f;
        public boolean extinguishing = false;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            noSleep();
            return liquid == ventLiquid && liquids.get(liquid) < liquidCapacity - 0.001f;
        }

        @Override
        public void updateTile() {
            scanTimer += Time.delta;

            if (scanTimer >= scanInterval) {
                scanTimer = 0f;
                refreshTargets();
            }

            targets.removeAll(t -> t == null || !Fires.has(t.x, t.y));

            extinguishing = false;

            if (!targets.isEmpty() && liquids.get(ventLiquid) > 0.001f) {
                for (Tile t : targets) {
                    Fires.extinguish(t, extinguishPower);
                }

                extinguishing = true;
            }

            soundLoop.update(x, y, extinguishing);

            if (extinguishing && Mathf.chanceDelta(cloudChance)) {
                IAFx.Particles.co2VentSmoke.at(x, y, extinguishRadius);
            }
        }

        @Override
        public BlockStatus status() {
            if (!enabled) {
                return BlockStatus.logicDisable;
            }

            if (!shouldConsume()) {
                return BlockStatus.noOutput;
            }

            if (efficiency <= 0f) {
                return BlockStatus.noInput;
            }

            return extinguishing ? BlockStatus.active : BlockStatus.noOutput;
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            soundLoop.stop();
        }

        protected void refreshTargets() {
            targets.clear();

            if (liquids.get(ventLiquid) <= 0.001f) return;

            int tr = (int) (extinguishRadius / tilesize);
            int tx = tileX();
            int ty = tileY();
            float r2 = extinguishRadius * extinguishRadius;

            for (int dx = -tr; dx <= tr; dx++) {
                for (int dy = -tr; dy <= tr; dy++) {
                    Tile other = world.tile(tx + dx, ty + dy);

                    if (other == null) continue;
                    if (Mathf.dst2(x, y, other.worldx(), other.worldy()) > r2) continue;
                    if (Fires.has(other.x, other.y)) targets.add(other);
                }
            }
        }

        @Override
        public boolean shouldConsume() {
            return enabled && extinguishing;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x, y, extinguishRadius, Color.white);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(
                x * tilesize + offset,
                y * tilesize + offset,
                extinguishRadius,
                Color.white
        );
    }
}