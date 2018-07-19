package steven.Vitals.combo;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EventsClass implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!(plugin.cfgm.getPlayersGuilds().contains(player.getUniqueId().toString()))) {
			List<String> guildRegions = plugin.getConfig().getStringList("guild_regions");
			for (String s : guildRegions) {
				String name = plugin.gUtil.cfgGuild(s);
				plugin.cfgm.getPlayersGuilds().set(player.getUniqueId().toString() + "." + name, 0);
			}
		}
		plugin.cfgm.savePlayersGuilds();;
	}

	@EventHandler
	public void onPlayerClickyEvent(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		String guild = plugin.isInGuild(player);
		
		if (!guild.equals("Unknown")) {
			if (guild.equals("brewingguildzone")) {
				if (entity instanceof Witch) {
					Inv I = new Inv();

					I.GShopInventory(player, guild);

					return;
				}
			}
			else {
				if (entity instanceof Villager) {
					Inv I = new Inv();

					I.GShopInventory(player, guild);

					return;
				}
			}
		}
		else
			return;
	}

	@EventHandler
	public void InvClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		String guild = plugin.isInGuild(player);
		String name = plugin.gUtil.readableGuild(guild);
		Material clickable = plugin.gUtil.zoneItem(guild);

		Inventory open = event.getInventory();
		ItemStack item = event.getCurrentItem();

		if (open.getName().equals(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', name)))) {
			if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
				if (item.getType() == Material.STAINED_GLASS_PANE) {
					event.setCancelled(true);
					return;
				}
				else if (item.getType() == clickable || item.getType() == Material.WRITTEN_BOOK && item.getAmount() == 1) {
					if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Tribute")) {
						event.setCancelled(true);
						plugin.gUtil.Tribute(player, guild);
						plugin.gUtil.nextRank(player, guild);
					}
					else if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Next rank information")) {
						event.setCancelled(true);
						plugin.gUtil.nextRank(player, guild);
					}
					else {
						return;
					}
				}
				else {
					return;
				}
			} else
				return;
		}
		else if (open.getName().equals(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', name)) + " Shop")) {
			if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
				if (item.getType() == Material.STAINED_GLASS_PANE) {
					event.setCancelled(true);
					return;
				}
				else if (item.getType() == clickable && item.getAmount() == 1) {
					if (item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Test")) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.GOLD + "You now have this item");
						plugin.economy.withdrawPlayer(player, 200);
						player.getInventory().addItem(new ItemStack(clickable, 1));
					}
					else {
						return;
					}
				}
				else {
					return;
				}
			}
			else
				return;
		}
		else
			return;
	}

	@EventHandler
	public void autoCook(PlayerFishEvent event) {
		Player player = event.getPlayer();
		Random randomNum = new Random();
		int fishProb = randomNum.nextInt(100) + 1;
		int rank = plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Fishing");

		if (rank == 30) {
			if (event.getState() == State.CAUGHT_FISH) {
				if (fishProb <= 60) {
					ItemStack cookedFish = new ItemStack(Material.COOKED_FISH, 1);
					event.setCancelled(true);
					player.getInventory().addItem(cookedFish);
					event.getHook().remove();
				}
				else if (fishProb <= 76 && !(fishProb <= 60)) {
					ItemStack cookedFish = new ItemStack(Material.COOKED_FISH, 1, (byte) 1);
					event.setCancelled(true);
					player.getInventory().addItem(cookedFish);
					event.getHook().remove();
				}
				else
					return;
			}
		}
		else
			return;
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void fishBait(PlayerFishEvent event) {
		Player player = event.getPlayer();
		Material bait = Material.getMaterial(plugin.getConfig().getString("bait_item"));
		int count = 0, slot = 0, total = 0;
		boolean bothSides = false;
		
		if (plugin.getConfig().getBoolean("Fishing_bait")) {
			if (event.getState() == State.FISHING) {
				for (int i = 0; i < 9; i++) {
					if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == Material.FISHING_ROD) {
						slot = i;
						break;
					}
				}
				if ((slot+1) < 9 && player.getInventory().getItem(slot+1) != null && player.getInventory().getItem(slot+1).getType() == bait) {
					count = player.getInventory().getItem(slot+1).getAmount();
					if ((slot-1) >= 0 && player.getInventory().getItem(slot-1) != null && player.getInventory().getItem(slot-1).getType() == bait) {
						total = count + player.getInventory().getItem(slot-1).getAmount() - 1;
						bothSides = true;
					}
					if (total == 0)
						total = count - 1;
					plugin.gUtil.takeBait(slot+1, count, total, player, bait, bothSides);
					return;
				}
				else if ((slot-1) >= 0 && player.getInventory().getItem(slot-1) != null && player.getInventory().getItem(slot-1).getType() == bait) {
					count = player.getInventory().getItem(slot-1).getAmount();
					total = count - 1;
					plugin.gUtil.takeBait(slot-1, count, total, player, bait, false);
					return;
				}
				else {
					if (player.getInventory().contains(bait)) {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You must have the bait next to your fishing rod on your hotbar.");
						event.setCancelled(true);
						return;
					}
					else
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You must have bait to fish.");
					event.setCancelled(true);
					return;
				}
			}
		}
		else
			return;
	}
	
	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent event) {
		if (plugin.getConfig().getBoolean("chatformat")) {
			Player player = event.getPlayer();
			if (player.hasPermission("wanderer.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.GRAY + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("citizen.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.WHITE + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("noble.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.YELLOW + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("merchant.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.GOLD + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("knight.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.DARK_GREEN + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("baron.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.GREEN + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("duke.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.DARK_PURPLE + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("chancellor.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.DARK_AQUA + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("viceroy.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.AQUA + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("guardian.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.BLUE + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
			else if (player.hasPermission("avatar.v")) {
				event.setFormat(plugin.ChatHud.supergroupTag(player) + ChatColor.DARK_RED + plugin.ChatHud.getGroup(player) + " " + event.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + plugin.ChatHud.unicize(event.getMessage()));
			}
		}
		else
			return;		
	}
	
}
