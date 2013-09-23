package net.dmulloy2.swornparkour.listeners;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.handlers.ParkourHandler;
import net.dmulloy2.swornparkour.tasks.ParkourJoinTask;
import net.dmulloy2.swornparkour.types.ParkourGame;
import net.dmulloy2.swornparkour.types.ParkourKickReason;
import net.dmulloy2.swornparkour.types.ParkourPlayer;
import net.dmulloy2.swornparkour.types.ParkourZone;
import net.dmulloy2.swornparkour.util.FormatUtil;
import net.dmulloy2.swornparkour.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author dmulloy2
 */

public class PlayerListener implements Listener
{
	private final SwornParkour plugin;
	public PlayerListener(SwornParkour plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!plugin.getParkourHandler().isInParkour(event.getPlayer()))
			return;
		
		if (!event.hasBlock())
			return;
		
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		
		if (block.getType() != Material.LAPIS_BLOCK)
			return;
		
		ParkourPlayer player = plugin.getParkourHandler().getParkourPlayer(event.getPlayer());
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
		if (!plugin.getParkourHandler().isInParkour(event.getPlayer()))
			return;
		
		ParkourPlayer player = plugin.getParkourHandler().getParkourPlayer(event.getPlayer());
		if (player == null)
			return;
		
		ParkourHandler manager = plugin.getParkourHandler();
		ParkourGame game = manager.getParkourGame(player);
		ParkourZone zone = game.getParkourZone();
		if (zone.getField().isInside(player.getPlayer()))
			return;
		
		if (game.hasFirstCheckpoint() && !game.hasSecondCheckpoint())
		{
			player.getPlayer().teleport(zone.getCheckpoint1());
		}
		
		else if (game.hasFirstCheckpoint() && game.hasSecondCheckpoint())
		{
			player.getPlayer().teleport(zone.getCheckpoint2());
		}
		
		else
		{
			player.getPlayer().teleport(zone.getSpawn());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove1(PlayerMoveEvent event)
	{
		if (!plugin.getParkourHandler().isInParkour(event.getPlayer()))
			return;
		
		ParkourPlayer player = plugin.getParkourHandler().getParkourPlayer(event.getPlayer());
		if (player == null)
			return;
		
		ParkourHandler manager = plugin.getParkourHandler();
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
		if (!plugin.getParkourHandler().isInParkour(event.getEntity()))
			return;
		
		ParkourPlayer player = plugin.getParkourHandler().getParkourPlayer(event.getEntity());
		if (player == null)
			return;
		
		ParkourHandler manager = plugin.getParkourHandler();
		ParkourGame game = manager.getParkourGame(player);
		
		game.onDeath();
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
		if (plugin.getParkourHandler().isInParkour(player))
		{
			plugin.getParkourHandler().getParkourGame(player).kick(ParkourKickReason.QUIT);
		}
		
		for (int i=0; i<plugin.waiting.size(); i++)
		{
			ParkourJoinTask task = plugin.waiting.get(i);
			if (task.player.getName().equals(player.getName()))
			{
				task.cancel();
				
				plugin.waiting.remove(task);
			}
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
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (event.hasBlock()) 
			{
				Block block = event.getClickedBlock();
				if (block.getState() instanceof Sign) 
				{
					Sign s = (Sign)block.getState();
					if (s.getLine(0).equalsIgnoreCase("[SwornParkour]"))
					{
						if (s.getLine(1).equalsIgnoreCase("Click to join"))
						{
							int gameId = Integer.parseInt(s.getLine(2).replaceAll("Game ", ""));
							
							if (plugin.loadedArenas.size() < gameId)
							{
								player.sendMessage(FormatUtil.format("&cNo arena by that number exists!"));
								return;
							}
							
							for (ParkourGame game : plugin.getParkourHandler().parkourGames)
							{
								if (game.getId() == gameId)
								{
									player.sendMessage(FormatUtil.format("&cThat game is already in progress!"));
									return;
								}
							}
							
							if (plugin.getParkourHandler().isInParkour(player))
							{
								player.sendMessage(FormatUtil.format("&cYou are already in a game!"));
								return;
							}
							
							int teleportTimer = plugin.teleportTimer * 20;
							
							player.sendMessage(FormatUtil.format("&ePlease stand still for {0} seconds!", plugin.teleportTimer));
							
							new ParkourJoinTask(plugin, player, gameId).runTaskLater(plugin, teleportTimer);
						}
					}
				}
			}
		}
	}
}