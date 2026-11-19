package infernoadvanced.loaders;

import infernoadvanced.InfernoAdvancedMod;
import mindustry.mod.Mods.LoadedMod;

import static mindustry.Vars.mods;

public class IAMeta {
    public static LoadedMod mod;
    public static String name, displayName, version, author, subtitle, description, minGameVersion;

    public static void load() {
        mod = mods.getMod(InfernoAdvancedMod.class);
        name = mod.name;
        displayName = mod.meta.displayName == null ? mod.meta.name : mod.meta.displayName;
        version = mod.meta.version;
        author = mod.meta.author;
        subtitle = mod.meta.subtitle;
        description = mod.meta.description;
        minGameVersion = mod.meta.minGameVersion;
    }

    public static String versionString() {
        return version == null ? "dev" : version;
    }

    public static String authorString() {
        return author == null ? "AtomicPickle" : author;
    }
}