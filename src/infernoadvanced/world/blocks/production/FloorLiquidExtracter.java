package infernoadvanced.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;


public class FloorLiquidExtracter extends GenericCrafter {

    public @Nullable Attribute attribute;
    public float baseEfficiency = 0f;
    public float boostScale = 1f;
    public boolean displayEfficiency = true;

    public float powerProduction = 1f;

    public FloorLiquidExtracter(String name) {
        super(name);
        hasPower = true;
        outputsPower = false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        if(attribute != null){
            drawPlaceText(Core.bundle.format("bar.efficiency", Math.round(Math.max((sumAttribute(attribute, x, y)) / size / size + percentSolid(x, y) * baseEfficiency, 0f) * boostScale * 100f)), x, y, valid);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        if(attribute != null){
            stats.add(baseEfficiency > 0.0001f ? Stat.affinities : Stat.tiles, attribute, floating, 1f, baseEfficiency <= 0.001f);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(attribute != null){
            float sum = tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attribute));
            return sum > 0.00001f;
        }
        return super.canPlaceOn(tile, team, rotation);
    }

    public class FloorPowerCrafterBuild extends GenericCrafterBuild {
        public float attrsum;

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            if(attribute != null){
                attrsum = sumAttribute(attribute, tile.x, tile.y);
            }
        }

        public float efficiencyMultiplier(){
            if(attribute == null) return 1f;
            return baseEfficiency + (attrsum / (size * size)) * boostScale;
        }

        @Override
        public float efficiencyScale() {
            return super.efficiencyScale() * efficiencyMultiplier();
        }
    }
}