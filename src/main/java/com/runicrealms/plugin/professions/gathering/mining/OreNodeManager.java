package com.runicrealms.plugin.professions.gathering.mining;

import com.runicrealms.plugin.professions.RunicProfessions;
import com.runicrealms.plugin.rdb.event.CharacterLoadedEvent;
import com.runicrealms.plugin.runicrestart.event.PreShutdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to control spawning and de-spawning of ore nodes around the world
 * Uses a yml file to specify a list of locations to spawn the ore nodes and destroys them on server shutdown
 *
 * @author Skyfallin
 */
public class OreNodeManager implements Listener {

    private final List<OreNode> oreNodes = new ArrayList<>();

    public OreNodeManager() {
        Bukkit.getPluginManager().registerEvents(this, RunicProfessions.getInstance());
        startParticleTask();
    }

    private void startParticleTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RunicProfessions.getInstance(), () -> oreNodes.forEach(oreNode -> {
            Color color = Color.GRAY;
            switch (oreNode.getOreTier()) {
                case TWO -> color = Color.GREEN;
                case THREE -> color = Color.AQUA;
                case FOUR -> color = Color.FUCHSIA;
            }
            oreNode.getBaseEntity().getWorld().spawnParticle
                    (
                            Particle.REDSTONE,
                            oreNode.getBaseEntity().getLocation(),
                            10,
                            0.5f,
                            0,
                            0.5f,
                            0,
                            new Particle.DustOptions(color, 3)
                    );
        }), 0, 3 * 20L);
    }

    @EventHandler
    public void onPreShutdown(PreShutdownEvent event) {
        for (OreNode oreNode : oreNodes) {
            oreNode.destroy();
        }
    }

    @EventHandler
    public void onCharacterLoad(CharacterLoadedEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(RunicProfessions.getInstance(),
                () -> oreNodes.forEach(oreNode -> {
                    oreNode.getBaseEntity().getRangeManager().forceSpawn(event.getPlayer());
                    oreNode.getBaseEntity().setCollidableToLiving(event.getPlayer(), false);
                }));
    }

    public List<OreNode> getOreNodes() {
        return oreNodes;
    }

}
