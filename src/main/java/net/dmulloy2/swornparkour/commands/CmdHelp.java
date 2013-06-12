package net.dmulloy2.swornparkour.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdHelp extends PaginatedCommand 
{
	public CmdHelp(SwornParkour plugin)
	{
		super(plugin);
		this.name = "help";
		this.description = "Shows " + plugin.getName() + " help.";
		this.optionalArgs.add("page");
		this.linesPerPage = 6;
	}

	@Override
	public int getListSize() 
	{
		return plugin.getCommandHandler().getRegisteredCommands().size();
	}

	@Override
	public String getHeader(int index) 
	{
		return FormatUtil.format(getMessage("help_header"), plugin.getName(), index, getPageCount());
	}

	@Override
	public List<String> getLines(int startIndex, int endIndex) 
	{
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++) 
		{
			SwornParkourCommand command;
			command = plugin.getCommandHandler().getRegisteredCommands().get(i);
			
			if (plugin.getPermissionHandler().hasPermission(sender, permission))
				lines.add(command.getUsageTemplate(true));
		}
		return lines;
	}

	
	@Override
	public String getLine(int index) 
	{
		return null;
	}
}