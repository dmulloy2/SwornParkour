package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdReload extends SwornParkourCommand 
{
	public CmdReload(SwornParkour plugin) 
	{
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = "Reloads the configuration";
		this.permission = Permission.CMD_RELOAD;
		
		this.mustBePlayer = false;
	}

	@Override
	public void perform()
	{
		plugin.reload();
		sendpMessage(getMessage("reload"));
		outConsole(getMessage("log_reload"));
	}
}