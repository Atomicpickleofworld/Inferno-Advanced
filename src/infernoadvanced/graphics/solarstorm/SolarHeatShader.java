package infernoadvanced.graphics;

import arc.Core;
import arc.graphics.gl.Shader;
import arc.util.Log;
import arc.util.Time;
import infernoadvanced.content.planets.events.SolarStorm;

import static mindustry.Vars.*;

public class SolarHeatShader{
    public static float amplitude = 0.0035f;

    private static final String VERT = String.join("\n",
            "attribute vec4 a_position;",
            "attribute vec2 a_texCoord0;",
            "varying vec2 v_texCoords;",
            "void main(){",
            "    v_texCoords = a_texCoord0;",
            "    gl_Position = a_position;",
            "}"
    );

    private static final String FRAG = String.join("\n",
            "#define HIGHP",
            "uniform sampler2D u_texture;",
            "uniform vec2 u_campos;",
            "uniform vec2 u_resolution;",
            "uniform float u_time;",
            "uniform float u_heat;",
            "varying vec2 v_texCoords;",
            "void main(){",
            "    vec2 c = v_texCoords;",
            "    vec2 coords = c * u_resolution + u_campos;",
            "    float w1 = sin(c.y * 90.0 + u_time * 2.2);",
            "    float w2 = sin(c.y * 37.0 - u_time * 1.4 + c.x * 8.0);",
            "    float w3 = sin(c.x * 55.0 + u_time * 1.7);",
            "    coords += vec2(w1 * 0.6 + w2 * 0.4, w3 * 0.5) * u_resolution.y * AMPL * u_heat;",
            "    gl_FragColor = texture2D(u_texture, (coords - u_campos) / u_resolution);",
            "}"
    );

    private static Shader shader;
    private static boolean failed;

    public static void load(){
        IAScreenFX.add(get(), () -> SolarStorm.intensity > 0.02f);
    }

    public static Shader get(){
        if(shader == null && !failed && !headless){
            try{
                shader = new Shader(VERT, FRAG.replace("AMPL", String.valueOf(amplitude))){
                    @Override
                    public void apply(){
                        setUniformf("u_campos",
                                Core.camera.position.x - Core.camera.width / 2f,
                                Core.camera.position.y - Core.camera.height / 2f);
                        setUniformf("u_resolution", Core.camera.width, Core.camera.height);
                        setUniformf("u_time", Time.time / 60f);
                        setUniformf("u_heat", SolarStorm.intensity);
                    }
                };
            }catch(Throwable t){
                failed = true;
                Log.err("SolarHeatShader failed", t);
            }
        }
        return shader;
    }
}