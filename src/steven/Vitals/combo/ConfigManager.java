package steven.Vitals.combo;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class ConfigManager implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	//files and configs here
	public FileConfiguration playerGuildData;
	public File playersGuilds;
	
	public FileConfiguration guildItemsData;
	public File guildItems;
	
	public void setupPlayersGuilds() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		playersGuilds = new File(plugin.getDataFolder(), "playersGuilds.yml");
		
		if (!playersGuilds.exists()) {
			try {
				playersGuilds.createNewFile();
			}
			catch(IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create the playersGuilds.yml file");
			}
		}
		
		playerGuildData = YamlConfiguration.loadConfiguration(playersGuilds);
	}
	
	public FileConfiguration getPlayersGuilds() {
		return playerGuildData;
	}
	
	public void savePlayersGuilds() {
		try {
			playerGuildData.save(playersGuilds);
		}
		catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the players.yml file");
		}
	}
	
	public void reloadPlayersGuilds() {
		playerGuildData = YamlConfiguration.loadConfiguration(playersGuilds);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The players.yml file has been reloaded");
	}
	
	public void setupGuildItem() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		guildItems = new File(plugin.getDataFolder(), "guildItems.yml");
		
		if (!guildItems.exists()) {
			try {
				guildItems.createNewFile();
			}
			catch(IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not create the guildItems.yml file");
			}
		}
		
		guildItemsData = YamlConfiguration.loadConfiguration(guildItems);
	}
	
	public FileConfiguration getGuildItems() {
		return guildItemsData;
	}
	
	public void saveGuildItems() {
		try {
			guildItemsData.save(guildItems);
		}
		catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the guildItems.yml file");
		}
	}
	
	public void reloadGuildItems() {
		guildItemsData = YamlConfiguration.loadConfiguration(guildItems);
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "The guildItems.yml file has been reloaded");
	}
}