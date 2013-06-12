package net.dmulloy2.swornparkour.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.parkour.objects.ParkourField;
import net.dmulloy2.swornparkour.parkour.objects.ParkourZone;
import net.dmulloy2.swornparkour.permissions.Permission;
import net.dmulloy2.swornparkour.util.FormatUtil;

public class BlockListener implements Listener
{
	public SwornParkour plugin;
	public BlockListener(SwornParkour plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		Location loc = block.getLocation();
		
		for (ParkourZone zone : plugin.loadedArenas)
		{
			ParkourField field = zone.getField();
			if (field.isInside(loc))
			{
				Player player = event.getPlayer();
				if (plugin.getParkourManager().isInParkour(player))
				{
					String message = "&cYou cannot break blocks while in parkour!";
					player.sendMessage(FormatUtil.format(message));
					event.setCancelled(true);
					return;
				}
				
				if (!plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					String message = "&cYou do not have permission to edit parkour arenas!";
					player.sendMessage(FormatUtil.format(message));
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		Location loc = block.getLocation();
		
		for (ParkourZone zone : plugin.loadedArenas)
		{
			ParkourField field = zone.getField();
			if (field.isInside(loc))
			{
				Player player = event.getPlayer();
				if (plugin.getParkourManager().isInParkour(player))
				{
					String message = "&cYou cannot place blocks while in parkour!";
					player.sendMessage(FormatUtil.format(message));
					event.setCancelled(true);
					return;
				}
				
				if (!plugin.getPermissionHandler().hasPermission(player, Permission.BUILD))
				{
					String message = "&cYou do not have permission to edit parkour arenas!";
					player.sendMessage(FormatUtil.format(message));
					event.setCancelled(true);
				}
			}
		}
	}
}