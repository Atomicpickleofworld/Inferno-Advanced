package infernoadvanced.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.SectorPresets;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

public class InfernoTechTree {
    public static TechTree.TechNode context = null;

    public static void moveVanillaNode(UnlockableContent content, UnlockableContent newParentContent) {
        TechTree.TechNode node = TechTree.all.find(t -> t.content == content);
        TechTree.TechNode newParent = TechTree.all.find(t -> t.content == newParentContent);

        if (node != null && newParent != null) {
            if (node.parent != null) {
                node.parent.children.remove(node);
            }

            node.parent = newParent;
            newParent.children.add(node);

//            Log.info("InfernoTechTree: successfully deported, " + content.name + " к " + newParentContent.name);
        } else {
//            Log.warn("InfernoTechTree: Failed to find nodes for transfer!");
        }
    }

    public static void load() {

        moveVanillaNode(Blocks.shockMine, Blocks.duo);
        addToNode(Blocks.shockMine, () -> {
            node(InfernoBlocks.clusterMine, () ->
                    node(InfernoBlocks.blastMine,
                            Seq.with(new Objectives.SectorComplete(SectorPresets.stainedMountains)),
                            () -> {
                            }));
            node(InfernoBlocks.tripwireMine, () ->
                    node(InfernoBlocks.stormMine,
                            Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex)),
                            () -> {
                            }));
        });

        addToNode(Blocks.scatter, () -> {
            node(InfernoBlocks.skyfall, () -> {
                node(InfernoBlocks.MLRSdownpour,
                        Seq.with(new Objectives.SectorComplete(SectorPresets.windsweptIslands)),
                        () -> {
                            node(InfernoBlocks.MLRShail,
                                    Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex)),
                                    () -> {
                                    });
                        });
            });
        });

        addToNode(Blocks.fuse, () -> {
            node(InfernoBlocks.sievert,
                    Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex)),
                    () -> {
                    });
        });

        addToNode(Blocks.parallax, () -> {
            node(InfernoBlocks.impulse,
                    Seq.with(new Objectives.SectorComplete(SectorPresets.stainedMountains)),
                    () -> {
                    });
        });
    }


    public static void addToNode(UnlockableContent p, Runnable c) {
        context = TechTree.all.find((t) -> t.content == p);
        if (context == null) {
            arc.util.Log.warn("InfernoTechTree: parent node not found for " + p.name);
            return;
        }
        c.run();
    }

    public static void node(UnlockableContent content, Runnable children) {
        node(content, content.researchRequirements(), children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
        node(content, requirements, null, children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children) {
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if (objectives != null) {
            node.objectives.addAll(objectives);
        }

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    public static void node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives, children);
    }

    public static void node(UnlockableContent block) {
        node(block, () -> {
        });
    }
}