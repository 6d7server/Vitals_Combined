package steven.Vitals.combo;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class GenUtils implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	
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

}
