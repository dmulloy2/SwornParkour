package net.dmulloy2.swornparkour.util;

import java.text.MessageFormat;

import org.bukkit.ChatColor;

/**
 * @author dmulloy2
 */

public class FormatUtil 
{

	public static String format(String format, Object... objects) 
	{
		String ret = MessageFormat.format(format, objects);
		return ChatColor.translateAlternateColorCodes('&', ret);
	}
	
}
