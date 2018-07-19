package steven.Vitals.combo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	public static Chat chat = null;
	private Commands commands = new Commands();
	public Economy economy = null;
	public ConfigManager cfgm;
	public Guilds guild;
	public GuildUtils gUtil;
	public ChatAndHud ChatHud;
	public GenUtils utils;
	private int Ticks = 0;
	
	public void onEnable() {
		getCommand(commands.cmd1).setExecutor(commands);
		getCommand(commands.cmd2).setExecutor(commands);
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
	
	public void ChatAndHud() {
		ChatHud = new ChatAndHud();
	}
	
	public void generalUtils() {
		utils = new GenUtils();
	}
	
	public void timer() {
		new BukkitRunnable() {

			@Override
			public void run() {
				
				Ticks++;
				
				if (Ticks % 20 == 0) {   //every 1 seconds
					if (getConfig().getBoolean("Sidebar")) {
						ChatHud.updater();
					}
				}
				else if (Ticks == 2000) { //every 100 seconds and reset timer
					Ticks = 0;
				}				
			}
		}.runTaskTimerAsynchronously(this, 0, 1);
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
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		
		return (WorldGuardPlugin) plugin;
	}
}

