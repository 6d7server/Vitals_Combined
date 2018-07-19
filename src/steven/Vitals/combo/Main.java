package steven.Vitals.combo;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	public static Chat chat = null;
	private Commands commands = new Commands();
	public Economy economy = null;
	public ConfigManager cfgm;
	public Guilds guild;
	public GuildUtils gUtil;
	
	//test
	
	public void onEnable() {
		getCommand(commands.cmd1).setExecutor(commands);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Vitals Guilds has be enabled.");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		loadConfig();
		loadConfigManager();
		setupChat();
		setupEconomy();
		getWorldGuard();
	}
	
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "Vitals Guilds has be disabled.");		
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void loadConfigManager() {
		cfgm = new ConfigManager();
		cfgm.setupPlayersGuilds();
		cfgm.savePlayersGuilds();
		cfgm.reloadPlayersGuilds();
		cfgm.setupGuildItem();
		cfgm.saveGuildItems();
		cfgm.reloadGuildItems();
	}
	
	public void guildLogic() {
		guild = new Guilds();
	}
	
	public void guildUtils() {
		gUtil = new GuildUtils();
	}
	
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatprovider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatprovider != null) {
			chat = chatprovider.getProvider();
		}
		return (chat != null);	
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyprovider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyprovider != null) {
			economy = economyprovider.getProvider();
		}
		return (economy != null);
	}
	
	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		
		return (WorldGuardPlugin) plugin;
	}
	
	public boolean isWithinRegion(Location loc, String region) {
	    WorldGuardPlugin guard = getWorldGuard();
	    Vector v = BukkitUtil.toVector(loc);
	    RegionManager manager = guard.getRegionManager(loc.getWorld());
	    ApplicableRegionSet set = manager.getApplicableRegions(v);
	    for (ProtectedRegion each : set)
	        if (each.getId().equalsIgnoreCase(region))
	            return true;
	    return false;
	}
	
	public String isInGuild(Player player) {
		
		List<String> guildRegions = getConfig().getStringList("guild_regions");
		
		for (String s : guildRegions) {
			if (isWithinRegion(player.getLocation(), s)) {
				return s;
			}
		}
		return "Unknown";
	}
}

