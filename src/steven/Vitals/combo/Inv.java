package steven.Vitals.combo;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Inv implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	
	public void GuildInventory(Player player, String guild) {
		String name = plugin.gUtil.readableGuild(guild);
		Material item = plugin.gUtil.zoneItem(guild);
		Inventory I = plugin.getServer().createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', name));
		ItemStack a = new ItemStack(item, 1);
		ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemStack b = new ItemStack(Material.WRITTEN_BOOK, 1);
		ItemMeta eMeta = empty.getItemMeta();
		ItemMeta aMeta = a.getItemMeta();
		ItemMeta bMeta = b.getItemMeta();
		aMeta.setDisplayName(ChatColor.GOLD + "Tribute");
		eMeta.setDisplayName(ChatColor.GRAY + " ");
		bMeta.setDisplayName(ChatColor.GOLD + "Next rank information");
		ArrayList<String> lorea = new ArrayList<String>();
		lorea.add("Pay tribute to");
		lorea.add("this guild to");
		lorea.add("join or rank up");
		ArrayList<String> loreb = new ArrayList<String>();
		loreb.add("Check what items");
		loreb.add("and money you");
		loreb.add("need to rank up");
		aMeta.setLore(lorea);
		bMeta.setLore(loreb);
		empty.setItemMeta(eMeta);
		a.setItemMeta(aMeta);
		b.setItemMeta(bMeta);
		
		I.setItem(0, empty);
		I.setItem(1, a);
		I.setItem(2, empty);
		I.setItem(3, b);
		I.setItem(4, empty);
		I.setItem(5, empty);
		I.setItem(6, empty);
		I.setItem(7, empty);
		I.setItem(8, empty);
		
		player.openInventory(I);
	}
	
	public void GShopInventory(Player player, String guild) {
		String name = plugin.gUtil.readableGuild(guild);
		Material item = plugin.gUtil.zoneItem(guild);
		Inventory I = plugin.getServer().createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', name) + " Shop");
		ItemStack a = new ItemStack(item, 1);
		ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		ItemMeta eMeta = empty.getItemMeta();
		ItemMeta aMeta = a.getItemMeta();
		aMeta.setDisplayName(ChatColor.GOLD + "Test");
		eMeta.setDisplayName(ChatColor.GRAY + " ");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Take $200");
		lore.add("Gives guild Item");
		aMeta.setLore(lore);
		empty.setItemMeta(eMeta);
		a.setItemMeta(aMeta);
		
		I.setItem(0, empty);
		I.setItem(1, a);
		I.setItem(2, empty);
		I.setItem(3, empty);
		I.setItem(4, empty);
		I.setItem(5, empty);
		I.setItem(6, empty);
		I.setItem(7, empty);
		I.setItem(8, empty);
		
		player.openInventory(I);
	}
}

