package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourZone;
import net.dmulloy2.swornparkour.types.Permission;

import org.bukkit.Location;

public class CmdSpawn extends SwornParkourCommand
{
	public CmdSpawn(SwornParkour plugin) 
	{
		super(plugin);
		this.name = "spawn";
		this.aliases.add("spawnpoint");
		this.requiredArgs.add("id");
		this.description = "Teleport to the spawn point of an arena";
		this.permission = Permission.CMD_SPAWN;
		
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		int id = argAsInt(0, true);
		if (plugin.loadedArenas.size() < id)
		{
			err("&cNo arena with id {0} exists!", id);
			return;
		}
		
		for (ParkourZone zone : plugin.loadedArenas)
		{
			if (zone.getId() == id)
			{
				Location loc = zone.getSpawn();
				player.teleport(loc.clone().add(0, 2.0D, 0));
				sendMessage("&eYou have been teleported to the spawn of arena {0}", id);
			}
		}
	}
}