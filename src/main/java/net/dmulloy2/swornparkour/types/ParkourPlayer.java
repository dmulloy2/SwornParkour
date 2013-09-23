package net.dmulloy2.swornparkour.types;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class ParkourPlayer
{
	private Player player;
	private int points;
	private int gameId;
	private Location spawnBack;
	private int deaths;
	
	private List<Location> clickedBlocks = new ArrayList<Location>();
	
	public SwornParkour plugin;
	public ParkourPlayer(SwornParkour plugin, Player player, int gameId, Location spawnBack)
	{
		this.plugin = plugin;
		this.player = player;
		this.gameId = gameId;
		this.spawnBack = spawnBack;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public List<Location> getClickedBlocks()
	{
		return clickedBlocks;
	}
	
	public void addPoints(int amount)
	{
		points += amount;
	}
	
	public int getGameId()
	{
		return gameId;
	}
	
	public Location getSpawnBack()
	{
		return spawnBack;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	public void onDeath()
	{
		deaths++;
	}
	
	public void addClickedBlock(Location loc)
	{
		clickedBlocks.add(loc);
	}
	
	public void sendMessage(String msg, Object...objects)
	{
		player.sendMessage(FormatUtil.format(msg, objects));
	}
}