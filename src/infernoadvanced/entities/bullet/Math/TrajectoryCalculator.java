package infernoadvanced.entities.bullet.Math;

import arc.math.geom.Vec3;

public interface TrajectoryCalculator {
    void calculate(float time, Vec3 out);
}