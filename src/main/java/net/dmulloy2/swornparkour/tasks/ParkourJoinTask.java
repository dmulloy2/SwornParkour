package net.dmulloy2.swornparkour.tasks;

import net.dmulloy2.swornparkour.SwornParkour;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class ParkourJoinTask extends BukkitRunnable
{
	public final SwornParkour plugin;
	public final Player player;
	public final int gameId;

	public ParkourJoinTask(final SwornParkour plugin, final Player player, final int gameId)
	{
		this.plugin = plugin;
		this.player = player;
		this.gameId = gameId;
		this.plugin.getWaiting().put(player, this);
	}

	@Override
	public void run()
	{
		plugin.getWaiting().remove(player);
		plugin.getParkourHandler().joinParkour(player, gameId);
	}
}