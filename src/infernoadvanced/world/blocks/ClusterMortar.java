//package infernoadvanced.world.blocks;
//
//import arc.graphics.Color;
//import arc.math.Mathf;
//import arc.util.Time;
//import mindustry.content.Fx;
//import mindustry.content.Items;
//import mindustry.content.StatusEffects;
//import mindustry.entities.Effect;
//import mindustry.entities.bullet.BasicBulletType;
//import mindustry.entities.bullet.BombBulletType;
//import mindustry.entities.bullet.BulletType;
//import mindustry.entities.effect.MultiEffect;
//import mindustry.gen.Sounds;
//import mindustry.graphics.Pal;
//import mindustry.type.ItemStack;
//import mindustry.world.blocks.defense.turrets.ItemTurret;
//import mindustry.world.draw.DrawTurret;
//import mindustry.world.draw.DrawDefault;
//import mindustry.world.draw.DrawWarmupRegion;
//import mindustry.entities.part.RegionPart;
//import mindustry.entities.part.ShapePart;
//import mindustry.graphics.Layer;
//
//import static mindustry.Vars.renderer;
//
//public class ClusterMortar extends ItemTurret {
//
//    // Настройки разбрасывания бомб
//    public int bombCount = 6;        // количество разбрасываемых бомб
//    public float spreadRadius = 70f; // радиус разбрасывания
//    public float clusterDamage = 45f; // урон каждой бомбы
//    public float clusterRadius = 35f; // радиус взрыва бомбы
//
//    public ClusterMortar(String name) {
//        super(name);
//
//        // Базовые характеристики
//        size = 3;
//        health = 1400;
//        reloadTime = 85f;
//        range = 280f;
//        rotateSpeed = 2.5f;
//        shootCone = 12f;
//        inaccuracy = 8f;
//        hake = 3f;
//        shootSound = Sounds.artillery;
//        targetAir = true;
//        targetGround = true;
//
//        // Потребление
//        consumePower(2.5f);
//        coolant = consumeCoolant(0.2f);
//        coolantMultiplier = 8f;
//
//        // Визуал в стиле твоего мода
//        drawer = new DrawTurret("cluster-") {{
//            Color heatColor = Color.valueOf("ffaa44");
//
//            parts.addAll(
//                    new RegionPart("-base") {{
//                        progress = PartProgress.constant(1f);
//                        under = true;
//                        mirror = false;
//                    }},
//                    new RegionPart("-barrel") {{
//                        progress = PartProgress.recoil;
//                        mirror = false;
//                        moveY = -4f;
//                        heatColor = heatColor;
//                    }},
//                    new RegionPart("-side") {{
//                        progress = PartProgress.warmup;
//                        mirror = true;
//                        x = 8f;
//                        y = -3f;
//                        moveX = 1f;
//                        moveRot = -8f;
//                        heatColor = heatColor;
//                    }},
//                    new ShapePart() {{
//                        progress = PartProgress.warmup;
//                        color = Color.valueOf("ffaa44");
//                        circle = true;
//                        hollow = true;
//                        stroke = 0f;
//                        strokeTo = 3f;
//                        radius = 12f;
//                        layer = Layer.effect;
//                        y = -6f;
//                    }}
//            );
//        }};
//
//        outlineColor = Pal.darkOutline;
//
//        // Боеприпасы
//        ammoTypes = ItemStack.with(
//                Items.blastCompound, new BasicBulletType(3.5f, 35f) {{
//                    splashDamage = 25f;
//                    splashDamageRadius = 20f;
//                    lifetime = 70f;
//                    speed = 3f;
//                    hitEffect = Fx.blastExplosion;
//                    despawnEffect = Fx.blastExplosion;
//                    shootEffect = Fx.artilleryTrailSmoke;
//                    smokeEffect = Fx.shootBigSmoke;
//                    ammoMultiplier = 2;
//                    status = StatusEffects.blasted;
//                    statusDuration = 60f;
//
//                    // Осколки = бомбы которые разлетаются
//                    fragOnHit = true;
//                    fragBullets = 6;
//                    fragRandomSpread = 120f;
//                    fragVelocityMin = 0.6f;
//                    fragVelocityMax = 1.2f;
//                    fragBullet = new BombBulletType(2.5f, 45f) {{
//                        splashDamage = 40f;
//                        splashDamageRadius = 32f;
//                        lifetime = 45f;
//                        hitEffect = new MultiEffect(Fx.blastExplosion, Fx.smokeCloud);
//                        trailColor = Color.valueOf("ffaa44");
//                        trailWidth = 2f;
//                        trailLength = 8;
//                    }};
//                }},
//
//                Items.pyratite, new BasicBulletType(3.8f, 30f) {{
//                    splashDamage = 20f;
//                    splashDamageRadius = 18f;
//                    lifetime = 68f;
//                    speed = 3.2f;
//                    hitEffect = Fx.fireExplosion;
//                    shootEffect = Fx.artilleryShoot;
//                    ammoMultiplier = 2;
//                    status = StatusEffects.burning;
//                    statusDuration = 120f;
//                    makeFire = true;
//
//                    fragOnHit = true;
//                    fragBullets = 5;
//                    fragRandomSpread = 110f;
//                    fragBullet = new BombBulletType(2.2f, 35f) {{
//                        splashDamage = 30f;
//                        splashDamageRadius = 28f;
//                        status = StatusEffects.burning;
//                        statusDuration = 80f;
//                        makeFire = true;
//                        hitEffect = Fx.fireExplosion;
//                    }};
//                }},
//
//                InfernoItems.strontium, new BasicBulletType(3.2f, 40f) {{
//                    splashDamage = 30f;
//                    splashDamageRadius = 22f;
//                    lifetime = 72f;
//                    speed = 2.8f;
//                    hitEffect = Fx.blastExplosion;
//                    shootEffect = Fx.artilleryShoot;
//                    ammoMultiplier = 1;
//                    status = InfernoStatusEffects.radioactive;
//                    statusDuration = 180f;
//
//                    fragOnHit = true;
//                    fragBullets = 4;
//                    fragRandomSpread = 100f;
//                    fragBullet = new BombBulletType(2f, 50f) {{
//                        splashDamage = 45f;
//                        splashDamageRadius = 30f;
//                        status = InfernoStatusEffects.radioactive;
//                        statusDuration = 120f;
//                        hitEffect = new MultiEffect(Fx.blastExplosion, InfernoBulletTypes.strontiumSmallCloud);
//                        trailColor = Color.valueOf("48db79");
//                    }};
//                }}
//        );
//    }
//
//    @Override
//    public void shoot(ClusterMortarBuild tile, float targetX, float targetY) {
//        BulletType ammo = tile.peekAmmo();
//        if (ammo == null) return;
//
//        // Эффект выстрела
//        Effects.shake(shootShake, shootShake, tile);
//        Effects.effect(Fx.artilleryShoot, tile.x, tile.y, tile.rotation);
//
//        // Создаем разбрасываемые бомбы по кругу
//        for (int i = 0; i < bombCount; i++) {
//            float angle = Mathf.random(0f, Mathf.PI * 2f);
//            float radius = Mathf.random(spreadRadius);
//            float bombX = targetX + Mathf.cos(angle) * radius;
//            float bombY = targetY + Mathf.sin(angle) * radius;
//
//            ammo.create(tile, tile.team, bombX, bombY, tile.rotation);
//        }
//
//        // Центральный снаряд
//        ammo.create(tile, tile.team, targetX, targetY, tile.rotation);
//
//        tile.useAmmo();
//        tile.reload = reloadTime;
//    }
//
//    public class ClusterMortarBuild extends ItemTurretBuild {
//        @Override
//        protected void shoot(boolean b) {
//            if (target != null && hasAmmo()) {
//                float targetX = target.aimX(this);
//                float targetY = target.aimY(this);
//                ClusterMortar.this.shoot(this, targetX, targetY);
//            }
//        }
//
//        @Override
//        public void draw() {
//            super.draw();
//
//            // Эффект свечения при нагреве
//            if (warmup > 0.1f) {
//                Draw.blend(Blending.additive);
//                Draw.color(Color.valueOf("ffaa44"), warmup * 0.5f);
//                Fill.circle(x, y, 12f * warmup);
//                Draw.blend();
//                Draw.reset();
//            }
//        }
//    }
//
//    @Override
//    public TurretBuild createBuilding() {
//        return new ClusterMortarBuild();
//    }
//}