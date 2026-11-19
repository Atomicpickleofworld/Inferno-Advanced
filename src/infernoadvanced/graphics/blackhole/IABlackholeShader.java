package infernoadvanced.graphics.blackhole;

import arc.*;
import arc.graphics.gl.*;

import static arc.Core.files;
import static mindustry.Vars.tree;

public class IABlackholeShader {
    public static int MaxCont = 4;
    public static HoleShader holeShader;

    public static void createShader() {
        if (MaxCont >= 512) return;

        MaxCont = Math.min(MaxCont * 2, 512);
        if (holeShader != null) holeShader.dispose();
        Shader.prependFragmentCode = "#define MAX_COUNT " + MaxCont + "\n";
        holeShader = new HoleShader();
        Shader.prependFragmentCode = "";
    }

    public static class HoleShader extends Shader {
        public float[] blackHoles;

        public HoleShader() {
            super(
                    files.internal("shaders/screenspace.vert"),
                    tree.get("shaders/TearingSpace.frag")
            );
        }

        @Override
        public void apply() {
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);

            setUniformi("u_blackholecount", blackHoles.length / 4);
            if (blackHoles.length > 0) {
                setUniform4fv("u_blackholes", blackHoles, 0, blackHoles.length);
            }
        }
    }
}
