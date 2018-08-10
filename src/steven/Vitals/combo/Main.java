package steven.Vitals.combo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	
	public Commands commands = new Commands();
	public Chat chat = null;
	public Economy economy = null;
	public Permission permission = null;
	public ConfigManager cfgm;
	public Guilds guild;
	public GuildUtils gUtil;
	public ChatAndHud ChatHud;
	public GenUtils utils;
	public Auction auct;
	public PlayerInfo pInfo;
	private int Ticks = 0;
	public List<UUID> emmy = new ArrayList<>();
	
	public void onEnable() {
		getCommand(commands.cmd1).setExecutor(commands);
		getCommand(commands.cmd2).setExecutor(commands);
		getCommand(commands.cmd3).setExecutor(commands);
		getCommand(commands.cmd4).setExecutor(commands);
		getCommand(commands.cmd5).setExecutor(commands);
		getCommand(commands.cmd6).setExecutor(commands);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Vitals Guilds has be enabled.");
		getServer().getPluginManager().registerEvents(new EventsClass(), this);
		loadConfig();
		loadConfigManager();
		setupChat();
		setupEconomy();
		setupPermissions();
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
	
	public void AuctionHandler() {
		auct = new Auction();
	}
	
	public void playerInfo() {
		pInfo = new PlayerInfo();
	}
	
	public void timer() {
		new BukkitRunnable() {

			@Override
			public void run() {
				
				Ticks++;
				
				if (getConfig().getBoolean("World_time")) {
					utils.sunHandler();
					utils.deathTracker(null);
				}
				auct.timer();
				
				if (Ticks % 20 == 0) {   //every 1 seconds
					if (getConfig().getBoolean("Sidebar")) {
						ChatHud.updater();
					}
					pInfo.playtime();
				}
				if (Ticks % 39 == 0) {	//every 1.9 seconds
					utils.emmyRemove();
				}
				if (Ticks % 200 == 0) { //every 10 seconds
					if (getConfig().getBoolean("FeatherFly"))
						utils.featherRemove();
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
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		
		return (WorldGuardPlugin) plugin;
	}
}

