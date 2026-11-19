package infernoadvanced.graphics.parts;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;

public class GammaChargePart extends DrawPart{

    public float muzzleOffset = 0f;
    public float layer = Layer.effect;

    public int shapes = 16;
    public int sides = 6;
    public float spawnRadiusMin = 16f;
    public float spawnRadiusMax = 46f;
    public float cycleTime = 55f;
    public float spiralTurns = 0.85f;
    public float hexSize = 4f;
    public float hexSizeTo = 1.2f;
    public float hexStroke = 1.4f;
    public float hexSpinSpeed = 2f;
    public float funnelContract = 0.4f;

    public boolean drawRings = true;
    public int rings = 3;
    public float ringCycle = 40f;
    public float ringRadMax = 21f;
    public float ringRadMin = 6f;
    public float ringStroke = 1.3f;
    public float ringRotSpeed = 1.5f;

    public float sphereRadius = 11f;
    public float coreFraction = 0.45f;
    public Interp sphereCurve = Interp.pow2In;

    public Color color = Color.valueOf("ffffff");
    public Color colorTo = Color.white;
    public Color sphereColor = Color.valueOf("000000");
    public Color coreColor = Color.white;
    public float lightRadius = 70f;
    public float lightOpacity = 0.75f;

    private static final Rand rand = new Rand();

    @Override
    public void draw(PartParams params){
        float charge = params.charge;
        if(charge <= 0.0001f) return;

        float z = Draw.z();
        if(layer > 0f) Draw.z(layer);

        float rot = params.rotation;
        float mx = params.x + Angles.trnsx(rot, muzzleOffset);
        float my = params.y + Angles.trnsy(rot, muzzleOffset);

        float chargeAlpha = Interp.pow2Out.apply(charge);
        float funnel = Mathf.lerp(1f, 1f - funnelContract, charge);

        Draw.blend(Blending.additive);

        if(drawRings){
            for(int i = 0; i < rings; i++){
                float rp = (Time.time / ringCycle + i / (float)rings) % 1f;
                float rr = Mathf.lerp(ringRadMax, ringRadMin, Interp.pow2Out.apply(rp)) * funnel;
                Draw.color(color, colorTo, rp);
                Draw.alpha((1f - rp) * 0.55f * chargeAlpha);
                Lines.stroke(ringStroke * (1f - rp * 0.5f));
                Lines.poly(mx, my, sides, rr, Time.time * ringRotSpeed + i * 20f);
            }
        }

        for(int i = 0; i < shapes; i++){
            rand.setSeed(i * 7919 + 13);
            float speed = rand.random(0.7f, 1.4f);
            float phase = rand.random(1f);
            float sizeScl = rand.random(0.7f, 1.3f);

            float cyc = Time.time * speed / cycleTime + phase;
            float t = cyc % 1f;

            rand.setSeed(i * 104729 + (int)cyc * 7919 + 13);
            float spawnAngle = rand.random(360f);
            float spawnDist = rand.random(spawnRadiusMin, spawnRadiusMax);
            float spinDir = rand.random(1f) < 0.5f ? 1f : -1f;

            float tt = Interp.pow2In.apply(t);
            float dist = spawnDist * (1f - tt) * funnel;
            float angle = spawnAngle + tt * spiralTurns * 360f * spinDir;
            float hx = mx + Angles.trnsx(angle, dist);
            float hy = my + Angles.trnsy(angle, dist);

            float aIn = Mathf.clamp(t / 0.18f);
            float aOut = 1f - Mathf.clamp((t - 0.72f) / 0.28f);
            float alpha = (0.2f + 0.8f * t) * aIn * aOut * chargeAlpha;

            float size = Mathf.lerp(hexSize, hexSizeTo, tt) * sizeScl;
            float hrot = angle * spinDir + Time.time * hexSpinSpeed;

            Draw.color(color, colorTo, tt);
            Draw.alpha(alpha);
            Lines.stroke(hexStroke * (0.4f + 0.6f * (1f - tt)));
            Lines.poly(hx, hy, sides, size, hrot);
        }

        float sphere = sphereRadius * sphereCurve.apply(charge);
        Draw.alpha(chargeAlpha);
        Draw.color(sphereColor);
        Fill.circle(mx, my, sphere);
        Draw.color(coreColor);
        Fill.circle(mx, my, sphere * coreFraction);

        Drawf.light(mx, my, lightRadius * charge, coreColor, lightOpacity * charge);

        Draw.blend();
        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void load(String name){}
}