package net.dmulloy2.swornparkour.util;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class Util 
{	
	public static Player matchPlayer(String pl)
	{
		List<Player> players = Bukkit.matchPlayer(pl);
		
		if (players.size() >= 1)
			return players.get(0);
		
		return null;
	}
	
	public static OfflinePlayer matchOfflinePlayer(String pl)
	{
		if (matchPlayer(pl) != null)
			return matchPlayer(pl);
		
		for (OfflinePlayer o : Bukkit.getOfflinePlayers())
		{
			if (o.getName().equalsIgnoreCase(pl))
				return o;
		}
		
		return null;
	}
	
	public static boolean isBanned(OfflinePlayer p)
	{
		for (OfflinePlayer banned : Bukkit.getBannedPlayers()) 
		{
			if (p.getName().equalsIgnoreCase(banned.getName()))
				return true;
		}
		return false;
	}

	public static int random(int x)
	{
		Random rand = new Random();
		return rand.nextInt(x);
	}

	public static double pointDistance(Location loc1, Location loc2)
	{
		int xdist = loc1.getBlockX() - loc2.getBlockX();
		int ydist = loc1.getBlockY() - loc2.getBlockY();
		int zdist = loc2.getBlockY() - loc2.getBlockZ();
		
		return Math.sqrt(xdist * xdist + ydist * ydist + zdist * zdist);
	}
	
	public static boolean checkLocation(Location loc1, Location loc2)
	{
		Block block1 = loc1.getBlock();
		Block block2 = loc2.getBlock();
		
		return (block1.getLocation().equals(block2.getLocation()));
	}
}