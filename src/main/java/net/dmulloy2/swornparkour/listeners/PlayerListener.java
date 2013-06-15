package net.dmulloy2.swornparkour.listeners;

import net.dmulloy2.swornparkour.ParkourManager;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.parkour.ParkourGame;
import net.dmulloy2.swornparkour.parkour.objects.ParkourJoinTask;
import net.dmulloy2.swornparkour.parkour.objects.ParkourKickReason;
import net.dmulloy2.swornparkour.parkour.objects.ParkourPlayer;
import net.dmulloy2.swornparkour.parkour.objects.ParkourZone;
import net.dmulloy2.swornparkour.parkour.objects.SavedParkourPlayer;
import net.dmulloy2.swornparkour.util.FormatUtil;
import net.dmulloy2.swornparkour.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author dmulloy2
 */

public class PlayerListener implements Listener
{
	public SwornParkour plugin;
	public PlayerListener(SwornParkour plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!plugin.getParkourManager().isInParkour(event.getPlayer()))
			return;
		
		if (!event.hasBlock())
			return;
		
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		
		if (block.getType() != Material.LAPIS_BLOCK)
			return;
		
		ParkourPlayer player = plugin.getParkourManager().getParkourPlayer(event.getPlayer());
		if (player == null)
			return;
		
		if (!player.getClickedBlocks().contains(block.getLocation()))
		{
			player.addClickedBlock(block.getLocation());
			player.addPoints(1);
			
			player.sendMessage("&eYou have gained &b1 &epoint! You now have &b{0} &epoints!", player.getPoints());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (!plugin.getParkourManager().isInParkour(event.getPlayer()))
			return;
		
		ParkourPlayer player = plugin.getParkourManager().getParkourPlayer(event.getPlayer());
		if (player == null)
			return;
		
		ParkourManager manager = plugin.getParkourManager();
		ParkourGame game = manager.getParkourGame(player);
		ParkourZone zone = game.getParkourZone();
		if (zone.getField().isInside(player.getPlayer()))
			return;
		
		player.getPlayer().teleport(zone.getSpawn());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove1(PlayerMoveEvent event)
	{
		if (!plugin.getParkourManager().isInParkour(event.getPlayer()))
			return;
		
		ParkourPlayer player = plugin.getParkourManager().getParkourPlayer(event.getPlayer());
		if (player == null)
			return;
		
		ParkourManager manager = plugin.getParkourManager();
		ParkourGame game = manager.getParkourGame(player);
		ParkourZone zone = game.getParkourZone();
		if (zone.getField().isInside(player.getPlayer()))
		{
			Location loc = player.getPlayer().getLocation();
			if (Util.checkLocation(loc, zone.getEnd()))
			{
				game.onComplete();
			}
			
			if (Util.checkLocation(loc, zone.getCheckpoint1()) && !game.hasFirstCheckpoint())
			{
				game.firstCheckpoint();
			}
			
			if (Util.checkLocation(loc, zone.getCheckpoint2()) && !game.hasSecondCheckpoint())
			{
				game.secondCheckpoint();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (!plugin.getParkourManager().isInParkour(event.getEntity()))
			return;
		
		ParkourPlayer player = plugin.getParkourManager().getParkourPlayer(event.getEntity());
		if (player == null)
			return;
		
		ParkourManager manager = plugin.getParkourManager();
		ParkourGame game = manager.getParkourGame(player);
		
		game.onDeath();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		for (SavedParkourPlayer savedPlayer : plugin.savedPlayers)
		{
			if (savedPlayer.getName().equals(event.getPlayer().getName()))
			{
				plugin.getParkourManager().normalizeSavedPlayer(savedPlayer);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) 
	{
		onPlayerDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) 
	{
		if (!event.isCancelled()) 
		{
			onPlayerDisconnect(event.getPlayer());
		}
	}

	public void onPlayerDisconnect(Player player) 
	{
		if (plugin.getParkourManager().isInParkour(player))
		{
			plugin.getParkourManager().getParkourGame(player).kick(ParkourKickReason.QUIT);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove2(PlayerMoveEvent event)
	{
		for (int i=0; i<plugin.waiting.size(); i++)
		{
			ParkourJoinTask task = plugin.waiting.get(i);
			if (task.player.getName().equals(event.getPlayer().getName()))
			{
				task.cancel();
				
				plugin.waiting.remove(task);
				
				event.getPlayer().sendMessage(FormatUtil.format("&cCancelled!"));
			}
		}
	}
}