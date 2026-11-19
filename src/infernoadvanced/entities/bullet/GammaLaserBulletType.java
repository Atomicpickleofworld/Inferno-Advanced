package infernoadvanced.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;

public class GammaLaserBulletType extends LaserBulletType {

    public Color plasmaColor1 = Color.valueOf("b3ff00");
    public Color plasmaColor2 = Color.valueOf("7a00ff");
    public Color coreColor = Color.valueOf("0d0514");
    public Color hexColor = Color.valueOf("e0ff00");

    public float hexSpacing = 24f;
    public float hexRotSpeed = 3f;
    public float plasmaPulseSpeed = 4f;
    public float plasmaPulseMag = 0.25f;
    public float backLengthFract = 0.125f;
    public float widthScale = 1f;
    public float coreWidthMult = 1;

    public float drawOffset = 0f;
    public float drawSideOffset = 0f;

    private static final Rand rand = new Rand();

    @Override
    public void draw(Bullet b) {
        float realLength = length * b.fout();
        float baseWidth = width * b.fout();
        float rotation = b.rotation();
        float backLen = realLength * backLengthFract;

        float baseX = b.x + Angles.trnsx(rotation, drawOffset) + Angles.trnsx(rotation + 90f, drawSideOffset);
        float baseY = b.y + Angles.trnsy(rotation, drawOffset) + Angles.trnsy(rotation + 90f, drawSideOffset);

        Draw.blend(Blending.additive);
        float pulse = 1f + Mathf.sin(Time.time, plasmaPulseSpeed, plasmaPulseMag);

        for (int i = 0; i < 3; i++) {
            float w = (baseWidth * (1.5f + i * 0.4f) * pulse) * widthScale;
            float colLerp = (float) i / 2f;
            Draw.color(plasmaColor1, plasmaColor2, colLerp);
            Draw.alpha(0.4f * b.fout());

            Drawf.tri(baseX, baseY, w, realLength, rotation);
            Drawf.tri(baseX, baseY, w, backLen, rotation + 180f);
        }

        rand.setSeed(b.id);
        Draw.color(Color.white, plasmaColor1, 0.8f);
        Draw.alpha(0.9f * b.fout());

        for (int i = 0; i < 25; i++) {
            float dist = rand.random(realLength);
            float offset = rand.range(baseWidth * 0.8f);
            float angle = rotation + 90f;

            float px = baseX + Angles.trnsx(rotation, dist) + Angles.trnsx(angle, offset);
            float py = baseY + Angles.trnsy(rotation, dist) + Angles.trnsy(angle, offset);

            float size = rand.random(1.5f, 3.5f) * b.fout();
            size *= (1f + Mathf.sin(Time.time * 2f + i * 15f, 3f, 0.5f));

            Fill.circle(px, py, size);
        }

        Draw.color(hexColor);
        Draw.alpha(0.85f * b.fout());
        float hexSize = baseWidth * 0.55f;
        Lines.stroke(1.5f * b.fout());

        for (float i = 0; i < realLength; i += hexSpacing) {
            float px = baseX + Angles.trnsx(rotation, i);
            float py = baseY + Angles.trnsy(rotation, i);
            float rot = Time.time * hexRotSpeed + i;
            float currentHexSize = hexSize * (1f + Mathf.sin(Time.time * 2f + i, 4f, 0.15f));
            Lines.poly(px, py, 6, currentHexSize, rot);
        }

        Draw.blend();
        Draw.color(coreColor);
        Draw.alpha(1f);
        float coreWidth = (baseWidth * 0.25f) * coreWidthMult;

        Drawf.tri(baseX, baseY, coreWidth, realLength, rotation);
        Drawf.tri(baseX, baseY, coreWidth, backLen, rotation + 180f);

        Draw.blend(Blending.additive);
        Draw.color(Color.white);
        Draw.alpha(0.9f * b.fout());

        Drawf.tri(baseX, baseY, coreWidth * 0.3f, realLength, rotation);
        Drawf.tri(baseX, baseY, coreWidth * 0.3f, backLen, rotation + 180f);

        Drawf.light(baseX, baseY, realLength * 0.7f, plasmaColor1, 0.5f * b.fout());

        Draw.blend();
        Draw.reset();
    }
}