package steven.Vitals.combo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class GenUtils implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public static Boolean isNight = false, firstRun = true;
	static List<UUID> track = new ArrayList<>();
	
	public boolean isWithinRegion(Location loc, String region) {
	    WorldGuardPlugin guard = plugin.getWorldGuard();
	    Vector v = BukkitUtil.toVector(loc);
	    RegionManager manager = guard.getRegionManager(loc.getWorld());
	    ApplicableRegionSet set = manager.getApplicableRegions(v);
	    for (ProtectedRegion each : set)
	        if (each.getId().equalsIgnoreCase(region))
	            return true;
	    return false;
	}
	
	public String isInGuild(Player player) {
		
		List<String> guildRegions = plugin.getConfig().getStringList("guild_regions");
		
		for (String s : guildRegions) {
			if (isWithinRegion(player.getLocation(), s)) {
				return s;
			}
		}
		return "Unknown";
	}
	
	public void emmyRemove() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (plugin.emmy.contains(player.getUniqueId())) {
				if (player.getInventory().contains(Material.EMERALD)) {
					for (ItemStack item : player.getInventory().getContents()) {
						if (item != null && item.getType().equals(Material.EMERALD)) {
							item.setAmount(item.getAmount() - 1);
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2), true);
							return;
						}
					}
				}
				else {
					plugin.emmy.remove(player.getUniqueId());
					player.removePotionEffect(PotionEffectType.SPEED);
					return;
				}
			}
		}
	}
	
	//this can be used to stop things when there is no players on
	//good for keeping console clean
	public boolean players() {
		if (Bukkit.getServer().getOnlinePlayers().size() == 0)
			return false;
		else
			return true;
	}
	
	public void giveItem(Player player, ItemStack aItem, ItemMeta meta) {
		ItemStack Item = new ItemStack(aItem.getType(), aItem.getAmount(), aItem.getDurability());
		Item.setItemMeta(meta);
		player.getInventory().addItem(Item);
	}
	
	public void buyRank(Player player) {
		ChatAndHud ChatHud = new ChatAndHud();
		String test = ChatHud.getGroup(player);
		double balance = plugin.economy.getBalance(player);
		switch(test) {
			case "Wanderer":
				player.sendMessage(ChatColor.RED + "You do not rank this way.");
				break;
			case "Citizen":
				if(balance >= 10000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Noble!");
					plugin.economy.withdrawPlayer(player, 10000);
					plugin.permission.playerAddGroup("6d7", (OfflinePlayer)player, "Noble");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $10,000 to rank up.");
				}
				return;
			case "Noble":
				if(plugin.economy.getBalance(player) >= 25000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Merchant!");
					plugin.economy.withdrawPlayer(player, 25000);
					plugin.permission.playerAddGroup(player, "Merchant");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $25,000 to rank up.");
				}
				return;
			case "Merchant":
				if(plugin.economy.getBalance(player) >= 50000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Knight!");
					plugin.economy.withdrawPlayer(player, 50000);
					plugin.permission.playerAddGroup(player, "Knight");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $50,000 to rank up.");
				}
				return;
			case "Knight":
				if(plugin.economy.getBalance(player) >= 100000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Baron!");
					plugin.economy.withdrawPlayer(player, 100000);
					plugin.permission.playerAddGroup(player, "Baron");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $100,000 to rank up.");
				}
				return;
			case "Baron":
				if(plugin.economy.getBalance(player) >= 250000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Duke!");
					plugin.economy.withdrawPlayer(player, 250000);
					plugin.permission.playerAddGroup(player, "Duke");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $250,000 to rank up.");
				}
				return;
			case "Duke":
				if(plugin.economy.getBalance(player) >= 500000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Chancellor!");
					plugin.economy.withdrawPlayer(player, 500000);
					plugin.permission.playerAddGroup(player, "Chancellor");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $500,000 to rank up.");
				}
				return;
			case "Chancellor":
				if(plugin.economy.getBalance(player) >= 1000000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Viceroy!");
					plugin.economy.withdrawPlayer(player, 1000000);
					plugin.permission.playerAddGroup(player, "Viceroy");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $1,000,000 to rank up.");
				}
				return;
			case "Viceroy":
				if(plugin.economy.getBalance(player) >= 2500000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Guardian!");
					plugin.economy.withdrawPlayer(player, 2500000);
					plugin.permission.playerAddGroup(player, "Guardian");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $2,500,000 to rank up.");
				}
				return;
			case "Guardian":
				if(plugin.economy.getBalance(player) >= 10000000) {
					player.sendMessage(ChatColor.GOLD + "You are now a Avatar!");
					plugin.economy.withdrawPlayer(player, 10000000);
					plugin.permission.playerAddGroup(player, "Avatar");
				}
				else {
					player.sendMessage(ChatColor.RED + "You do not have enough money to rank up. You need $10,000,000 to rank up.");
				}
				return;
			case "Avatar":
				player.sendMessage(ChatColor.GOLD + "Your are the highest rank!");
				return;
			default:
				player.sendMessage("Error");
				break;
		}
		return;		
	}
	
	public void reloadAllCfg() {
		//plugin.cfgm.reloadPlayers();
		plugin.reloadConfig();
		//plugin.cfgm.reloadPlaytime();
	}
	
	public void getPlayTime(Player player) {
		int hr, min, sec, time;
		String comb = null;
		if (plugin.cfgm.getPlayerTime().contains(player.getUniqueId().toString())) {
			time = plugin.cfgm.getPlayerTime().getInt(player.getUniqueId().toString() + ".playTime");
			hr = time / 3600;
			min = (time % 3600) / 60;
			sec = time % 60;
			comb = String.format("%d:%2d:%2d", hr, min, sec);
			player.sendMessage(ChatColor.GOLD + "Your current playtime is: " + comb);
		}
		else {
			player.sendMessage(ChatColor.RED + "Unfortunately you were not found in out player time file please report this to the admins.");
		}
	}
	
	public void featherRemove() {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("essentials.fly")) {
				boolean hasFeather = false;
				int featherSlot = 0;
				for (int i = 0; i < 9; i++) {
					ItemStack held = player.getInventory().getItem(i);
					if (held != null) {
						if (held.getType() == Material.FEATHER) {
							featherSlot = i;
							hasFeather = true;
						}
					}
				}
				if (hasFeather) {
					ItemStack held = player.getInventory().getItem(featherSlot);
					if (player.isFlying()) {
						int amountLeft = held.getAmount() - 1;
						if (amountLeft == 1)
							player.sendMessage(ChatColor.BLUE + "You have one feather left, land soon to avoid damage.");
						else if (amountLeft == 0)
							player.sendMessage(ChatColor.RED + "You have no feathers left.");
						held.setAmount(amountLeft);
						return;
					}
					if (player.getAllowFlight() == true && player.isFlying() == false) {
						player.setAllowFlight(false);
						player.sendMessage(ChatColor.BLUE + "FeatherFlight has been cancled.");
						return;
					}
				}
				if (!hasFeather && player.isFlying()) {
					player.sendMessage(ChatColor.RED + "You have no more feathers and can no longer fly!");
					player.setFlying(false);
					player.setAllowFlight(false);
					EventsClass.safeFall.add(player.getUniqueId());
				}
				else
					return;
			}
		}
	}
	
	public void sunHandler() {
		
		long worldTime = Bukkit.getServer().getWorld("6d7").getTime();
		Boolean fire1 = false, fire2 = false, fire3 = false, fire4 = false;
		
		if (worldTime == 0) {
			if (players()) {
				if (fire1 == false) {
					fire1 = true;
					fire4 = false;
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Worldtime] " + ChatColor.AQUA + "A new day has started.");
					NightReward();
				}
			}
				isNight = false;
				firstRun = true;
				return;
		}
		if (worldTime == 12000) {
			if (players() ) {
				if (fire2 == false) {
					fire2 = true;
					fire1 = false;
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Worldtime] " + ChatColor.AQUA + "Darkness begins to fall.");
				}
				return;
			}
		}
		if (worldTime == 13000) {
			if (players()) {
				if (fire3 == false) {
					fire3 = true;
					fire2 = false;
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Worldtime] " + ChatColor.AQUA + "Night time has fallen.");
				}
			}
				isNight = true;
				return;
		}
		if (worldTime == 23000) {
			if (players()) {
				if (fire4 == false) {
					fire4 = true;
					fire1 = false;
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Worldtime] " + ChatColor.AQUA + "The Suns light starts to become visable.");
				}
				return;
			}
		}
		else
			return;
	}

	private void NightReward() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (track.contains(player.getUniqueId())) {
				plugin.economy.depositPlayer(player, 500);
				player.sendMessage(ChatColor.GREEN + "You have survived the night and been awarded $500.");
			}
		}
		return;
	}

	public void deathTracker(Player dead) {
		if (isNight) {
			if (firstRun) {
				firstRun = false;
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getGameMode() == GameMode.SURVIVAL) {
						player.sendMessage(ChatColor.GOLD + "Survive the night to get a reward.");
						track.add(player.getUniqueId());
					}
				}
				return;
			}
			else if (dead != null) {
				if (track.contains(dead.getUniqueId()) && dead.getUniqueId() != null) {
					track.remove(dead.getUniqueId());
				}
			}
		}
		else
			return;
	}
	
	public String time(String world) {
		long time = Bukkit.getServer().getWorld(world).getFullTime();
		int hours = (int)((time/1000+8)%24);
		int minutes = (int)(60*(time%1000)/1000);
		return String.format("%02d:%02d", hours, minutes);
	}

}
