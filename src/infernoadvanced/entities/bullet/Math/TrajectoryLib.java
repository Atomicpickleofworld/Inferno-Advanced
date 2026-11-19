package infernoadvanced.entities.bullet.Math;

import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec3;

public class TrajectoryLib {

    public static TrajectoryCalculator straight(float speed) {
        return (time, out) -> out.set(speed * time, 0f, 0f);
    }

    public static LoopSpiral loopSpiral(float radius, float angularSpeed, float forwardSpeed) {
        return loopSpiral(radius, angularSpeed, forwardSpeed, 15f);
    }

    public static LoopSpiral loopSpiral(float radius, float angularSpeed, float forwardSpeed, float rampTime) {
        return new LoopSpiral(radius, angularSpeed, forwardSpeed, rampTime);
    }


    public static class LoopSpiral implements TrajectoryCalculator {
        public final float radius, angularSpeed, forwardSpeed, rampTime;

        public LoopSpiral(float radius, float angularSpeed, float forwardSpeed, float rampTime) {
            this.radius = radius;
            this.angularSpeed = angularSpeed;
            this.forwardSpeed = forwardSpeed;
            this.rampTime = rampTime;
        }

        @Override
        public void calculate(float time, Vec3 out) {
            float angle = angularSpeed * time;
            float r = radius * Interp.smooth.apply(Mathf.clamp(time / rampTime, 0f, 1f));
            out.set(
                    forwardSpeed * time + r * Mathf.sin(angle),
                    r * Mathf.cos(angle),
                    0f
            );
        }

        public float loopsPer(float lifetime) {
            return lifetime * angularSpeed / (2f * Mathf.pi);
        }

        public float advancePerLoop() {
            return forwardSpeed * (2f * Mathf.pi / angularSpeed);
        }
    }
}