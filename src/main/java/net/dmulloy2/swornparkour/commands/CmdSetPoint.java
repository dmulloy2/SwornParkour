package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourCreator;
import net.dmulloy2.swornparkour.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdSetPoint extends SwornParkourCommand
{
	public CmdSetPoint(SwornParkour plugin)
	{
		super(plugin);
		this.name = "setpoint";
		this.aliases.add("sp");
		this.description = "Sets a point in a parkour arena";
		this.permission = Permission.CMD_SETPOINT;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (getManager().isCreatingArena(player))
		{
			ParkourCreator creator = getManager().getParkourCreator(player);
			creator.setPoint(creator.getStep());
		}
		else
		{
			sendMessage("&cError, you are not creating an arena!");
		}
	}
}