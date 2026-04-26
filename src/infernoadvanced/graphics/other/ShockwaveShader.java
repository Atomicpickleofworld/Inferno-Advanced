//package infernoadvanced.graphics.other;
//
//import arc.*;
//import arc.graphics.gl.*;
//
//public class ShockwaveShader extends Shader {
//
//    private static final String vertexShader =
//            "attribute vec4 a_position;\n" +
//                    "attribute vec2 a_texCoord0;\n" +
//                    "varying vec2 v_texCoords;\n" +
//                    "void main(){\n" +
//                    "  v_texCoords = a_texCoord0;\n" +
//                    "  gl_Position = a_position;\n" +
//                    "}";
//
//    private static final String fragmentShader =
//            "#define HIGHP\n" +
//                    "uniform sampler2D u_texture;\n" +
//                    "uniform vec2 u_resolution;\n" +
//                    "uniform vec2 u_campos;\n" +
//                    "uniform int u_wavecount;\n" +
//                    "uniform vec4 u_waves[256];\n" +
//                    "varying vec2 v_texCoords;\n" +
//                    "\n" +
//                    "void main(){\n" +
//                    "  vec2 uv = v_texCoords.xy;\n" +
//                    "  \n" +
//                    "  // ✅ Правильный расчёт мировых координат\n" +
//                    "  vec2 worldCoords = uv * u_resolution + u_campos;\n" +
//                    "\n" +
//                    "  vec2 offset = vec2(0.0);\n" +
//                    "  for(int i = 0; i < u_wavecount; ++i){\n" +
//                    "    vec4 wave = u_waves[i];\n" +
//                    "    vec2 center = wave.xy;\n" +
//                    "    float radius = wave.z;\n" +
//                    "    float strength = wave.w;\n" +
//                    "\n" +
//                    "    float dst = distance(center, worldCoords);\n" +
//                    "    float ringWidth = 50.0;\n" +
//                    "\n" +
//                    "    // Кольцо волны\n" +
//                    "    float ring = 1.0 - abs(dst - radius) / ringWidth;\n" +
//                    "    ring = clamp(ring, 0.0, 1.0);\n" +
//                    "    ring = ring * ring * (3.0 - 2.0 * ring);\n" +
//                    "\n" +
//                    "    float intensity = ring * strength;\n" +
//                    "\n" +
//                    "    // Искажение от центра волны\n" +
//                    "    vec2 dir = normalize(worldCoords - center);\n" +
//                    "    offset += dir * intensity * 30.0;\n" +
//                    "  }\n" +
//                    "\n" +
//                    "  // ✅ Применяем смещение и конвертируем обратно в UV\n" +
//                    "  vec2 newWorldCoords = worldCoords + offset;\n" +
//                    "  vec2 newUv = (newWorldCoords - u_campos) / u_resolution;\n" +
//                    "  \n" +
//                    "  // ✅ Защита от выхода за границы\n" +
//                    "  if(newUv.x < 0.0 || newUv.x > 1.0 || newUv.y < 0.0 || newUv.y > 1.0){\n" +
//                    "    gl_FragColor = texture2D(u_texture, uv);\n" +
//                    "  } else {\n" +
//                    "    gl_FragColor = texture2D(u_texture, newUv);\n" +
//                    "  }\n" +
//                    "}";
//
//    public float[] waveData = new float[0];
//    public int waveCount = 0;
//
//    public ShockwaveShader() {
//        super(vertexShader, fragmentShader);
//    }
//
//    @Override
//    public void apply() {
//        setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2,
//                Core.camera.position.y - Core.camera.height / 2);
//        setUniformf("u_resolution", Core.camera.width, Core.camera.height);
//        setUniformi("u_wavecount", waveCount);
//        setUniform4fv("u_waves", waveData, 0, waveData.length);
//    }
//}
