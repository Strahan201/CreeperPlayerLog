package com.sylvcraft.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import com.sylvcraft.CreeperPlayerLog;

public class cpl implements TabExecutor {
  CreeperPlayerLog plugin;
  
  public cpl(CreeperPlayerLog instance) {
    plugin = instance;
  }

	@Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tabs = new ArrayList<String>();
		if (args.length == 1) {
			if (sender.hasPermission("CreeperPlayerLog.show")) tabs.add("show");
			if (sender.hasPermission("CreeperPlayerLog.radius")) tabs.add("radius");
			if (sender.hasPermission("CreeperPlayerLog.showrange")) tabs.add("showrange");
			if (sender.hasPermission("CreeperPlayerLog.reload")) tabs.add("reload");
		}
		return tabs;
	}
	
	@Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      if (args.length == 0) {
        showHelp(sender);
        return true;
      }

      Map<String, String> data = new HashMap<String, String>();
      switch (args[0].toLowerCase()) {
      case "clear":
      	if (!(sender instanceof Player)) {
      		if (args.length == 1) {
      			plugin.msg("help-clear-console", sender);
      			return true;
      		}
      		switch (args[1].toLowerCase()) {
      		case "all":
      			data.put("mode", "all");
      			break;
      		case "world":
      			if (args.length < 3) {
      				plugin.msg("help-clear-console", sender);
      				return true;
      			}
      			data.put("world", args[2]);
      			data.put("mode", "world");
      			break;
    			default:
      			plugin.msg("help-clear-console", sender);
      			return true;
      		}
    			plugin.clearData(sender, args, data);
      		return true;
      	}
      	if (!sender.hasPermission("CreeperPlayerLog.clear")) {
      		plugin.msg("access-denied", sender);
      		return true;
      	}
      	plugin.clearData(sender, args, data);
        break;
        
      case "radius":
      	if (!sender.hasPermission("CreeperPlayerLog.radius")) {
      		plugin.msg("access-denied", sender);
      		return true;
      	}
      	plugin.setRadius(sender, args, data);
        break;
        
      case "showrange":
      	if (args.length < 1) {
      		showHelp(sender);
      		return true;
      	}
      	if (!sender.hasPermission("CreeperPlayerLog.showrange")) {
      		plugin.msg("access-denied", sender);
      		return true;
      	}
      	plugin.setShowRange(sender, args, data);
        break;

      case "reload":
      	plugin.reloadConfig();
      	plugin.msg("reloaded", sender);
      	break;
      	
      case "show":
      	if (!(sender instanceof Player)) {
      		plugin.msg("no-console", sender);
      		return true;
      	}
      	if (!sender.hasPermission("CreeperPlayerLog.show")) {
      		plugin.msg("access-denied", sender);
      		return true;
      	}
      	plugin.showData(sender, args, data);
      	break;

      default:
    		showHelp(sender);
    		break;
      }

      return true;
    } catch (Exception ex) {
      return false;
    }
  }

	void showHelp(CommandSender sender) {
    int displayed = 0;
		if (sender instanceof Player && sender.hasPermission("CreeperPlayerLog.show")) { plugin.msg("help-show", sender); displayed++; }
		if (sender.hasPermission("CreeperPlayerLog.radius")) { plugin.msg("help-radius", sender); displayed++; }
		if (sender.hasPermission("CreeperPlayerLog.showrange")) { plugin.msg("help-showrange", sender); displayed++; }
		if (sender.hasPermission("CreeperPlayerLog.reload")) { plugin.msg("help-reload", sender); displayed++; }
		if (sender.hasPermission("CreeperPlayerLog.clear")) { plugin.msg("help-clear" + ((sender instanceof Player)?"":"-console"), sender); displayed++; }
		if (displayed == 0) plugin.msg("access-denied", sender);
  }
}
