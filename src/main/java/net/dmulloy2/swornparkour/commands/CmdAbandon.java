package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.permissions.Permission;

/**
 * @author dmulloy2
 */

public class CmdAbandon extends SwornParkourCommand
{
	public CmdAbandon(SwornParkour plugin)
	{
		super(plugin);
		this.name = "abandon";
		this.description = "Abandons the creation of an arena";
		this.permission = Permission.CMD_ABANDON;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (!getManager().isCreatingArena(player))
		{
			err("&cYou are not creating an arena!");
			return;
		}
		
		getManager().creators.remove(player);
		
		sendMessage("&cYou have stopped creating the arena!");
	}
}