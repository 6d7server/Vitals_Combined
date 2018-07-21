package steven.Vitals.combo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class EventsClass implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public static boolean isChainmail(Material m) { return m == Material.CHAINMAIL_BOOTS || m == Material.CHAINMAIL_CHESTPLATE || m == Material.CHAINMAIL_HELMET || m == Material.CHAINMAIL_LEGGINGS; }
	public static List<UUID> safeFall = new ArrayList<>();
	
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
		plugin.cfgm.savePlayersGuilds();
		
		int veteranTime = plugin.getConfig().getInt("veteran_time");
		int playerTime = 0;
		
		if (playerTime / 3600 >= veteranTime) {
			if (!player.hasPermission("veteran.v")) {
			playerTime = plugin.cfgm.getPlayerTime().getInt(player.getName().toString() + "." + "playtime");
			player.sendMessage(ChatColor.GOLD + "You played on the old 6d7 server for over 50 hours. You are now a Veteran!");
			plugin.permission.playerAddGroup(player, "Veteran");
			}
		}
		
		if (plugin.getConfig().getBoolean("Play_time")) {
			if (!(plugin.cfgm.getPlayerTime().contains(player.getUniqueId().toString()))) {
				plugin.cfgm.getPlayerTime().set(player.getUniqueId().toString() + ".playTime", 0);
			}
			plugin.cfgm.savePlayerTime();
			return;
		}
		else
			return;
	}

	@EventHandler
	public void onPlayerClickyEvent(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		String guild = plugin.utils.isInGuild(player);
		
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

		String guild = plugin.utils.isInGuild(player);
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
	
	@EventHandler
	public void negateFall(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			if (event.getCause() == DamageCause.FALL) {
				Material landingOn = entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
				if (safeFall.contains(entity.getUniqueId())) {
					event.setCancelled(true);
					safeFall.remove(entity.getUniqueId());
				}
				else if (landingOn == Material.SPONGE || landingOn == Material.EMERALD_BLOCK) {
					event.setCancelled(true);
					return;
				}
				else
					return;
			}
			else if (((Player) entity).isFlying() && !((HumanEntity) entity).getGameMode().equals(GameMode.CREATIVE)) {
				((Player) entity).setFlying(false);
				safeFall.add(entity.getUniqueId());
			}
			else
				return;
		}
		else return;
	}
	
	@EventHandler
	public void betterChain(EntityDamageEvent event) {
		
		Entity player = event.getEntity();
		
		if (player instanceof Player) {
			if (player.hasPermission("betterchain.v")) {
				if (plugin.getConfig().getBoolean("betterchain")) {
					int numChainmail = 0;
					double damageFactor = 1;
					
					for (int i = 36; i <= 39; i++) {     //checks armor slots
						if (((Player) player).getInventory().getItem(i) != null) { 	//checks if the slot has an item
							if (isChainmail(((Player) player).getInventory().getItem(i).getType())) { 	//checks if that item is a piece of chain mail armor
								numChainmail++;
								damageFactor *= 0.8;
							}
						}
					}
					if (numChainmail > 0) { 	//if the player has more then one piece of armor
						player.sendMessage(ChatColor.AQUA + "Your chainmail armor reduced the damage taken by " + Math.round(100*(1 - damageFactor)) + "%!");
						event.setDamage((int) Math.round(damageFactor * event.getDamage()));
						player.sendMessage("damageFactor = " + damageFactor);
						return;
					}
					else
						return;
				}
				else
					return;				
			}
			else
				return;
		}
		else
			return;
	}
	
	@EventHandler
	public void emeraldsAndsponges(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		Vector vector = player.getVelocity();
		Material standingOn = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		
		if (plugin.getConfig().getBoolean("Movement")) {
			//SpeedBoost on Emeralds and jump on sponge
			if (standingOn == Material.EMERALD_BLOCK) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 5), true);
				return;
			}
			else if (standingOn == Material.SPONGE) {
				vector.setY(2);
				player.setVelocity(vector);
				return;
			}
		}
		else
			return;
	}
	
	@EventHandler
	public void elevatorUp(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		Material standingOn = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		Location loc = player.getEyeLocation().add(0,1,0);
		
		if (plugin.getConfig().getBoolean("elevator")) {
			if (standingOn == Material.BEDROCK) {
				if ((event.getTo().getY() > event.getFrom().getY()) && player.isFlying() == false) {
					while (loc.getY() < 256) {
						if (loc.getBlock().getType() == Material.BEDROCK) {
							player.teleport(loc.add(0,1,0));
							player.sendMessage("up");
							return;
						}
						loc.add(0,1,0);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void elevatorDown(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();
		Material standingOn = player.getEyeLocation().subtract(0,2,0).getBlock().getType();
		Location loc = player.getEyeLocation().add(0,1,0);
		int lowerLim = plugin.getConfig().getInt("Elevator_Lower");
		
		if (plugin.getConfig().getBoolean("elevator")) {
			if (standingOn == Material.BEDROCK) {
				if (player.isSneaking() && player.isFlying() == false) {
					loc.subtract(0,4,0);
					while (loc.getY() > lowerLim) {
						if (loc.getBlock().getType() == Material.BEDROCK) {
							player.teleport(loc.add(0,1,0));
							player.sendMessage("down");
							return;
						}
						loc.subtract(0,1,0);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Featherfly(PlayerInteractEvent event) {
		
		Action action = event.getAction();
		Player player = event.getPlayer();
		ItemStack held = player.getInventory().getItemInMainHand();
		
		if (player.hasPermission("featherfly.v")) {
			if (plugin.getConfig().getBoolean("FeatherFly")) {
				if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && held.getType() == Material.FEATHER) {
					if (player.isFlying()) {
						player.sendMessage(ChatColor.BLUE + "You are no longer flying.");
						player.setAllowFlight(false);
						player.setFlying(false);
						return;
					}
					if (player.getAllowFlight() && !player.isFlying()) {
						return;
					}
					else if (!player.getAllowFlight()) {
						player.sendMessage(ChatColor.AQUA + "You started to fly.");
						player.setAllowFlight(true);
						player.getLocation().add(0, 2, 0);
						player.setFlying(true);
						held.setAmount(held.getAmount() - 1);
						return;
					}
				}
				else
					return;
			}
			else
				return;
		}
		else
			return;
	}
	
	@EventHandler
	public void blazeCast(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack held = player.getInventory().getItemInMainHand();
		
		if (player.hasPermission("blazecast.v")) {
			if (plugin.getConfig().getBoolean("Blaze_case")) {
				if (action.equals(Action.LEFT_CLICK_AIR) && held.getType() == Material.BLAZE_ROD) {
					player.launchProjectile(Fireball.class).setIsIncendiary(false);
					held.setAmount(held.getAmount() - 1);
					return;
					}
					else
						
						return;
			}
		}
		else
			return;
	}
	
	@EventHandler
	public void consumeEmmy(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack held = player.getInventory().getItemInMainHand();
		
		if (player.hasPermission("consumeemmy.v")) {
			if (plugin.getConfig().getBoolean("Consume_emmy")) {
				if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && held.getType() == Material.EMERALD) {
					if (plugin.emmy.contains(player.getUniqueId())) {
						plugin.emmy.remove(player.getUniqueId());
						return;
					}
					
					else {
						plugin.emmy.add(player.getUniqueId());
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Hasten your way on wind-touched heels!");
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2), true);
						held.setAmount(held.getAmount() - 1);
						return;
					}
				}
			}
		}
	}
	
}
