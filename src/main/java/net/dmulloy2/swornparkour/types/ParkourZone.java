package net.dmulloy2.swornparkour.types;

import lombok.Data;
import net.dmulloy2.swornparkour.SwornParkour;

import org.bukkit.Location;

/**
 * @author dmulloy2
 */

@Data
public class ParkourZone 
{
	private ParkourField field;
	private Location spawn;
	private Location end;
	private Location check1;
	private Location check2;
	private int timesPlayed;
	private int gameId;

	private final SwornParkour plugin;
	public ParkourZone(SwornParkour plugin, int gameId)
	{
		this.plugin = plugin;
		this.gameId = gameId;
	}
	
	public void save() 
	{
		plugin.getFileHandler().save(this);
	}
}