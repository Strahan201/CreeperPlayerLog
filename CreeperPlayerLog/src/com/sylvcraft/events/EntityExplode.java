package com.sylvcraft.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.sylvcraft.CreeperPlayerLog;
import org.bukkit.event.entity.EntityExplodeEvent;


public class EntityExplode implements Listener {
  CreeperPlayerLog plugin;
  
  public EntityExplode(CreeperPlayerLog instance) {
    plugin = instance;
  }

	@EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
		if (!(e.getEntity() instanceof Creeper)) return;

		List<String> players = new ArrayList<String>();
		Creeper c = (Creeper)e.getEntity();

		int rangeX = plugin.getConfig().getInt("config.detectionradius.x", plugin.getConfig().getInt("config.detectionradius", 10));
		int rangeY = plugin.getConfig().getInt("config.detectionradius.y", 5);
		int rangeZ = plugin.getConfig().getInt("config.detectionradius.z", plugin.getConfig().getInt("config.detectionradius", 10));
		
		for (Entity ent : c.getNearbyEntities(rangeX, rangeY, rangeZ)) {
			if (!(ent instanceof Player)) continue;
			players.add(ent.getName());
		}

		long curtime = System.currentTimeMillis();
		plugin.getConfig().set("logs." + c.getWorld().getName() + "." + c.getEntityId() + "_" + curtime + ".players", players);
		plugin.getConfig().set("logs." + c.getWorld().getName() + "." + c.getEntityId() + "_" + curtime + ".location", c.getLocation());
		plugin.saveConfig();

  }
}