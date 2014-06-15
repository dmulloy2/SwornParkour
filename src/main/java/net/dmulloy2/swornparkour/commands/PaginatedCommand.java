package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;

/**
 * @author dmulloy2
 */

public abstract class PaginatedCommand extends net.dmulloy2.commands.PaginatedCommand
{
	protected final SwornParkour plugin;
	public PaginatedCommand(SwornParkour plugin)
	{
		super(plugin);
		this.plugin = plugin;
	}
}