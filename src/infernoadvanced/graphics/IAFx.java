package infernoadvanced.graphics;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class IAFx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    /*
    TODO better effects, like mindustry overhaul
    TODO Switch from debug mode
     */

    // ==========================================
    // 1. ГЕНЕРАТОРЫ (Фабрики эффектов)
    // Методы, которые создают эффекты с параметрами.
    // ==========================================
    public static class Generators {

        /**
         * Дымовой шлейф позади ракеты.
         *
         * @param backOffset смещение точки вылета частиц вдоль направления, противоположного движению
         * @param cone       полуугол конуса разброса частиц, в градусах
         * @param distance   дальность (длина) шлейфа дыма
         */
        public static Effect shootSmokeMissileBack(float backOffset, float cone, float distance) {
            return shootSmokeMissileBack(backOffset, cone, distance, Pal.redLight, null, 0.5f);
        }

        public static Effect shootSmokeMissileBack(float backOffset, float cone, float distance, Color from, @Nullable Color to, float smokeAlpha) {
            return missileSmoke(backOffset, cone, distance, 9f, from, to, smokeAlpha).baseRotation(180f);
        }

        public static Effect shootSmokeMissileFront(Color from, @Nullable Color to, float smokeAlpha) {
            return missileSmoke(0f, 21f, 90f, 9f, from, to, smokeAlpha);
        }

        public static Effect shootSmokeMissileFront(Color smokeColor, float smokeAlpha) {
            return shootSmokeMissileFront(smokeColor, null, smokeAlpha);
        }

        public static Effect shootSmokeMissileFront(float smokeAlpha) {
            return shootSmokeMissileFront(Pal.redLight, null, smokeAlpha);
        }

        private static Effect missileSmoke(float offset, float cone, float distance, float size, Color from, @Nullable Color to, float smokeAlpha) {
            float lifetime = distance * 1.44f;
            float clip = (offset + distance + size) * 2f;
            int particles = Math.max(6, (int) (distance * 0.4f));

            return new Effect(lifetime, clip, e -> {
                rand.setSeed(e.id);
                if (to == null) color(from);

                float ox = Angles.trnsx(e.rotation, offset), oy = Angles.trnsy(e.rotation, offset);

                randLenVectors(e.id, particles, e.finpow() * distance, e.rotation, cone, (x, y) -> {
                    float life = e.lifetime * rand.random(0.2f, 1f);
                    float scl = rand.random(0.6f, 1f);

                    e.scaled(life, b -> {
                        if (to != null) color(from, to, b.fin());
                        alpha(smokeAlpha * b.fslope());
                        Fill.circle(e.x + x + ox, e.y + y + oy, b.fout() * size * scl + 0.3f);
                    });
                });
            }).followParent(false);
        }


        public static Effect smokeAoeCloud(float radius, @Nullable Color smokeColor) {
            return new Effect(radius * 2f, radius * 2f + 70f, e -> {
                color(smokeColor == null ? e.color : smokeColor, 0.65f);
                Angles.randLenVectors(e.id, (int) (radius * radius / 100f), radius, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, radius / 15f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                });
            });
        }

        public static Effect detonation(Color color, Color smokeColor, float radius, int shards, float lifetime) {
            float flashTime = 10f, waveTime = 22f, shardTime = 40f;
            return new Effect(lifetime, radius * 3f, e -> {
                float fp = e.time / flashTime;
                if (fp < 1f) {
                    Draw.color(Color.white, color, fp);
                    Draw.blend(Blending.additive);
                    Fill.circle(e.x, e.y, radius * 0.5f * (1f - fp));
                    Draw.blend();
                }

                float wp = (e.time - 4f) / waveTime;
                if (wp > 0f && wp < 1f) {
                    Draw.color(color);
                    stroke((1f - wp) * radius * 0.12f);
                    Lines.circle(e.x, e.y, Interp.pow2Out.apply(wp) * radius);
                }

                float sp = (e.time - 6f) / shardTime;
                if (sp > 0f && sp < 1f) {
                    Draw.color(color);
                    stroke((1f - sp) * 2.5f);
                    Angles.randLenVectors(e.id, shards, Interp.pow2Out.apply(sp) * radius * 1.2f, (x, y) -> {
                        Lines.lineAngle(e.x + x, e.y + y, Angles.angle(x, y), (1f - sp) * radius * 0.15f);
                    });
                }

                float mp = (e.time - 8f) / (lifetime - 8f);
                if (mp > 0f && mp < 1f) {
                    Draw.color(smokeColor, (1f - mp) * 0.5f);
                    Angles.randLenVectors(e.id + 1, shards * 2, Interp.pow2Out.apply(mp) * radius * 0.9f, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, radius * 0.14f * (0.4f + mp * 0.6f));
                    });
                }
                // Draw.reset() убран: Effect.render() сам сбрасывает состояние до и после рендерера.
                // Blending парами blend()/blend() оставляем — Draw.reset() его не сбрасывает.
            });
        }
    }

    // ==========================================
    // 2. ТУРЕЛИ И СНАРЯДЫ
    // Эффекты зарядки, выстрелов и попаданий турелей.
    // ==========================================
    public static class Turrets {

        public static final Effect shootSmokeHail = Generators.shootSmokeMissileBack(35f, 21f, 90f, Color.blue, Color.green, 0.5f);
        public static final Effect shootSmokeDownpour = Generators.shootSmokeMissileBack(30f, 18f, 50f, Color.blue, Color.green, 0.5f);

        public static final Effect barium137mLaserCharge = new Effect(80f, 210f, e -> {
            color(IAPal.barium137mAmmoFront);
            stroke(e.fin() * 2f);
            Lines.circle(e.x, e.y, 4f + e.fout() * 100f);

            Fill.circle(e.x, e.y, e.fin() * 20);

            randLenVectors(e.id, 20, 40f * e.fout(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fin() * 5f);
                Drawf.light(e.x + x, e.y + y, e.fin() * 15f, IAPal.barium137mAmmoFront, 0.7f);
            });

            color();

            Fill.circle(e.x, e.y, e.fin() * 10);
            Drawf.light(e.x, e.y, e.fin() * 20f, IAPal.barium137mAmmoFront, 0.7f);
        }).followParent(true).rotWithParent(true);


        public static final Effect singularityUnfold = new Effect(45f, 160f, e -> {
            float phase1 = Mathf.clamp(e.fin() / 0.3f);
            float phase2 = Mathf.clamp((e.fin() - 0.3f) / 0.7f);

            if (phase1 < 1f) {
                color(IAPal.blackHoleArc, IAPal.blackHoleGlow, phase1);
                stroke(3f * (1f - phase1));
                rand.setSeed(e.id);
                for (int i = 0; i < 12; i++) {
                    float angle = rand.random(360f);
                    float len = 100f * (1f - phase1) + 20f;
                    float cx = e.x + Angles.trnsx(angle, len * phase1);
                    float cy = e.y + Angles.trnsy(angle, len * phase1);
                    Lines.lineAngle(cx, cy, angle + 180f, 20f * (1f - phase1));
                }
            }

            if (phase2 > 0f) {
                color(IAPal.blackHoleCore);
                blend(Blending.additive);
                Fill.circle(e.x, e.y, 40f * (1f - phase2) * Interp.pow2Out.apply(phase2));
                blend();

                color(IAPal.blackHoleOuterRing);
                stroke(4f * (1f - phase2));
                Lines.circle(e.x, e.y, 80f * Interp.pow3Out.apply(phase2));
            }
        }).layer(Layer.effect);

        public static final Effect singularityCollapse = new Effect(35f, 250f, e -> {
            float phase1 = Mathf.clamp(e.fin() / 0.7f);
            float phase2 = Mathf.clamp((e.fin() - 0.7f) / 0.3f);

            if (phase1 < 1f) {
                float collapse = 1f - phase1;
                color(IAPal.blackHoleOuterRing, IAPal.blackHoleCore, phase1);
                stroke(3f * collapse);
                Lines.circle(e.x, e.y, 80f * collapse);

                color(IAPal.blackHoleArc);
                rand.setSeed(e.id);
                for (int i = 0; i < 16; i++) {
                    float angle = rand.random(360f);
                    float len = 100f * collapse;
                    float px = e.x + Angles.trnsx(angle, len);
                    float py = e.y + Angles.trnsy(angle, len);
                    Fill.circle(px, py, 4f * collapse);
                }
            }

            if (phase2 > 0f) {
                color(Color.white, IAPal.blackHoleGlow, phase2);
                blend(Blending.additive);
                Fill.circle(e.x, e.y, 50f * (1f - phase2));
                blend();

                color(IAPal.blackHoleInnerRing);
                stroke(5f * (1f - phase2));
                Lines.circle(e.x, e.y, 120f * Interp.pow2Out.apply(phase2));

                rand.setSeed(e.id + 1);
                stroke(2f * (1f - phase2));
                for (int i = 0; i < 8; i++) {
                    float angle = rand.random(360f);
                    float len = 60f * Interp.pow2Out.apply(phase2);
                    Lines.lineAngle(e.x, e.y, angle, len);
                }
            }
        }).layer(Layer.effect);

        public static final Effect singularityAccretion = new Effect(40f, 250f, e -> {
            rand.setSeed(e.id);
            float startDist = 80f + rand.random(40f);
            float angle = rand.random(360f);

            float dist = startDist * e.fout();

            float px = e.x + Angles.trnsx(angle, dist);
            float py = e.y + Angles.trnsy(angle, dist);

            color(IAPal.blackHoleArc, IAPal.blackHoleCore, e.fin());
            blend(Blending.additive);
            Fill.circle(px, py, 3f * e.fout() + 1f);
            Drawf.light(px, py, 15f, IAPal.blackHoleArc, 0.5f * e.fout());
            blend();

            stroke(1.5f * e.fout());
            // Хвост теперь направлен наружу (против движения к центру).
            Lines.lineAngle(px, py, angle, 10f * e.fout());
        }).layer(Layer.effect);
    }

    // ==========================================
    // 3. ВЗРЫВЫ И РАЗРУШЕНИЯ
    // Эпичные взрывы, детонации, коллапсы.
    // ==========================================
    public static class Explosions {

        public static final Effect mineBlast = Generators.detonation(Pal.blastAmmoBack, Pal.lightishGray, 34f, 10, 60f);
        public static final Effect stormBlast = Generators.detonation(Pal.blastAmmoBack, Pal.lightishGray, 20f, 16, 90f);
        public static final Effect hailBlast = Generators.detonation(Pal.blastAmmoBack, Pal.lightishGray, 45f, 14, 75f);
        public static final Effect reactorBlast = Generators.detonation(IAPal.strontiumAmmoBack, Pal.lightishGray, 95f, 20, 120f);
        public static final Effect reactorFragBlast = Generators.detonation(IAPal.strontiumAmmoBack, Pal.lightishGray, 70f, 14, 100f);

        public static final Effect strontiumReactorExplosion = new Effect(30f, 500f, b -> {
            float intensity = 6.8f;
            float baseLifetime = 25f + intensity * 11f;
            b.lifetime = 50f + intensity * 65f;

            color(IAPal.strontiumGlow);
            alpha(0.7f);
            for (int i = 0; i < 4; i++) {
                rand.setSeed(b.id * 2 + i);
                float lenScl = rand.random(0.4f, 1f);
                int fi = i;
                b.scaled(b.lifetime * lenScl, e -> {
                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int) (2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                        float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                        float rad = fout * ((2f + intensity) * 2.35f);
                        Fill.circle(e.x + x, e.y + y, rad);
                        Drawf.light(e.x + x, e.y + y, rad * 2.5f, IAPal.strontiumGlow, 0.5f);
                    });
                });
            }

            b.scaled(baseLifetime, e -> {
                color(Color.white, IAPal.strontiumGlow, e.fin());
                stroke(3f * e.fout());
                Lines.circle(e.x, e.y, 6f + e.fin() * 60f);
                randLenVectors(e.id, 12, e.finpow() * 45f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 4f);
                });
                Drawf.light(e.x, e.y, 120f, IAPal.strontiumGlow, 0.9f * e.fout());
            });
        });
    }

    // ==========================================
    // 4. ДЫМ, ЧАСТИЦЫ И СЛЕДЫ
    // Облака дыма, реактивные следы, общие частицы.
    // ==========================================
    public static class Particles {

        public static final Effect hailSmokeCloud = Generators.smokeAoeCloud(50f, Pal.blastAmmoBack);

        public static final Effect reactorSmokeStrontium = new Effect(17f, e -> {
            randLenVectors(e.id, 4, e.fin() * 8f, (x, y) -> {
                color(Pal.lightishGray, IAPal.strontiumGlow, e.fin() * 0.5f);
                Fill.circle(e.x + x, e.y + y, (1f + e.fout() * 5f) / 2f);
            });
        });

        public static final Effect co2VentSmoke = new Effect(60f, 150f, e -> {
            float mp = (e.time - 8f) / (e.lifetime - 8f);
            if (mp > 0f && mp < 1f) {
                Draw.color(Color.gray, (1f - mp) * 0.5f);
                Angles.randLenVectors(e.id + 1, 8, Interp.pow2Out.apply(mp) * e.rotation * 0.9f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.rotation * 0.14f * (0.4f + mp * 0.6f));
                });
            }
        });

        public static final Effect gravityCollapse = new Effect(30f, e -> {
            float radius = 14f;

            color(IAPal.blackHoleArc);
            for (int i = 0; i < 1; i++) {
                float angle = e.rotation + i * 120f + Mathf.randomSeedRange(e.id + i, 20f);
                float startR = radius * (0.6f + 0.4f * Mathf.randomSeed(e.id + i));
                float r = startR * (1f - e.finpow());
                float x = e.x + Angles.trnsx(angle, r);
                float y = e.y + Angles.trnsy(angle, r);
                Fill.circle(x, y, 1.6f * e.fout() + 0.3f);
            }

            color(IAPal.blackHoleArc, 0.35f * e.fout());
            Fill.circle(e.x, e.y, 3f + 2f * e.fin());

            color(IAPal.blackHoleArc, 0.5f * e.fout());
            stroke(0.8f * e.fout());
            Lines.circle(e.x, e.y, radius * (1f - e.fin()) + 2f);
        });
    }

    public static class Radiation {
        private static final Color[] strongColors = {
                Color.valueOf("3a3a3a"),
                Color.valueOf("c82020"),
                Color.valueOf("6b0000"),
                Color.valueOf("a83800")
        };

        public static final Effect
                weak = new Effect(30f, e -> {
            color(Color.gray, Pal.darkishGray, e.fin());
            randLenVectors(e.id, 6, 4f + 30f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 3f);
                Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
            });
        }),
                strong = new Effect(40f, e -> {
                    color(Color.gray, Color.lightGray, e.fin());
                    randLenVectors(e.id, 3, 1f + e.fin() * 4f, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 0.5f + e.fout() * 1.5f);
                    });

                    Draw.blend(Blending.additive);
                    for (int i = 0; i < 4; i++) {
                        float angle = Mathf.randomSeed(e.id + i, 360f);
                        float dist = Mathf.randomSeed(e.id + i + 100, 4f, 14f) * e.finpow();
                        Tmp.v1.trns(angle, dist);

                        int colorIndex = Mathf.randomSeed(e.id + i + 200, 0, 3);
                        color(strongColors[colorIndex], 0.7f * e.fout());
                        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 2f * e.finpow() + 1f);
                    }
                    Draw.blend();
                }).layer(Layer.effect);
    }

    // ==========================================
    // 5. БЛОКИ И ОКРУЖЕНИЕ
    // Эффекты для крафта, вентиляции, нефтяных вышек и т.д.
    // ==========================================
    public static class Blocks {

        public static final Effect wasteBurnerSmoke = new Effect(35f, 60f, e -> {
            float mp = e.time / e.lifetime;
            if (mp > 0f && mp < 1f) {
                Draw.color(Color.gray, Color.darkGray, mp);
                Draw.alpha((1f - mp) * 0.4f);
                Angles.randLenVectors(e.id + 1, 2, Interp.pow2Out.apply(mp) * 8f, (x, y) -> {
                    Fill.circle(e.x + x * 0.3f, e.y + Math.abs(y) + mp * 18f, 3f * (0.6f + mp * 0.4f));
                });
            }
        });

        public static final Effect oilRigFlame = new Effect(80f, e -> {
            float mp = (e.time - 8f) / (e.lifetime - 8f);
            if (mp > 0f && mp < 1f) {
                float efficiency = e.rotation > 0 ? e.rotation : 1f;

                Draw.color(Pal.lightFlame, Pal.darkFlame, mp);
                Angles.randLenVectors(e.id + 1, 8, Interp.pow2Out.apply(mp) * 8f, (x, y) -> {
                    Fill.circle(e.x + x * 0.2f, e.y + Math.abs(y) + mp * 15f, 2f * (0.4f + mp * 0.6f) * efficiency);
                });
                Drawf.light(e.x, e.y + mp * 15f, 25f * efficiency, Pal.lightFlame, 0.6f * (1f - mp) * efficiency);
            }
        });

        public static final Effect oilRigSmoke = new Effect(120f, 90f, e -> {
            float mp = (e.time - 8f) / (e.lifetime - 8f);
            if (mp > 0f && mp < 1f) {
                Draw.color(Color.gray, Color.darkGray, mp);
                Draw.alpha((1f - mp) * 0.35f);
                Angles.randLenVectors(e.id + 2, 4, Interp.pow2Out.apply(mp) * 12f, (x, y) -> {
                    Fill.circle(e.x + x * 0.3f, e.y + Math.abs(y) * 1.5f + mp * 30f, 3f * (0.5f + mp * 0.5f));
                });
            }
        });
    }
}