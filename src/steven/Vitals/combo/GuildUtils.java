package steven.Vitals.combo;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GuildUtils implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	public String cfgItem(String guild) {
		switch (guild) {
			case "Brewing":
				return "Brewing_Amount";
			case "Enchanting":
				return "Enchanting_Amount";
			case "Farming":
				return "Farming_Amount";
			case "Fishing":
				return "Fishing_Amount";
			case "Mining":
				return "Mining_Amount";
			case "Rancher":
				return "Rancher_Amount";
			case "Slayer":
				return "Slayer_Amount";
			case "Woodcutting":
				return "Woodcutting_Amount";
		}
		return null;
	}
	
	public void takeBait(int i, int count, int total, Player player, Material bait, boolean bothSides) {
		if (count > 1) {
			count--;
			player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You cast your line out. You now have " + total + " bait left.");
			player.getInventory().getItem(i).setAmount(count);
		}
		else if (count == 1) {
			count--;
			if (bothSides) {
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You cast your line out. You now have " + player.getInventory().getItem(i-2).getAmount() + " bait left.");
				player.getInventory().getItem(i).setAmount(count);
			}
			else {
				player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "You cast your line out. You have no more bait!");
				player.getInventory().getItem(i).setAmount(count);
			}
		}
	}
	
	public void nextRank(Player player, String guildzone) {
		String guild = cfgGuild(guildzone);
		int currentRank = plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + guild);
		String item = cfgItem(guild);
		
		if (currentRank == 30) {
			player.sendMessage(ChatColor.GOLD + "You are max level! You cannot pay any more tribute!");
			return;
		}
		
		List<String> materialList = plugin.cfgm.getGuildItems().getStringList(guild);
		List<Integer> amountList = plugin.cfgm.getGuildItems().getIntegerList(item);
		Material material = Material.getMaterial(materialList.get(currentRank));
		int amount = amountList.get(currentRank);
		int totalrank = rankCount(player);
		int numof = guildCount(player);
		int nummax = maxCount(player);
		int cost = tributeCost(totalrank, numof, nummax, currentRank);
		
		player.sendMessage(ChatColor.GOLD + "For you next rank in " + guild + " you need: $" + cost + " and " + material.toString() + " x" + amount);
	}

	public void Tribute(Player player, String guildzone) {
		String guild = cfgGuild(guildzone);
		int currentRank = plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + guild);
		String item = cfgItem(guild);
		
		if (currentRank == 30) {
			player.sendMessage(ChatColor.GOLD + "You are max level! You cannot pay any more tribute!");
			return;
		}
		
		int totalrank = rankCount(player);
		int numof = guildCount(player);
		int nummax = maxCount(player);
		int cost = tributeCost(totalrank, numof, nummax, currentRank);
		double balance = plugin.economy.getBalance(player);
		boolean hasItem = false;
		List<String> materialList = plugin.cfgm.getGuildItems().getStringList(guild);
		List<Integer> amountList = plugin.cfgm.getGuildItems().getIntegerList(item);
		Material material = Material.getMaterial(materialList.get(currentRank));
		int amount = amountList.get(currentRank);
		
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null && i.getType() == material && i.getAmount() >= amount) {
				hasItem = true;
				break;
			}
		}
		
		if (hasItem && balance >= cost) {
			player.sendMessage(ChatColor.GOLD + "Rank up successful! You are now a level " + (currentRank + 1) + " " + guild + "!");
			plugin.economy.withdrawPlayer(player, cost);
			plugin.cfgm.getPlayersGuilds().set(player.getUniqueId().toString() + "." + guild, (currentRank + 1));
			plugin.cfgm.savePlayersGuilds();
			for (ItemStack i : player.getInventory().getContents()) {
				if (i != null && i.getType() == material) {
					i.setAmount(i.getAmount()-amount);
					break;
				}
			}
			return;
		}
		else {
			player.sendMessage(ChatColor.GOLD + "Failed to rank up! Are you sure you have everything?");
			return;
		}
	}

	public int maxCount(Player player) {
		List<Integer> Guilds = new ArrayList<Integer>();
		int count = 0;
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Brewing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Enchanting"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Farming"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Fishing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Mining"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Rancher"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Slayer"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Woodcutting"));

		for (int i : Guilds) {
			if (i == 30) {
				count++;
			}
		}
		return count;
	}

	public int guildCount(Player player) {
		List<Integer> Guilds = new ArrayList<Integer>();
		int count = 0;
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Brewing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Enchanting"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Farming"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Fishing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Mining"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Rancher"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Slayer"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Woodcutting"));

		for (int i : Guilds) {
			if (!(i == 0)) {
				count++;
			}
		}
		return count;
	}

	public int tributeCost(int totalrank, int guildcount, int maxcount, int currentRank) {
		List<Integer> price = plugin.getConfig().getIntegerList("Price_base");
		int mod = plugin.getConfig().getInt("monopoly_modifier");
		int cost = price.get(currentRank);
		
		if ((currentRank + 1) == 30) {
			return (int) (totalrank*(Math.pow(guildcount, 1.2))*cost+(mod*maxcount));
		}
		else {
			return (int) (totalrank*(Math.pow(guildcount, 1.2))*cost);
		}
	}

	public int rankCount(Player player) {
		List<Integer> Guilds = new ArrayList<Integer>();
		int count = 0;
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Brewing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Enchanting"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Farming"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Fishing"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Mining"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Rancher"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Slayer"));
		Guilds.add(plugin.cfgm.getPlayersGuilds().getInt(player.getUniqueId().toString() + "." + "Woodcutting"));

		for (int i : Guilds) {
			count += i;
		}
		return count;
	}

	public String readableGuild(String guild) {
		switch (guild) {
		case "brewingguildzone":
			return "&5&l&nBrewing &5&l&nGuild";
		case "enchantguildzone":
			return "&d&l&nEnchanting &d&l&nGuild";
		case "farmingguildzone":
			return "&2&l&nFarming &2&l&nGuild";
		case "fishingguildzone":
			return "&b&l&nFishing &b&l&nGuild";
		case "miningguildzone":
			return "&7&l&nMining &7&l&nGuild";
		case "rancherguildzone":
			return "&4&l&nRancher &4&l&nGuild";
		case "slayerguildzone":
			return "&8&l&nSlayer &8&l&nGuild";
		case "wcguildzone":
			return "&a&l&nWoodcutting &a&l&nGuild";
		default:
			return "Unknown";
		}
	}

	public Material zoneItem(String guild) {
		switch (guild) {
		case "brewingguildzone":
			return Material.BREWING_STAND;
		case "enchantguildzone":
			return Material.ENCHANTING_TABLE;
		case "farmingguildzone":
			return Material.WHEAT;
		case "fishingguildzone":
			return Material.COD;
		case "miningguildzone":
			return Material.DIAMOND_PICKAXE;
		case "rancherguildzone":
			return Material.BEEF;
		case "slayerguildzone":
			return Material.ROTTEN_FLESH;
		case "wcguildzone":
			return Material.DIAMOND_AXE;
		default:
			return Material.STICK;
		}
	}

	public String cfgGuild(String guild) {
		switch (guild) {
		case "brewingguildzone":
			return "Brewing";
		case "enchantguildzone":
			return "Enchanting";
		case "farmingguildzone":
			return "Farming";
		case "fishingguildzone":
			return "Fishing";
		case "miningguildzone":
			return "Mining";
		case "rancherguildzone":
			return "Rancher";
		case "slayerguildzone":
			return "Slayer";
		case "wcguildzone":
			return "Woodcutting";
		default:
			return "Unknown";
		}
	}

}
