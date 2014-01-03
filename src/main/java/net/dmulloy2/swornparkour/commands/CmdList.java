package net.dmulloy2.swornparkour.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourGame;
import net.dmulloy2.swornparkour.types.ParkourZone;

/**
 * @author dmulloy2
 */

public class CmdList extends SwornParkourCommand
{
	public CmdList(SwornParkour plugin)
	{
		super(plugin);
		this.name = "list";
		this.aliases.add("ls");
		this.description = "List all available games";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		List<String> lines = new ArrayList<String>();
		
		StringBuilder line = new StringBuilder();
		line.append("&3====[ &eAvailable Arenas &3]====");
		lines.add(line.toString());
		
		for (ParkourZone zone : plugin.getLoadedArenas())
		{
			int id = zone.getId();
			boolean active = false;
			
			for (ParkourGame game : handler.getParkourGames())
			{
				if (game.getId() == id)
				{
					active = true;
				}
			}
			
			line = new StringBuilder();
			line.append("&b[&eGame " + id + "&b]");
			
			line.append("    &ePlayed: &b" + zone.getTimesPlayed());
			
			if (active) line.append("    &4[INGAME]");
			else line.append("     &2[OPEN]");
			
			lines.add(line.toString());
		}
		
		for (String s : lines)
		{
			sendMessage(s);
		}
	}
}