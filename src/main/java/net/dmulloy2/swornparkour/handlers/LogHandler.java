package net.dmulloy2.swornparkour.handlers;

import java.util.logging.Level;

import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author dmulloy2
 */

public class LogHandler
{
	private final JavaPlugin plugin;
	public LogHandler(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	public final void log(Level level, String msg, Object... objects)
	{
		plugin.getServer().getLogger().log(level, FormatUtil.format("[{0}] {1}", plugin.getName(), FormatUtil.format(msg, objects)));		
	}

	public final void log(String msg, Object... objects) 
	{
		log(Level.INFO, msg, objects);
	}
}