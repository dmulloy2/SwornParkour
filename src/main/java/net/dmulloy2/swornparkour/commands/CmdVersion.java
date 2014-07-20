package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;

/**
 * @author dmulloy2
 */

public class CmdVersion extends SwornParkourCommand
{
	public CmdVersion(SwornParkour plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Display version info";
	}

	@Override
	public void perform()
	{
		sendMessage("&3====[ &eSwornParkour &3]====");
		sendMessage("&bVersion&e: {0}", plugin.getDescription().getVersion());
		sendMessage("&bAuthor&e: dmulloy2");
		sendMessage("&bIssues&e: https://github.com/dmulloy2/SwornParkour/issues");
	}
}