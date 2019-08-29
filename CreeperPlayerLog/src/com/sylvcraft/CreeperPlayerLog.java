package com.sylvcraft;

import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import com.sylvcraft.events.EntityExplode;

import com.sylvcraft.commands.cpl;


public class CreeperPlayerLog extends JavaPlugin {
  @Override
  public void onEnable() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new EntityExplode(this), this);
    getCommand("cpl").setExecutor(new cpl(this));
    saveDefaultConfig();
  }
  
  public void msg(String msgCode, CommandSender sender) {
  	String tmp = getConfig().getString("messages." + msgCode, msgCode) + ' ';
  	if (tmp.trim().equals("")) return;
  	for (String m : tmp.split("%br%")) {
  		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
  	}
  }

  public void msg(String msgCode, CommandSender sender, Map<String, String> data) {
  	String tmp = getConfig().getString("messages." + msgCode, msgCode) + ' ';
  	if (tmp.trim().equals("")) return;
  	for (Map.Entry<String, String> mapData : data.entrySet()) {
  	  tmp = tmp.replace(mapData.getKey(), mapData.getValue());
  	}
  	msg(tmp, sender);
  }
	
	public void setRadius(CommandSender sender, String[] args, Map<String, String> data) {
  	try {
  		switch (args.length) {
  		case 0:
  			sender.sendMessage("show radius");
  			if (getConfig().getInt("config.detectionradius.x", -1) == -1) {
  				data.put("%value%", getConfig().getString("config.detectionradius"));
  				msg("radius-show-general", sender, data);
  			} else {
  				data.put("%x%", getConfig().getString("config.detectionradius.x"));
  				data.put("%y%", getConfig().getString("config.detectionradius.y"));
  				data.put("%z%", getConfig().getString("config.detectionradius.z"));
  				msg("radius-show-general", sender, data);
  			}
  			break;
  		case 4:
    		data.put("%x%", args[1]);
    		data.put("%y%", args[1]);
    		data.put("%z%", args[1]);
	  		int radiusX = Integer.parseInt(args[1]);
	  		int radiusY = Integer.parseInt(args[2]);
	  		int radiusZ = Integer.parseInt(args[3]);
	  		if (radiusX < 1 || radiusX > 99 || radiusY < 0 || radiusY > 99 || radiusZ < 0 || radiusZ > 99) {
	  			msg("out-of-range", sender);
	  			return;
	  		}
	  		getConfig().set("config.detectionradius.x", radiusX);
	  		getConfig().set("config.detectionradius.y", radiusY);
	  		getConfig().set("config.detectionradius.z", radiusZ);
	  		saveConfig();
	  		msg("radius-set-specific", sender, data);
	  		break;
  		default:
	  		data.put("%value%", args[1]);
	  		int radius = Integer.parseInt(args[1]);
	  		if (radius < 0 || radius > 99) {
	  			msg("out-of-range", sender);
	  			return;
	  		}
	  		getConfig().set("config.detectionradius", radius);
	  		saveConfig();
	  		msg("radius-set-general", sender, data);
  		}
  	} catch (NumberFormatException e) {
  		msg("invalid-number", sender, data);
  	}
	}

	public void setShowRange(CommandSender sender, String[] args, Map<String, String> data) {
  	try {
  		data.put("%value%", args[1]);
  		int radius = Integer.parseInt(args[1]);
  		getConfig().set("config.showrange", radius);
  		saveConfig();
  		msg("range-set", sender, data);
  	} catch (NumberFormatException e) {
  		msg("invalid-number", sender, data);
  	}
	}

	public void showData(CommandSender sender, String[] args, Map<String, String> data) {
  	Player p = (Player)sender;
  	String cfgPathRoot = "logs." + p.getWorld().getName();
  	ConfigurationSection cfg = getConfig().getConfigurationSection(cfgPathRoot);
  	if (cfg == null) {
  		msg("no-results", sender);
  		return;
  	}
  	
  	int detectionRange = getConfig().getInt("config.showrange", 10);
  	String dateFormat = getConfig().getString("config.dateformat", "MM/d/yy @ hh:mm a");
  	boolean headerShown = false;
  	for (String key : cfg.getKeys(false)) {
  		String cfgPathRecord = cfgPathRoot + "." + key;
  		Location tmp = (Location)getConfig().get(cfgPathRecord + ".location");
  		if (tmp == null) continue;
  		if (tmp.distance(p.getLocation()) > detectionRange) continue;
  		try {
  			List<String> playerList = getConfig().getStringList(cfgPathRecord + ".players");
  			data.put("%time%", new SimpleDateFormat(dateFormat).format(Long.parseLong(key.substring(key.indexOf('_')+1, key.length()))));
  			data.put("%world%", tmp.getWorld().getName());
  			data.put("%x%", String.valueOf(tmp.getBlockX()));
  			data.put("%y%", String.valueOf(tmp.getBlockY()));
  			data.put("%z%", String.valueOf(tmp.getBlockZ()));
  			data.put("%players%", (playerList.size() == 0)?"None":String.join(",", playerList));
  			if (!headerShown) {
  				msg("show-header", sender);
  				headerShown = true;
  			}
  			msg("show-data", sender, data);
  		} catch (NumberFormatException ex) {
  			continue;
  		}
  	}
  	if (!headerShown) msg("no-results", sender);
	}

	public void clearData(CommandSender sender, String[] args, Map<String, String> data) {
		if (data.containsKey("mode")) {
			int totalCleared = 0;
			switch (data.get("mode")) {
			case "world":
				ConfigurationSection worldCfg = getConfig().getConfigurationSection("logs." + data.get("world"));
				if (worldCfg != null) {
					totalCleared = worldCfg.getKeys(false).size();
					getConfig().set("logs." + data.get("world"), null);
					saveConfig();
				}
				break;
			case "all":
				ConfigurationSection allCfg = getConfig().getConfigurationSection("logs");
				if (allCfg != null) {
					totalCleared = allCfg.getKeys(false).size();
					getConfig().set("logs", null);
					saveConfig();
				}
				break;
			}
			data.put("%value%", String.valueOf(totalCleared));
			msg("cleared", sender, data);
			return;
		}
		
  	Player p = (Player)sender;
  	String cfgPathRoot = "logs." + p.getWorld().getName();
  	ConfigurationSection cfg = getConfig().getConfigurationSection(cfgPathRoot);
  	if (cfg == null) {
  		msg("no-results", sender);
  		return;
  	}
  	
  	int deletions = 0;
  	int detectionRange = getConfig().getInt("config.showrange", 10);
  	for (String key : cfg.getKeys(false)) {
  		String cfgPathRecord = cfgPathRoot + "." + key;
  		Location tmp = (Location)getConfig().get(cfgPathRecord + ".location");
  		if (tmp == null) continue;
  		if (tmp.distance(p.getLocation()) > detectionRange) continue;
  		getConfig().set(cfgPathRecord, null);
  		deletions++;
  	}
		if (deletions > 0) {
			saveConfig();
			data.put("%value%", String.valueOf(deletions));
			msg("cleared", sender, data);
		} else {
			msg("no-results", sender);
		}
	}
}