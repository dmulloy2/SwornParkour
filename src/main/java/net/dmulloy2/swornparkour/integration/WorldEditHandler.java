/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornparkour.integration;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.integration.DependencyProvider;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

/**
 * @author dmulloy2
 */

public class WorldEditHandler extends DependencyProvider<WorldEditPlugin>
{
	public WorldEditHandler(SwornPlugin handler)
	{
		super(handler, "WorldEdit");
	}

	public final Location getMaxPoint(Player player)
	{
		if (! isEnabled())
			return null;

		try
		{
			Selection selection = getSelection(player);
			return selection != null ? selection.getMaximumPoint() : null;
		} catch (Throwable ex) { }
		return null;
	}

	public final Location getMinPoint(Player player)
	{
		if (! isEnabled())
			return null;

		try
		{
			Selection selection = getSelection(player);
			return selection != null ? selection.getMinimumPoint() : null;
		} catch (Throwable ex) { }
		return null;
	}

	public final boolean hasSelection(Player player)
	{
		return getSelection(player) != null;
	}

	private final Selection getSelection(Player player)
	{
		if (! isEnabled())
			return null;

		try
		{
			return getDependency().getSelection(player);
		} catch (Throwable ex) { }
		return null;
	}
}