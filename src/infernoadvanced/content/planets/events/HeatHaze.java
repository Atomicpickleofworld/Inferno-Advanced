package infernoadvanced.content.planets.events;

import arc.Core;
import arc.graphics.gl.Shader;
import arc.util.Log;
import arc.util.Time;

import static mindustry.Vars.*;

public class HeatHaze{
    public static float intensity;

    private static final String VERT = String.join("\n",
            "attribute vec4 a_position;",
            "attribute vec4 a_color;",
            "uniform mat4 u_proj;",
            "uniform mat4 u_trans;",
            "varying vec4 v_col;",
            "void main(){",
            "    v_col = a_color;",
            "    gl_Position = u_proj * u_trans * a_position;",
            "}"
    );

    private static final String FRAG = String.join("\n",
            "#define HIGHP",
            "uniform vec2 u_resolution;",
            "uniform float u_time;",
            "uniform float u_intensity;",
            "varying vec4 v_col;",
            "void main(){",
            "    vec2 uv = gl_FragCoord.xy / u_resolution;",
            "    float a = sin(uv.y * 110.0 + u_time * 2.6);",
            "    float b = sin(uv.y * 43.0 - u_time * 1.5 + uv.x * 6.0);",
            "    float band = 0.5 + 0.5 * a * b;",
            "    gl_FragColor = vec4(v_col.rgb, v_col.a * u_intensity * (0.3 + 0.7 * band));",
            "}"
    );

    private static Shader shader;
    private static boolean failed;

    public static Shader get(){
        if(shader == null && !failed && !headless){
            try{
                shader = new Shader(VERT, FRAG){
                    @Override
                    public void apply(){
                        setUniformf("u_time", Time.globalTime / 60f);
                        setUniformf("u_intensity", intensity);
                        setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
                    }
                };
            }catch(Throwable t){
                failed = true;
                Log.err("HeatHaze shader failed", t);
            }
        }
        return shader;
    }
}