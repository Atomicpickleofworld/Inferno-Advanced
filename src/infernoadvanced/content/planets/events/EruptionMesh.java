package infernoadvanced.content.planets.events;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Log;
import arc.util.Time;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

import static mindustry.Vars.renderer;

public class EruptionMesh implements GenericMesh {
    private static final int DIVISIONS = 5;

    private static final String VERT = String.join("\n",
            "#define WAVES " + SolarActivity.WAVES,
            "",
            "attribute vec3 a_position;",
            "",
            "uniform mat4 u_proj;",
            "uniform mat4 u_trans;",
            "uniform float u_time;",
            "uniform float u_reach;",
            "",
            "uniform vec4 u_wave[WAVES];",
            "uniform vec4 u_shape[WAVES];",
            "",
            "varying float v_glow;",
            "varying vec3 v_world;",
            "varying vec3 v_normal;",
            "",
            "void main(){",
            "    vec3 n = normalize(a_position);",
            "    float disp = 0.0;",
            "",
            "    for(int i = 0; i < WAVES; i++){",
            "        float c = max(dot(n, u_wave[i].xyz), 0.0);",
            "        if(c < 0.3) continue;",
            "",
            "        float phase = u_wave[i].w;",
            "        float cone = pow(c, u_shape[i].x);",
            "        float ripple = 0.85 + 0.15 * sin(c * 40.0 - u_time * 4.0 + float(i));",
            "        float env = 4.0 * phase * (1.0 - phase);",
            "        float reach = u_reach * u_shape[i].y * pow(phase, 0.45);",
            "        disp += cone * ripple * env * reach;",
            "    }",
            "",
            "    vec3 pos = a_position + n * disp;",
            "    v_glow = disp / u_reach;",
            "    v_world = (u_trans * vec4(pos, 1.0)).xyz;",
            "    v_normal = (u_trans * vec4(n, 0.0)).xyz;",
            "    gl_Position = u_proj * u_trans * vec4(pos, 1.0);",
            "}"
    );

    private static final String FRAG = String.join("\n",
            "varying float v_glow;",
            "varying vec3 v_world;",
            "varying vec3 v_normal;",
            "",
            "uniform vec3 u_campos;",
            "",
            "void main(){",
            "    float g = clamp(v_glow, 0.0, 1.0);",
            "    if(g < 0.002) discard;",
            "",
            "    vec3 deep = vec3(0.55, 0.07, 0.12);",
            "    vec3 mid = vec3(1.0, 0.35, 0.15);",
            "    vec3 core = vec3(1.0, 0.8, 0.55);",
            "",
            "    vec3 color = mix(deep, mid, smoothstep(0.0, 0.4, g));",
            "    color = mix(color, core, smoothstep(0.4, 0.85, g));",
            "",
            "    vec3 view = normalize(u_campos - v_world);",
            "    float facing = clamp(dot(normalize(v_normal), view), 0.0, 1.0);",
            "",
            "    float alpha = g * (0.35 + 0.65 * facing);",
            "    gl_FragColor = vec4(color * (0.9 + g), alpha);",
            "}"
    );

    private final float reach;
    private final Mesh mesh;
    private final float[] waveData = new float[SolarActivity.WAVES * 4];
    private final float[] shapeData = new float[SolarActivity.WAVES * 4];
    /**
     * Шейдер создаётся лениво в render(): там есть контекст OpenGL, в конструкторе его нет.
     */
    private Shader shader;
    private boolean shaderFailed;

    public EruptionMesh(Planet star) {
        this.reach = SolarActivity.REACH - star.radius;
        this.mesh = MeshBuilder.buildHex(new HexMesher() {
            @Override
            public float getHeight(Vec3 position) {
                return 0f;
            }

            @Override
            public void getColor(Vec3 position, Color out) {
                out.set(Color.white);
            }
        }, DIVISIONS, star.radius, 0f);
    }

    private boolean ensureShader() {
        if (shader != null) return true;
        if (shaderFailed) return false;

        try {
            shader = new Shader(VERT, FRAG);
            Log.info("EruptionMesh: shader compiled");
            return true;
        } catch (Exception e) {
            shaderFailed = true;
            Log.err("EruptionMesh: shader compilation failed, eruptions disabled", e);
            return false;
        }
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        if (!ensureShader()) return;

        float seconds = Time.globalTime / 60f;
        SolarActivity.fill(seconds, waveData, shapeData);

        Blending.additive.apply();
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", transform.val);
        shader.setUniformf("u_time", seconds);
        shader.setUniformf("u_reach", reach);
        shader.setUniformf("u_campos", renderer.planets.cam.position);
        shader.setUniform4fv("u_wave", waveData, 0, waveData.length);
        shader.setUniform4fv("u_shape", shapeData, 0, shapeData.length);

        mesh.render(shader, Gl.triangles);
        Blending.normal.apply();
    }

    @Override
    public void dispose() {
        mesh.dispose();
        if (shader != null) shader.dispose();
    }
}