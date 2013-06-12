package net.dmulloy2.swornparkour.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dmulloy2.swornparkour.ParkourManager;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.permissions.Permission;
import net.dmulloy2.swornparkour.util.FormatUtil;

/**
 * @author dmulloy2
 */

public abstract class SwornParkourCommand 
{
	protected final SwornParkour plugin;
	
	protected CommandSender sender;
	protected Player player;
	protected String args[];
	
	protected String name;
	protected String description;
	protected Permission permission;
	
	protected boolean mustBePlayer;
	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;
		
	public SwornParkourCommand(SwornParkour plugin) 
	{
		this.plugin = plugin;
		requiredArgs = new ArrayList<String>(2);
		optionalArgs = new ArrayList<String>(2);
		aliases = new ArrayList<String>(2);
	}
	
	public abstract void perform();
	
	public final void execute(CommandSender sender, String[] args) 
	{
		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;
		
		if (mustBePlayer && !isPlayer())
		{
			err(plugin.getMessage("error_must_be_player"));
			return;
		}
		
		if (requiredArgs.size() > args.length)
		{
			err(plugin.getMessage("error_arg_count"), getUsageTemplate(false));
			return;
		}
		
		if (hasPermission())
			perform();
		else
			err(plugin.getMessage("error_insufficient_permissions"));
	}
	
	protected final boolean isPlayer() 
	{
		return (player != null);
	}
	
	private final boolean hasPermission()
	{
		return (plugin.getPermissionHandler().hasPermission(sender, permission));
	}
	
	protected final boolean argMatchesAlias(String arg, String... aliases) 
	{
		for (String s : aliases)
			if (arg.equalsIgnoreCase(s))
				return true;
		return false;
	}
	
	protected final void err(String msg, Object... args)
	{
		sendMessage(getMessage("error"), FormatUtil.format(msg, args));
	}
	
	protected final void sendMessage(String msg, Object... args) 
	{
		sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(msg, args));
	}
	
	protected final void sendpMessage(String msg, Object... args) 
	{
		sender.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}
	
	protected final void outConsole(String msg, Object... args)
	{
		plugin.outConsole(msg, args);
	}
	
	protected final String getMessage(String msg)
	{
		return plugin.getMessage(msg);
	}
	
	protected final ParkourManager getManager()
	{
		return plugin.getParkourManager();
	}

	public final String getName() 
	{
		return name;
	}

	public final List<String> getAliases() 
	{
		return aliases;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/parkour ");
				
		ret.append(name);
		
		ret.append("&3 ");
		for (String s : requiredArgs)
			ret.append(String.format("<%s> ", s));
		
		for (String s : optionalArgs)
			ret.append(String.format("[%s] ", s));
		
		if (displayHelp)
			ret.append("&e" + description);
		
		return FormatUtil.format(ret.toString());
	}
	
	protected int argAsInt(int arg, boolean msg)
	{	
		try 
		{
			return Integer.valueOf(args[arg]);
		} 
		catch (NumberFormatException ex) 
		{
			if (msg)
				err(plugin.getMessage("error_invalid_syntax"), getUsageTemplate(false));
			return -1;
		}
	}
	
	protected double argAsDouble(int arg, boolean msg) 
	{
		try 
		{
			return Double.valueOf(args[arg]);
		}
		catch (NumberFormatException ex) 
		{
			if (msg)
				err(plugin.getMessage("error_invalid_syntax"), getUsageTemplate(false));
			return -1;
		}
	}
}