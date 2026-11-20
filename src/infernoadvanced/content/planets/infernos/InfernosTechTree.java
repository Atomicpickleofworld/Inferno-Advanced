package infernoadvanced.content.planets.infernos;

import infernoadvanced.content.InfernoItems;
import infernoadvanced.content.InfernoLiquids;
import infernoadvanced.content.InfernoPlanets;
import infernoadvanced.content.InfernoTestContent;
import mindustry.content.Items;
import mindustry.content.Liquids;

import static mindustry.content.TechTree.nodeProduce;
import static mindustry.content.TechTree.nodeRoot;

public class InfernosTechTree {
    public static void load() {
        InfernoPlanets.infernos.techTree = nodeRoot("infernos", InfernoTestContent.testCore, () -> {

            nodeProduce(InfernoItems.ferrite, () -> {

                nodeProduce(Liquids.water, () -> {
                });

                nodeProduce(Items.lead, () -> {
                    nodeProduce(Items.titanium, () -> {
                        nodeProduce(Items.thorium, () -> {
                            nodeProduce(InfernoItems.cesium137, () -> nodeProduce(InfernoItems.barium137m, () -> {
                            }));
                            nodeProduce(InfernoItems.barium133, () -> {
                            });
                        });
                        nodeProduce(Items.tungsten, () -> nodeProduce(Items.carbide, () -> nodeProduce(InfernoItems.cermet, () -> {
                        })));

                        nodeProduce(InfernoItems.sitall, () -> {
                        });
                    });
                });

                nodeProduce(Items.sand, () -> {
                    nodeProduce(Liquids.slag, () -> {
                    });
                    nodeProduce(Items.coal, () -> {
                        nodeProduce(Items.graphite, () -> nodeProduce(Items.silicon, () -> {
                        }));
                        nodeProduce(InfernoLiquids.carbonDioxide, () -> {
                        });
                        nodeProduce(InfernoItems.celestine, () -> nodeProduce(InfernoItems.strontium, () -> nodeProduce(InfernoItems.strontiumTitanate, () -> {
                        })));
                    });
                });
            });
        });
    }
}