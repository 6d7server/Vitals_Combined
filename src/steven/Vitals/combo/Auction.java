package steven.Vitals.combo;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Auction implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);

	static boolean auctionRunning = false;
	private int ticksLeft = 0;
	public int startamount;
	public int currentbid;
	public int bidcount = 0;
	public UUID startUUID, lastbidder, auctioner;
	public String bidstring = " The current bid is: $";
	public String noMoney = "You do not have enough money to bid $";
	public ItemStack AItem;
	public ItemMeta ItemMeta;
	
	public void startAuction(CommandSender sender) {
		
		//get the item held by the player that run startauction
		ItemStack Item = ((Player) sender).getInventory().getItemInMainHand();
		
		//if that item is not air
		if (Item != null && Item.getType() != Material.AIR) {
			double duraPercent = 100 - (100.0 * Item.getDurability() / Item.getType().getMaxDurability());
			String printDura = String.format("%.2f", duraPercent);
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.RED + sender.getName().toString() + ChatColor.YELLOW + " has started an auction for: " + ChatColor.GREEN + Item.getType().toString().replaceAll("_", " ") + " X " + Item.getAmount() + " With " + printDura + "% durability." + ChatColor.YELLOW + " The starting Bid is $" + startamount + "."); //announce the start of the auction with item details
			auctionRunning = true; //allow the announcer to run along with preventing multiple auctions
			startUUID = ((Player) sender).getUniqueId(); //get the id to check against later
			currentbid = startamount; //set current bid to starting amount
			AItem = new ItemStack(Item.getType(), Item.getAmount(), Item.getDurability());
			ItemMeta = Item.getItemMeta();
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You must have an Item in your hand to start an auction!");
			return;
		}
	}

	public void bid(CommandSender sender, int bidamount) {
		
		OfflinePlayer pl = ((Player) sender); //get the player to check if they have bid amount in balance
		
		//if the bidder is not the player that started the auction
		if (((Player) sender).getUniqueId() != startUUID) {
			if (bidcount == 0 && bidamount > startamount) { //if first bid and bid is over starting
				if (plugin.economy.getBalance(pl) >= bidamount) { //check if player has the money
					lastbidder = ((Player) sender).getUniqueId(); //get new bidder id to reference
					currentbid = bidamount; //update current bid
					bidcount++; //mark not first bid
					sender.sendMessage(ChatColor.GOLD + "You are now the highest bidder!");
				}
				else
					sender.sendMessage(ChatColor.RED + noMoney + bidamount + ".");
			}
			//if first bid and amount not over startamount
			else if (bidcount == 0 && !(bidamount > startamount)) {
				sender.sendMessage(ChatColor.RED + "Your bid is not higher then the starting price!");
			}
			//not first bid and amount over current
			else if (bidcount > 0 && bidamount > currentbid) {
				if (plugin.economy.getBalance(pl) >= bidamount) { //player has the money needed to bid
					for (Player players : Bukkit.getServer().getOnlinePlayers()) { //tell last high bid that they are no longer high bid
						if (players.getUniqueId() == lastbidder) {
							players.sendMessage(ChatColor.YELLOW + "You have been outbid!");
						}
					}
					sender.sendMessage(ChatColor.GOLD + "You are now the highest bidder!");
					lastbidder = ((Player) sender).getUniqueId();
					currentbid = bidamount;
				}
				else
					sender.sendMessage(ChatColor.RED + noMoney + bidamount + ".");
			}
			//not first bid and bid not high enough
			else if (bidcount > 0 && !(bidamount > currentbid)) {
				sender.sendMessage(ChatColor.RED + "Your bid is not higher then the current highest bid which is: $" + currentbid);
			}
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You cannot bid on your own item.");
		}
	}
	
	private void auctionend() {
		Player start = null;
		
		if (startamount == currentbid) { //if no bids
			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == startUUID) { //give item back
					plugin.utils.giveItem(player, AItem, ItemMeta);
				}
			}
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "The auction is over and no one bid.");
			return;
		}
		else if (currentbid > startamount) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == startUUID) {
					start = player;
				}
			}
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player.getUniqueId() == lastbidder) {
					OfflinePlayer pl = player;
					player.sendMessage(ChatColor.GREEN + "You have won the auction.");
					plugin.utils.giveItem(player, AItem, ItemMeta); //give item
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "The auction is over and " + player.getName() + " has won the auction for $" + currentbid);
					plugin.economy.withdrawPlayer(pl, currentbid); //take money
					//player.sendMessage(ChatColor.YELLOW + "You paid $" + currentbid + " to " + start.getName() + ".");
					plugin.economy.depositPlayer(start, currentbid);
					//start.sendMessage(ChatColor.YELLOW + "You recieved $" + currentbid + " from " + player.getName() + ".");
					return;
				}
			}
		}
		else //should never run
			Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Error something went wrong.");
	}

	public void timer() {
		if (!(auctionRunning)) {
			ticksLeft = 6000;
		}
		else if (auctionRunning) {
			switch (ticksLeft) {
				case 4800:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 4 minutes left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 3600:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 3 minutes left."+ bidstring + currentbid);
					ticksLeft--;
					break;
				case 2400:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 2 minutes left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 1200:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 1 minute left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 600:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 30 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 300:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 15 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 200:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 10 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 100:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 5 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 80:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 4 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 60:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 3 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 40:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 2 seconds left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 20:
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Auction] " + ChatColor.AQUA + "There is 1 second left." + bidstring + currentbid);
					ticksLeft--;
					break;
				case 0:
					auctionend();
					auctionRunning = false;
					bidcount = 0;
					break;
				default:
					ticksLeft--;
					break;
			}
		}
	}
}

