package infernoadvanced.content.planets.infernos;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import infernoadvanced.content.InfernoPlanets;
import mindustry.ai.Astar;
import mindustry.ai.BaseRegistry;
import mindustry.content.Blocks;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;

import java.util.Arrays;

import static mindustry.Vars.*;

public class InfernosPlanetGenerator extends PlanetGenerator {
    BaseGenerator basegen = new BaseGenerator();
    float scl = 4f;
    float heightScl = 0.65f;
    float heightPow = 1.8f;
    float lavaOffset = 0.08f;

    /**
     * Фиксированный сид: разлом одинаков на глобусе и во всех секторах, не зависит от сида карты.
     */
    static final int RIFT_SEED = 66613;
    /**
     * Нормаль большого круга, вдоль которого идёт разлом.
     */
    final Vec3 riftNormal = new Vec3(0.38f, 0.78f, 0.50f).nor();
    /**
     * Половина ширины ядра разлома (шлаковые озёра и магмовые камни). Сектор примерно 0.37 в этих единицах.
     */
    float riftCore = 0.07f;
    /**
     * Ширина горячей кромки (хотрок) за ядром.
     */
    float riftHotBand = 0.07f;
    /**
     * Максимальная половина ширины лавы на карте сектора, в клетках.
     */
    int maxLavaHalfWidth = 12;
    /**
     * Какую долю карты может занимать лава (шлак), остальное превращается в магмарок.
     */
    float maxLavaFraction = 0.35f;

    /**
     * Чёрные дюны.
     */
    final Block[] zoneDunes = {Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.darksand, Blocks.basalt,
            Blocks.darksand, Blocks.basalt, Blocks.darksand, Blocks.stone, Blocks.darksand};
    /**
     * Пепельные равнины.
     */
    final Block[] zoneAsh = {Blocks.basalt, Blocks.basalt, Blocks.stone, Blocks.basalt, Blocks.stone,
            Blocks.dacite, Blocks.basalt, Blocks.stone, Blocks.craters, Blocks.stone};
    /**
     * Сланцевые нагорья.
     */
    final Block[] zoneShale = {Blocks.shale, Blocks.shale, Blocks.dacite, Blocks.shale, Blocks.stone,
            Blocks.dacite, Blocks.shale, Blocks.stone, Blocks.basalt, Blocks.shale};
    /**
     * Холодные плато.
     */
    final Block[] zonePlateau = {Blocks.dacite, Blocks.stone, Blocks.dacite, Blocks.stone, Blocks.basalt,
            Blocks.dacite, Blocks.stone, Blocks.basalt, Blocks.dacite, Blocks.stone};

    ObjectMap<Block, Block> dec = ObjectMap.of(
            Blocks.basalt, Blocks.basaltBoulder,
            Blocks.stone, Blocks.boulder,
            Blocks.shale, Blocks.shaleBoulder,
            Blocks.dacite, Blocks.daciteBoulder,
            Blocks.hotrock, Blocks.air,
            Blocks.magmarock, Blocks.air,
            Blocks.craters, Blocks.air
    );

    float water;

    public InfernosPlanetGenerator() {
        water = 0.2f;
    }

    float rawHeight(Vec3 position) {
        float px = position.x * scl;
        float py = position.y * scl;
        float pz = position.z * scl;

        float base = (float) Math.pow(
                Simplex.noise3d(seed, 6, 0.5, 1f / 4f, px, py, pz) * heightScl,
                heightPow
        );

        float detail = Ridged.noise3d(seed + 1, px, py, pz, 2, 35f) * 0.12f;

        return Mathf.clamp(base + detail + lavaOffset);
    }

    @Override
    public float getHeight(Vec3 position) {
        float h = Math.max(rawHeight(position), water);
        float canyon = Mathf.clamp(1f - riftDistance(position) / (riftCore * 2.5f));
        return h * (1f - 0.5f * canyon);
    }

    @Override
    public void getColor(Vec3 position, Color out) {
        Block block = getBlock(position);
        if (block == Blocks.slag) {
            out.set(Color.valueOf("ff5a1a")).a(0.95f);
            return;
        }
        out.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public boolean isEmissive() {
        return true;
    }

    @Override
    public void getEmissiveColor(Vec3 position, Color out) {
        float dst = 999f;
        float captureDst = 999f;
        float lightScl = 0f;

        for (Sector sector : InfernoPlanets.infernos.sectors) {
            if (sector.hasEnemyBase() && !sector.isCaptured()) {
                dst = Math.min(dst, position.dst(sector.tile.v)
                        - (sector.preset != null ? sector.preset.difficulty / 10f * 0.03f - 0.03f : 0f));
            } else if (sector.hasBase()) {
                float cdst = position.dst(sector.tile.v);
                if (cdst < captureDst) {
                    captureDst = cdst;
                    lightScl = sector.info.lightCoverage;
                }
            }
        }

        lightScl = Math.min(lightScl / 50000f, 1.3f);

        Block block = getBlock(position);
        if (block == Blocks.slag || block == Blocks.magmarock || block == Blocks.hotrock) {
            float lnoise = (float) Simplex.noise3d(seed, 1f, 1f, 9f,
                    position.x, position.y + 99f, position.z);
            float strength = block == Blocks.hotrock ? 0.5f : 1f;
            out.set(Color.valueOf("ff5a1a")).mul(0.85f + lnoise * 0.3f).a(strength);
            return;
        }

        if (dst * 0.25f + (float) Simplex.noise3d(seed, 3f, 0.4, 9f,
                position.x, position.y + 370f, position.z) * 0.06f < 0.045f) {
            out.set(Team.crux.color).mul(0.8f).a(1f);
            return;
        }

        if (captureDst * 0.25f + (float) Simplex.noise3d(seed, 3f, 0.4, 9f,
                position.x, position.y + 600f, position.z) * 0.07f < 0.05f * lightScl) {
            out.set(Team.sharded.color).mul(0.7f).a(1f);
            return;
        }

        out.a = 0f;
    }

    /**
     * Расстояние до разлома: единая извилистая линия вдоль большого круга.
     */
    float riftDistance(Vec3 p) {
        float bend = ((float) Simplex.noise3d(RIFT_SEED, 3, 0.5, 1.1, p.x, p.y, p.z) - 0.5f) * 0.6f;
        float ripple = ((float) Simplex.noise3d(RIFT_SEED + 1, 4, 0.55, 4.0, p.x, p.y, p.z) - 0.5f) * 0.06f;
        return Math.abs(p.dot(riftNormal) + bend + ripple);
    }

    Block getBlock(Vec3 position) {
        float height = Mathf.clamp(rawHeight(position) * 1.2f);
        float dist = riftDistance(position);

        float widthNoise = (float) Simplex.noise3d(RIFT_SEED + 2, 2, 0.5, 3.0, position.x, position.y, position.z);
        float core = riftCore * (0.6f + 0.8f * widthNoise);
        float hot = core + riftHotBand * (0.7f + 0.6f * widthNoise);

        if (dist < core) {
            float lake = (float) Simplex.noise3d(RIFT_SEED + 6, 3, 0.55, 7.0, position.x, position.y, position.z);
            return lake > 0.48f ? Blocks.slag : Blocks.magmarock;
        }

        if (dist < hot) {
            float t = (dist - core) / (hot - core);
            float n = (float) Simplex.noise3d(RIFT_SEED + 7, 3, 0.5, 8.0, position.x, position.y, position.z);
            if (t < 0.35f && n > 0.55f) return Blocks.magmarock;
            return n > 0.3f ? Blocks.hotrock : Blocks.basalt;
        }

        float biome = (float) Simplex.noise3d(RIFT_SEED + 4, 3, 0.55, 1.5, position.x, position.y, position.z);
        float sand = (float) Simplex.noise3d(RIFT_SEED + 5, 4, 0.5, 3.5, position.x, position.y, position.z);

        if (height < 0.7f && sand > 0.62f - (biome < 0.3f ? 0.14f : 0f)) {
            return Blocks.darksand;
        }

        Block[] table;
        if (biome < 0.30f) table = zoneDunes;
        else if (biome < 0.58f) table = zoneAsh;
        else if (biome < 0.82f) table = zoneShale;
        else table = zonePlateau;

        int hi = Mathf.clamp((int) (height * table.length), 0, table.length - 1);
        return table[hi];
    }

    @Override
    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if (Ridged.noise3d(seed + 3, position.x, position.y, position.z, 2, 22f) > 0.31f) {
            tile.block = Blocks.air;
        }
    }

    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return (float) Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float) mag;
    }

    @Override
    protected void generate() {
        cells(4);
        distort(10f, 12f);

        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 4);

        class Room {
            int x, y, radius;
            ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2) {
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 7);
                brush(pathfind(x1, y1, x2, y2,
                        tile -> (tile.solid() ? 50f : 0f)
                                + noise(tile.x, tile.y, 2, 0.4, 1f / nscl) * 500f,
                        Astar.manhattan), stroke);
            }

            void connect(Room to) {
                if (connected.add(to) && to != this) {
                    Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                    midpoint.add(Tmp.v2.setToRandomDirection(rand)
                            .scl(Tmp.v1.dst(x, y)));
                    midpoint.sub(width / 2f, height / 2f)
                            .limit(width / 2f / Mathf.sqrt3)
                            .add(width / 2f, height / 2f);
                    int mx = (int) midpoint.x;
                    int my = (int) midpoint.y;
                    join(x, y, mx, my);
                    join(mx, my, to.x, to.y);
                }
            }
        }

        Seq<Room> roomseq = new Seq<>();

        for (int i = 0; i < rooms; i++) {
            Tmp.v1.trns(rand.random(360f), rand.random(radius / 1.3f));
            float rx = width / 2f + Tmp.v1.x;
            float ry = height / 2f + Tmp.v1.y;
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int) rx, (int) ry, (int) rrad));
        }

        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int) (sector.threat * 4f), 1));
        int offset = rand.nextInt(360);
        float length = width / 2.55f - rand.random(13, 23);
        int waterCheckRad = 5;

        for (int i = 0; i < 360; i += 5) {
            int angle = offset + i;
            int cx = (int) (width / 2f + Angles.trnsx(angle, length));
            int cy = (int) (height / 2f + Angles.trnsy(angle, length));
            int lavaTiles = 0;

            for (int rx = -waterCheckRad; rx <= waterCheckRad; rx++) {
                for (int ry = -waterCheckRad; ry <= waterCheckRad; ry++) {
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if (tile == null || tile.floor().liquidDrop != null) {
                        lavaTiles++;
                    }
                }
            }

            if (lavaTiles <= 4 || i + 5 >= 360) {
                roomseq.add(spawn = new Room(cx, cy, rand.random(8, 14)));

                for (int j = 0; j < enemySpawns; j++) {
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - width / 2f, cy - height / 2f)
                            .rotate(180f + enemyOffset)
                            .add(width / 2f, height / 2f);
                    Room espawn = new Room((int) Tmp.v1.x, (int) Tmp.v1.y, rand.random(8, 14));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }
                break;
            }
        }

        for (Room room : roomseq) {
            erase(room.x, room.y, room.radius);
        }

        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 2);
        for (int i = 0; i < connections; i++) {
            roomseq.random(rand).connect(roomseq.random(rand));
        }
        for (Room room : roomseq) {
            spawn.connect(room);
        }

        cells(1);
        distort(10f, 6f);

        limitLava(maxLavaHalfWidth, maxLavaFraction);
        lavaRim();

        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
        float poles = Math.abs(sector.tile.v.y);

        if (Simplex.noise3d(seed, 2, 0.5, 1f, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z) * 0.5f + poles > 0.25f * 1.3f) {
            ores.add(Blocks.oreCoal);
        }
        if (Simplex.noise3d(seed, 2, 0.5, 1f, sector.tile.v.x + 1f, sector.tile.v.y, sector.tile.v.z) * 0.5f + poles > 0.5f * 1.3f) {
            ores.add(Blocks.oreTitanium);
        }
        if (Simplex.noise3d(seed, 2, 0.5, 1f, sector.tile.v.x + 2f, sector.tile.v.y, sector.tile.v.z) * 0.5f + poles > 0.7f * 1.3f) {
            ores.add(Blocks.oreThorium);
        }

        pass((x, y) -> {
            if (!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for (int i = ores.size - 1; i >= 0; i--) {
                Block entry = ores.get(i);
                if (Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 2, 0.7, 40 + i * 2)) > 0.22f + i * 0.01
                        && Math.abs(0.5f - noise(offsetX, offsetY - i * 999, 1, 1, 30 + i * 4)) > 0.37f + poles * 0.04f) {
                    ore = entry;
                    break;
                }
            }
        });

        trimDark();
        median(2);
        inverseFloodFill(tiles.getn(spawn.x, spawn.y));

        pass((x, y) -> {
            if (floor.asFloor().hasSurface() && block == Blocks.air && rand.chance(0.01)) {
                block = dec.get(floor, floor.asFloor().decoration);
            }
        });

        float difficulty = sector.threat;
        int ruinCount = rand.random(-2, 3);
        if (ruinCount > 0) {
            IntSeq ints = new IntSeq(width * height / 4);
            int padding = 25;

            for (int x = padding; x < width - padding; x++) {
                for (int y = padding; y < height - padding; y++) {
                    Tile tile = tiles.getn(x, y);
                    if (!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)) {
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);
            int placed = 0;

            for (int i = 0; i < ints.size && placed < ruinCount; i++) {
                int val = ints.items[i];
                int x = Point2.x(val);
                int y = Point2.y(val);
                if (!Mathf.within(x, y, spawn.x, spawn.y, 18f)) {
                    float range = difficulty + rand.random(0.4f);
                    Tile tile = tiles.getn(x, y);
                    BaseRegistry.BasePart part = null;
                    if (tile.overlay().itemDrop != null) {
                        part = bases.forResource(tile.drop()).getFrac(range);
                    } else if (tile.floor().liquidDrop != null && rand.chance(0.05f)) {
                        part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                    } else if (rand.chance(0.05f)) {
                        part = bases.parts.getFrac(range);
                    }

                    if (part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, rand, null)) {
                        placed++;
                    }
                }
            }
        }

        for (Tile tile : tiles) {
            if (tile.overlay().needsSurface && !tile.floor().hasSurface()) {
                tile.setOverlay(Blocks.air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for (Room espawn : enemies) {
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if (sector.hasEnemyBase()) {
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)),
                    tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);
            state.rules.attackMode = sector.info.attack = true;
        } else {
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int) Math.max(difficulty * 10f, 1f);
        }

        state.rules.waveSpacing = Mathf.lerp(7800f, 3600f, Math.max(difficulty - 0.4f, 0f));
        state.rules.waves = true;
        state.rules.env = sector.planet.defaultEnv;
        state.rules.spawns = Waves.generate(difficulty, new Rand(sector.id),
                state.rules.attackMode,
                state.rules.attackMode && spawner.countGroundSpawns() == 0,
                false);
    }

    /**
     * Ограничивает лаву на карте. От суши по сетке считается глубина каждой клетки лавы.
     * Оставляем только прибрежную полосу не шире maxHalf клеток, причём такую, чтобы лавы
     * было не больше maxFraction карты. Остальное становится магмароком.
     */

    protected void limitLava(int maxHalf, float maxFraction) {
        int w = width, h = height;
        int[] dist = new int[w * h];
        Arrays.fill(dist, -1);
        IntSeq queue = new IntSeq(w * h / 4);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (!tiles.getn(x, y).floor().isLiquid) {
                    dist[x + y * w] = 0;
                    queue.add(x + y * w);
                }
            }
        }

        for (int head = 0; head < queue.size; head++) {
            int pos = queue.items[head];
            int px = pos % w, py = pos / w;
            for (Point2 dir : Geometry.d4) {
                int nx = px + dir.x, ny = py + dir.y;
                if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue;
                int npos = nx + ny * w;
                if (dist[npos] != -1) continue;
                dist[npos] = dist[pos] + 1;
                queue.add(npos);
            }
        }

        int[] hist = new int[maxHalf + 1];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int d = dist[x + y * w];
                if (tiles.getn(x, y).floor().isLiquid && d >= 1 && d <= maxHalf) hist[d]++;
            }
        }

        int limit = 1, sum = 0;
        for (int d = 1; d <= maxHalf; d++) {
            sum += hist[d];
            if (sum > maxFraction * w * h) break;
            limit = d;
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Tile t = tiles.getn(x, y);
                int d = dist[x + y * w];
                if (t.floor().isLiquid && (d == -1 || d > limit)) {
                    t.setFloor(Blocks.magmarock.asFloor());
                }
            }
        }
    }

    /**
     * Раскалённая кромка: порода в двух клетках от лавы становится хотрок.
     */
    protected void lavaRim() {
        pass((x, y) -> {
            if (floor.asFloor().isLiquid) return;

            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    Tile t = tiles.get(x + dx, y + dy);
                    if (t != null && t.floor() == Blocks.slag) {
                        floor = Blocks.hotrock;
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void postGenerate(Tiles tiles) {
        if (sector.hasEnemyBase()) {
            basegen.postGenerate();
            if (spawner.countGroundSpawns() == 0) {
                state.rules.spawns = Waves.generate(sector.threat, new Rand(sector.id),
                        state.rules.attackMode, true, false);
            }
        }
    }

    @Override
    public void addWeather(Sector sector, mindustry.game.Rules rules) {
    }
}