package com.runicrealms.plugin.professions.gathering.mining;

import com.runicrealms.plugin.rdb.RunicDatabase;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.impl.ManualRangeManager;
import org.bukkit.Bukkit;
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

    public Dummy getBaseEntity() {
        return baseEntity;
    }

    public void spawn() {
        // Create the specific design at the location
        createDesign();

        // Default visual accent for all tiers
        createModel();
    }

    public OreTier getOreTier() {
        return oreTier;
    }

    private void createModel() {
        ActiveModel activeModel = ModelEngineAPI.createActiveModel(oreType.getModelId());

        // Create base entity as a Dummy
        this.baseEntity = ModelEngineAPI.createDummy();
        Location loc = location.clone().add(-1, 0, -1);
        this.baseEntity.setLocation(loc);

        // Create model
        this.modeledEntity = ModelEngineAPI.createModeledEntity(baseEntity);
        modeledEntity.addModel(activeModel, true);

        // Create manual range manager (32 blocks)
        ManualRangeManager manualRangeManager = new ManualRangeManager(baseEntity, modeledEntity);
        manualRangeManager.setRenderDistance(32);
        this.baseEntity.setRangeManager(manualRangeManager);

        // Show model to all players
        RunicDatabase.getAPI().getCharacterAPI().getLoadedCharacters().forEach(uuid -> {
            manualRangeManager.forceSpawn(Bukkit.getPlayer(uuid));
            this.baseEntity.setCollidableToLiving(Bukkit.getPlayer(uuid), false);
        });
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

