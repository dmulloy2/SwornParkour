package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.handlers.ParkourHandler;

/**
 * @author dmulloy2
 */

public abstract class SwornParkourCommand extends Command
{
	protected final SwornParkour plugin;
	protected final ParkourHandler handler;

	public SwornParkourCommand(SwornParkour plugin) 
	{
		super(plugin);
		this.plugin = plugin;
		this.handler = plugin.getParkourHandler();
	}

	protected final String getMessage(String key)
	{
		return plugin.getMessage(key);
	}
}