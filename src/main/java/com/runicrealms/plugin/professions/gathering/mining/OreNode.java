package com.runicrealms.plugin.professions.gathering.mining;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class OreNode {
    private final OreType oreType;
    private final OreTier oreTier;
    private final Location location; // Location of the ore node
    private final List<Location> blocks; // List of all the block locations in the node
    // Visual models
    private Dummy baseEntity;
    private ModeledEntity modeledEntity; // Model from ModelEngine

    // Constructor
    public OreNode(Location location, OreType oreType, OreTier oreTier) {
        this.oreType = oreType;
        this.oreTier = oreTier;
        this.location = location;
        this.blocks = new ArrayList<>();
    }

    public void spawn() {
        // Create the specific design at the location
        createDesign();

        // Spawn visual accent for ore tiers two and four
        if (oreTier == OreTier.TWO || oreTier == OreTier.FOUR) {
            ActiveModel activeModel = ModelEngineAPI.createActiveModel("ore_ruby"); // Todo!
//            this.baseEntity = (Dummy) location.getWorld().spawnEntity(location.clone().add(1, 2, 1), EntityType.ARMOR_STAND);
            this.baseEntity = ModelEngineAPI.createDummy();
//            baseEntity.set
//            baseEntity.setInvulnerable(true);

            this.modeledEntity = ModelEngineAPI.createModeledEntity(baseEntity);
            modeledEntity.addModel(activeModel, true);
            modeledEntity.setBaseEntityVisible(false);
        }
    }

    public void destroy() {
        this.modeledEntity.destroy();
        for (Location loc : blocks) {
            loc.getBlock().setType(Material.AIR);
        }
    }

    private void createDesign() {
        // Define the specific design as offsets from the base location
        int[][] offsets = {
                {0, 0, 0},
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {0, 0, -1},
                {1, 0, 1},
                {0, 1, 0},
        };

        // Create a block at each offset location
        for (int[] offset : offsets) {
            Location blockLocation = location.clone().add(offset[0], offset[1], offset[2]);
            blocks.add(blockLocation);
            blockLocation.getBlock().setType(oreType.getMaterial(oreTier));
        }
    }

}

