package infernoadvanced.content;

import arc.graphics.Color;
import arc.math.Rand;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import infernoadvanced.content.planets.RingAsteroid;
import infernoadvanced.content.planets.events.EruptionMesh;
import infernoadvanced.content.planets.events.SolarActivity;
import infernoadvanced.content.planets.events.SolarStorm;
import infernoadvanced.content.planets.infernos.InfernosPlanet;
import infernoadvanced.content.planets.infernos.InfernosPlanetGenerator;
import mindustry.content.Planets;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

public class InfernoPlanets {
    public static Planet infernoStar, infernos;
    public static Planet[] asteroids;

    private static final boolean SEPARATE_SYSTEM = true;
    private static final int SECTOR_DETAIL = 2;

    //TODO tune: 6 = vanilla max (~0.5M verts per full layer); drop to 5 if FPS suffers
    private static final int SURFACE_DIV = 6, CLOUD_DIV = 6;

    private static final int RING_COUNT = 26;
    private static final long RING_SEED = 66613L;
    private static final float RING_RADIUS = 1.05f, RING_TILT = 27f, RING_PERIOD = 60f * 16f;

    private static final Color
            STAR_CORE = Color.valueOf("ff4a1f"),
            STAR_MID = Color.valueOf("ff6b3d"),
            STAR_EDGE = Color.valueOf("ffa070"),
            STAR_LIGHT = Color.valueOf("ff6b4d"),
            ROCK_DARK = Color.valueOf("2a1f1a"),
            ROCK_LIGHT = Color.valueOf("5a4a3a");

    public static void load() {
        loadStar();
        loadInfernos();
        loadRing();
        SolarStorm.load();
        //        Log.info("[IA] planets: star=@ infernos=@ ring=@", infernoStar, infernos, asteroids.length);
        //        Events.on(EventType.ClientLoadEvent.class, e -> Vars.content.planets().each(p ->
        //                Log.info("[IA] planet @ accessible=@ system=@", p.name, p.accessible, p.solarSystem)));
        InfernoTestContent.assignPlanet(infernos);
    }

    private static void loadStar() {
        infernoStar = new Planet("inferno-star", SEPARATE_SYSTEM ? null : Planets.sun, 2.8f) {{
            localizedName = "Vesper";
            description = "Красный карлик класса M. Его слабый свет окрашивает систему в багровые тона.";

            accessible = false;
            visible = true;
            bloom = true;
            hasAtmosphere = false;
            iconColor = STAR_CORE;
            lightColor = STAR_LIGHT;
            rotateTime = 60f * 60f * 8f;
            clipRadius = SolarActivity.REACH + 0.5f;

            if (!SEPARATE_SYSTEM) {
                orbitRadius = 45f;
                orbitTime = 60f * 60f * 40f;
                drawOrbit = false;
            }

            meshLoader = () -> new MultiMesh(
                    new SunMesh(
                            this, 4, 5, 0.3, 1.7, 1.2, 1, 1.1f,
                            STAR_CORE, STAR_CORE, STAR_MID, STAR_MID, STAR_EDGE, STAR_EDGE
                    ),
                    new EruptionMesh(this)
            );

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.10f, 0.05f, CLOUD_DIV,
                            Color.valueOf("c2571f").a(0.55f), 3, 0.5f, 1.4f, 0.50f),
                    new HexSkyMesh(this, 17, 0.20f, 0.12f, CLOUD_DIV,
                            Color.valueOf(String.valueOf(STAR_LIGHT)).a(0.75f), 3, 0.5f, 1.2f, 0.52f),
                    new HexSkyMesh(this, 23, 0.34f, 0.20f, CLOUD_DIV - 1,
                            Color.valueOf(String.valueOf(STAR_CORE)).a(0.5f), 2, 0.45f, 1.0f, 0.47f)
            );
        }};
    }

    private static void loadInfernos() {
        infernos = new InfernosPlanet("infernos", infernoStar, 0.45f, SECTOR_DETAIL) {{
            localizedName = "Infernos";
            description = "Горячая малая планета у самой звезды: базальтовые пики над огненными равнинами, " +
                    "реки лавы и плотный дым, почти не пропускающий свет.";

            alwaysUnlocked = true;
            accessible = true;
            visible = true;
            generator = new InfernosPlanetGenerator();
            startSector = 1;
            sectorSeed = 66613;

            orbitRadius = 8f;
            orbitTime = 60f * 60f * 3f;
//          rotateTime = orbitTime;
            tidalLock = true;
            minZoom = 1f;
            maxZoom = 3f;
            drawOrbit = true;
            updateLighting = true;
            bloom = false;

            generator = new InfernosPlanetGenerator() {{
                defaultLoadout = InfernoTestContent.loadout();
            }};
            defaultCore = InfernoTestContent.testCore;
            unlockedOnLand.add(InfernoTestContent.testCore);

            hasAtmosphere = true;
            atmosphereRadIn = ((InfernosPlanet) this).haloRadIn;
            atmosphereRadOut = ((InfernosPlanet) this).haloRadOut;
            atmosphereColor = ((InfernosPlanet) this).haloColor;
            iconColor = Color.valueOf("ff3300");
            landCloudColor = Color.valueOf("5a2a1a").a(0.5f);

            allowLaunchSchematics = true;
            allowLaunchLoadout = true;
            allowLaunchToNumbered = false;
            allowSectorInvasion = true;
            allowWaves = true;
            clearSectorOnLose = true;
            prebuildBase = false;

            defaultEnv = Env.terrestrial | Env.scorching;
            defaultAttributes.set(Attribute.heat, 1.2f);

            ruleSetter = r -> {
                r.waveTeam = InfernoTeams.cinis;
                r.placeRangeCheck = false;
                r.hideSpawns = false;
                r.fog = false;
                r.staticFog = false;
                r.lighting = true;
                r.coreDestroyClear = true;
                r.ambientLight = Color.valueOf("ff6b4d").a(0.1f);
            };

            meshLoader = () -> new mindustry.graphics.g3d.HexMesh(this, 6);

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.10f, 0.05f, CLOUD_DIV,
                            Color.valueOf("c2571f").a(0.55f), 3, 0.5f, 1.4f, 0.50f),
                    new HexSkyMesh(this, 17, 0.20f, 0.12f, CLOUD_DIV,
                            Color.valueOf("232326").a(0.75f), 3, 0.5f, 1.2f, 0.52f),
                    new HexSkyMesh(this, 23, 0.34f, 0.20f, CLOUD_DIV - 1,
                            Color.valueOf("3a3a40").a(0.5f), 2, 0.45f, 1.0f, 0.47f)
            );
        }};
    }

    private static void loadRing() {
        asteroids = new Planet[RING_COUNT];
        Rand rand = new Rand(RING_SEED);

        for (int i = 0; i < RING_COUNT; i++) {
            int seed = (int) (RING_SEED + i * 137L);
            float size = rand.random(0.02f, 0.05f);
            float orbit = RING_RADIUS + rand.random(-0.07f, 0.07f);
            float tilt = RING_TILT + rand.random(-1.6f, 1.6f);
            float offset = 360f / RING_COUNT * i + rand.random(-2.5f, 2.5f);
            float spin = 60f * rand.random(6f, 20f);

            asteroids[i] = new RingAsteroid("infernos-ring-" + i, infernos, size, tilt) {{
                localizedName = "Обломок Infernos";
                orbitRadius = orbit;
                orbitTime = RING_PERIOD;
                orbitOffset = offset;
                rotateTime = spin;
                iconColor = ROCK_LIGHT;
                meshLoader = () -> rockMesh(this, seed, radius);
            }};
        }
    }

    private static GenericMesh rockMesh(Planet planet, int seed, float radius) {
        Rand rand = new Rand(seed);
        Seq<GenericMesh> meshes = new Seq<>();

        meshes.add(new NoiseMesh(planet, seed, 2, radius,
                2, 0.55f, 0.45f, rand.random(9f, 15f),
                ROCK_DARK, ROCK_LIGHT, 3, 0.6f, 0.38f, rand.random(0.4f, 0.6f)));

        int pieces = rand.random(0, 2);
        for (int j = 0; j < pieces; j++) {
            meshes.add(new MatMesh(
                    new NoiseMesh(planet, seed + j + 1, 1, radius * rand.random(0.25f, 0.5f),
                            2, 0.6f, 0.38f, rand.random(12f, 20f),
                            ROCK_DARK, ROCK_LIGHT, 3, 0.6f, 0.38f, 0.5f),
                    new Mat3D().setToTranslation(new Vec3().setToRandomDirection(rand).setLength(radius * rand.random(1.6f, 2.6f)))
            ));
        }

        return meshes.size == 1 ? meshes.first() : new MultiMesh(meshes.toArray(GenericMesh.class));
    }

    private static NoiseMesh layer(Planet planet, int seed, int divisions, float radius,
                                   int octaves, float persistence, float scale, float relief,
                                   String color1, String color2,
                                   int colorOct, float colorPersistence, float colorScale, float colorThreshold) {
        return new NoiseMesh(planet, seed, divisions, radius,
                octaves, persistence, scale, relief,
                Color.valueOf(color1), Color.valueOf(color2),
                colorOct, colorPersistence, colorScale, colorThreshold);
    }


}