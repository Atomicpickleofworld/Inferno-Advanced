package infernoadvanced.content.planets.infernos;

import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.g3d.Camera3D;
import mindustry.graphics.g3d.MeshBuilder;
import mindustry.type.Planet;

public class InfernosPlanet extends Planet {
    public float haloRadIn = 0.02f, haloRadOut = 0.2f;
    public Color haloColor = Color.valueOf("ff4d1a");
    public float rimRadIn = 0.005f, rimRadOut = 0.05f;
    public Color rimColor = Color.valueOf("ff8a3d");

    private Mesh atmoProxy;

    public InfernosPlanet(String name, Planet parent, float radius, int sectorSize) {
        super(name, parent, radius, sectorSize);
    }

    @Override
    public void drawAtmosphere(Mesh atmosphere, Camera3D cam) {
        if (atmoProxy == null) {
            atmoProxy = MeshBuilder.buildHex(Color.white, 2, radius + Math.max(haloRadOut, rimRadOut) + 0.04f);
        }

        float in = atmosphereRadIn, out = atmosphereRadOut;
        Color color = atmosphereColor;

        atmosphereRadIn = haloRadIn;
        atmosphereRadOut = haloRadOut;
        atmosphereColor = haloColor;
        super.drawAtmosphere(atmoProxy, cam);

        atmosphereRadIn = rimRadIn;
        atmosphereRadOut = rimRadOut;
        atmosphereColor = rimColor;
        super.drawAtmosphere(atmoProxy, cam);

        atmosphereRadIn = in;
        atmosphereRadOut = out;
        atmosphereColor = color;
    }

    @Override
    public void removeContent() {
        super.removeContent();
        if (atmoProxy != null) {
            atmoProxy.dispose();
            atmoProxy = null;
        }
    }
}