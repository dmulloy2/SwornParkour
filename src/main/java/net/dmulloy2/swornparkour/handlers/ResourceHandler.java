package net.dmulloy2.swornparkour.handlers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.util.FileResourceLoader;

/**
 * @author dmulloy2
 */

public class ResourceHandler 
{
	private ResourceBundle messages;
	public ResourceHandler(SwornParkour plugin, ClassLoader classLoader) 
	{
		try 
		{
			messages = ResourceBundle.getBundle("messages", Locale.getDefault(), new FileResourceLoader(classLoader, plugin));
		} 
		catch (MissingResourceException ex) 
		{
			plugin.getLogHandler().log(Level.SEVERE, "Could not find resource bundle: messages.properties");
		}
	}
	
	public ResourceBundle getMessages() 
	{
		return messages;
	}
}