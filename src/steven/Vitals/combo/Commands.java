package steven.Vitals.combo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements Listener, CommandExecutor {
	
	//get config
	private Main plugin;
	
	public Commands(Main pl) {
		plugin = pl;
	}
	
	public String cmd1 = "guild";
	public String cmd2 = "chatsymbol";
	public String cmd3 = "auction"; //contains startauction and bid
	public String cmd4 = "rankup";
	public String cmd5 = "vitals"; //contains reload
	public String cmd6 = "playtime";
	
	public String noPerms = "You do not have permission to run this command.";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase(cmd1)) {
				Guilds guild = new Guilds();
				if (args.length >= 1) {
					switch(args[0]) {
						case "help":
							guild.printHelp((Player) sender);
							return true;
						case "reload":
							sender.sendMessage(ChatColor.BLUE + "Reloading players.yml & config.yml...");
							plugin.cfgm.reloadPlayersGuilds();
							plugin.cfgm.reloadGuildItems();
							plugin.reloadConfig();
							sender.sendMessage(ChatColor.BLUE + "Done!");
							return true;
						case "stats":
							if (args.length == 2) {
								guild.printStats(guild.getPlayer(args[1]), (Player) sender);
							}
							else
								guild.printStats(((Player) sender).getUniqueId(), (Player) sender);
							return true;
					}
				}
				else {
					GenUtils utils = new GenUtils();
					String guilds = utils.isInGuild((Player) sender);
					if (!guilds.equals("Unknown")) {
						Inv I = new Inv();
						
						I.GuildInventory((Player) sender, guilds);
						
						return true;
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase(cmd2)) {
				if (plugin.getConfig().getBoolean("chatsymbols")) {
					Player player = (Player)sender;
					ChatAndHud ChatHud = new ChatAndHud();
					if (player.hasPermission("chatsymbol.v")) {
						player.sendMessage(ChatColor.GREEN + "[Avaliable Unicodes]");
				    	player.sendMessage(ChatHud.unicodeList());
				    	return true;
					}
					else
						player.sendMessage(ChatColor.RED + plugin.getConfig().getString("no_perms_cmd"));
					return true;
				}
				else {
					sender.sendMessage(ChatColor.RED + "Chat Symbols is not enabled.");
					return true;
				}
			}
			else if (cmd.getName().equalsIgnoreCase(cmd3)) {
				if (plugin.getConfig().getBoolean("auctions")) {
					Auction auct = new Auction();
					if (args.length >= 1) {
						switch(args[0]) {
							case "start":
								if (sender.hasPermission("auctionstart.v")) {
									if (!(Auction.auctionRunning)) {
										if (args.length == 1) {
											sender.sendMessage(ChatColor.RED + "You must have a starting amount after the command!");
											return true;
										}
										else if (args.length == 2) {
											auct.startamount = Integer.parseInt(args[0]);
											auct.startAuction(sender);
											((Player) sender).getInventory().getItemInMainHand().setAmount(0);
											return true;
										}
									}
									else {
										sender.sendMessage(ChatColor.RED + "There is currently an active auction, please wait until the auction ends.");
									}
								}
								else {
									sender.sendMessage(ChatColor.RED + noPerms);
								}
							case "bid":
								if (sender.hasPermission("auctionbid.v")) {
									if (!(Auction.auctionRunning)) {
										sender.sendMessage(ChatColor.RED + "There is no active auction to bid on.");
										return true;
									}
									else if (args.length == 1) {
										sender.sendMessage(ChatColor.RED + "You must have a bit amount after the command!");
										return true;
									}
									else if (args.length == 2) {
										int bidamount = Integer.parseInt(args[0]);
										auct.bid(sender, bidamount);
										return true;
									}
								}
								else
									sender.sendMessage(ChatColor.RED + noPerms);
								return true;
						}
					}
				}
			}
			else if (cmd.getName().equalsIgnoreCase(cmd4)) {
				if (plugin.getConfig().getBoolean("Buy_rank")) {
					Player send = (Player)sender;
					GenUtils utils = new GenUtils();
					utils.buyRank(send);
					return true;
				}
				else
					return true;
			}
			else if (cmd.getName().equalsIgnoreCase(cmd5)) {
				if (sender.hasPermission("vitalsOP.v")) {
					GenUtils utils = new GenUtils();
					utils.reloadAllCfg();
					return true;
				}
				else
					return true;
			}
			else if (cmd.getName().equalsIgnoreCase(cmd6)) {
				GenUtils utils = new GenUtils();
				utils.getPlayTime((Player) sender);
				return true;
			}
		}
		
		else {
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		return false;		
	}


}
