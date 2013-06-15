package net.dmulloy2.swornparkour.parkour.objects;

import net.dmulloy2.swornparkour.SwornParkour;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

public class ParkourZone 
{
	private ParkourField field;
	private Location spawn;
	private Location end;
	private Location check1;
	private Location check2;
	private int timesPlayed;
	
	private SwornParkour plugin;
	private int gameId;
	public ParkourZone(SwornParkour plugin, int gameId)
	{
		this.plugin = plugin;
		this.gameId = gameId;
	}
	
	public void save() 
	{
		plugin.getFileHelper().save(this);
	}
	
	public ParkourField getField()
	{
		return field;
	}
	
	public void setField(ParkourField field)
	{
		this.field = field;
	}
	
	public Location getSpawn()
	{
		return spawn;
	}
	
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
	}
	
	public Location getEnd()
	{
		return end;
	}
	
	public void setEnd(Location end)
	{
		this.end = end;
	}
	
	public int getId()
	{
		return gameId;
	}
	
	public void setId(int gameId)
	{
		this.gameId = gameId;
	}
	
	public Location getCheckpoint1()
	{
		return check1;
	}
	
	public void setCheckpoint1(Location check1)
	{
		this.check1 = check1;
	}
	
	public Location getCheckpoint2()
	{
		return check2;
	}
	
	public void setCheckpoint2(Location check2)
	{
		this.check2 = check2;
	}
	
	public int getTimesPlayed()
	{
		return timesPlayed;
	}
	
	public void setTimesPlayed(int timesPlayed)
	{
		this.timesPlayed = timesPlayed;
	}
}