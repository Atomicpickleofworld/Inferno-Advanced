package infernoadvanced.loaders;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectMap;
import arc.util.Log;
import infernoadvanced.InfernoAdvancedMod;

import static mindustry.Vars.mods;

public class IAAtlas {
    private static final ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

    public static void load() {
        register("mod-icon", "textures/mod-icon.png");
        register("mod-banner", "textures/mod-banner.png");
    }

    public static void register(String key, String path) {
        Fi file = mods.getMod(InfernoAdvancedMod.class).root.child(path);
        if (!file.exists()) {
            Log.warn("IAAtlas: missing texture: " + path);
            regions.put(key, Core.atlas.find("error"));
            return;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.linear, Texture.TextureFilter.linear);
        regions.put(key, new TextureRegion(texture));
    }

    public static TextureRegion get(String key) {
        return regions.get(key, Core.atlas.find("error"));
    }

    public static Drawable drawable(String key) {
        return new TextureRegionDrawable(get(key));
    }
}