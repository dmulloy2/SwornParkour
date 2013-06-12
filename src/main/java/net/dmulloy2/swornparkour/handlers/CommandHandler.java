package net.dmulloy2.swornparkour.handlers;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.commands.CmdHelp;
import net.dmulloy2.swornparkour.commands.SwornParkourCommand;
import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor 
{
	private final SwornParkour plugin;

	private List<SwornParkourCommand> registeredCommands;
	
	public CommandHandler(SwornParkour plugin)
	{
		this.plugin = plugin;
		registeredCommands = new ArrayList<SwornParkourCommand>();
	}
	
	public void registerCommand(SwornParkourCommand command) 
	{
		registeredCommands.add(command);
	}

	public List<SwornParkourCommand> getRegisteredCommands()
	{
		return registeredCommands;
	}

	public void setCommandPrefix(String commandPrefix)
	{
		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{	
		List<String> argsList = new ArrayList<String>();
		
		if (args.length > 0)
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);
			
			for (SwornParkourCommand command : registeredCommands) 
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}
			
			sender.sendMessage(FormatUtil.format("&4Error: &cUnknown command \"{0}\"! Try /parkour help!", commandName));
		}
		else 
		{
			new CmdHelp(plugin).execute(sender, args);
		}
		
		return true;
	}
	
}
