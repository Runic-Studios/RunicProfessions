package com.runicrealms.plugin.professions.gathering.mining;

import com.runicrealms.plugin.RunicProfessions;
import com.runicrealms.runicrestart.event.PreShutdownEvent;
import org.bukkit.Bukkit;
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
    }

    @EventHandler
    public void onPreShutdown(PreShutdownEvent event) {
        for (OreNode oreNode : oreNodes) {
            oreNode.destroy();
        }
    }

    public List<OreNode> getOreNodes() {
        return oreNodes;
    }

}
