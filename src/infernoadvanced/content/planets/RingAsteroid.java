package infernoadvanced.content.planets;

import arc.math.Angles;
import arc.math.geom.Vec3;
import mindustry.type.Planet;

public class RingAsteroid extends Planet {
    private static final Vec3 offset = new Vec3();

    public float inclination;

    public RingAsteroid(String name, Planet parent, float radius, float inclination) {
        super(name, parent, radius);
        this.inclination = inclination;
        accessible = false;
        visible = true;
        hasAtmosphere = false;
        updateLighting = false;
        drawOrbit = false;
        bloom = false;
    }

    @Override
    public Vec3 addParentOffset(Vec3 in) {
        float angle = getOrbitAngle();
        offset.set(Angles.trnsx(angle, orbitRadius), 0f, Angles.trnsy(angle, orbitRadius)).rotate(Vec3.X, inclination);
        return in.add(offset);
    }
}