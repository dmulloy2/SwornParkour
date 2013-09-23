package net.dmulloy2.swornparkour.types;

import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class ParkourField
{
	private @Getter int minx;
	private @Getter int miny;
	private @Getter int minz;
	
	private @Getter int maxx;
	private @Getter int maxy;
	private @Getter int maxz;
	
	private @Getter World world;
	
	public ParkourField(Location min, Location max, World world)
	{
		this.minx = min.getBlockX();
		this.miny = min.getBlockY();
		this.minz = min.getBlockZ();
		
		this.maxx = max.getBlockX();
		this.maxy = max.getBlockY();
		this.maxz = max.getBlockZ();
		
		this.world = world;
	}
	
	public boolean isInside(Player player)
	{
		return isInside(player.getLocation());
	}

	public boolean isInside(Location loc) 
	{
		if (loc.getBlockX() >= minx && loc.getBlockX() <= maxx)
		{
			if (loc.getBlockY() >= miny && loc.getBlockY() <= maxy)
			{
				if (loc.getBlockZ() >= minz && loc.getBlockZ() <= maxz)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}