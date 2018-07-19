package steven.Vitals.combo;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Guilds implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);

	
	@SuppressWarnings("deprecation")
	public UUID getPlayer(String string) {
		OfflinePlayer seb = Bukkit.getOfflinePlayer(string);
		if (plugin.cfgm.getPlayersGuilds().contains(seb.getUniqueId().toString())) {
			return seb.getUniqueId();
		}
		else {
			return null;
		}
			
	}

	public void printStats(UUID uuid, Player player) {
		player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD  + "" + Bukkit.getOfflinePlayer(uuid).getName().toString()  + ChatColor.GOLD + "" + ChatColor.BOLD + "'s Guild Stats");
		player.sendMessage(ChatColor.DARK_PURPLE + "Brewing Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Brewing")));
		player.sendMessage(ChatColor.DARK_GREEN + "Farming Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Farming")));
		player.sendMessage(ChatColor.GREEN + "Woodcutting Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Woodcutting")));
		player.sendMessage(ChatColor.RED + "Rancher Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Rancher")));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Enchanting Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Enchanting")));
		player.sendMessage(ChatColor.AQUA + "Fishing Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Fishing")));
		player.sendMessage(ChatColor.GRAY + "Mining Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Mining")));
		player.sendMessage(ChatColor.DARK_GRAY + "Slayer Guild" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "Lvl " + ChatColor.translateAlternateColorCodes('&', lvlString(uuid, "Slayer")));
		player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
	}
	
	public void printHelp(Player player) {
		player.sendMessage(ChatColor.GRAY + "-----------------{" + ChatColor.GOLD + ChatColor.BOLD + "Guilds Commands" + ChatColor.GRAY + "}-----------------");
		player.sendMessage(ChatColor.GOLD + "/guild" + ChatColor.WHITE + ": " + ChatColor.BLUE + "Core Guilds command. Opens Guild Tribute panel if inside a guildroom, else it calls /guild help.");
		player.sendMessage("");
		player.sendMessage(ChatColor.GOLD + "/guild help" + ChatColor.WHITE + ": " + ChatColor.BLUE + "Displays all Guild commands.");
		player.sendMessage("");
		player.sendMessage(ChatColor.GOLD + "/guild reload" + ChatColor.WHITE + ": " + ChatColor.BLUE + "Reloads players.yml and config.yml");
		player.sendMessage("");
		player.sendMessage(ChatColor.GOLD + "/guild stats <player>" + ChatColor.WHITE + ": " + ChatColor.BLUE + "Displays a player's levels in all guilds. If no player given, defaults to the player who calls the command.");
		player.sendMessage(ChatColor.GRAY + "----------------------------------------------------");
	}

	public String lvlString(UUID uuid, String string) {
		int lvl = plugin.cfgm.getPlayersGuilds().getInt(uuid.toString() + "." + string);
		if (lvl == 30) {
			return "&e&l30/30";
		}
		else {
			String a = String.format("&e%d/30", lvl);
			return a;
		}
	}

}
