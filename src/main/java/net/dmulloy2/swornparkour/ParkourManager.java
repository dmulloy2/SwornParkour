package net.dmulloy2.swornparkour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dmulloy2.swornparkour.parkour.ParkourGame;
import net.dmulloy2.swornparkour.parkour.objects.ParkourCreator;
import net.dmulloy2.swornparkour.parkour.objects.ParkourKickReason;
import net.dmulloy2.swornparkour.parkour.objects.ParkourPlayer;
import net.dmulloy2.swornparkour.parkour.objects.ParkourZone;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author dmulloy2
 */

public class ParkourManager 
{
	public List<ParkourGame> parkourGames = new ArrayList<ParkourGame>();
	public List<ParkourCreator> creators = new ArrayList<ParkourCreator>();
	public HashMap<String, List<ItemStack>> redemption = new HashMap<String, List<ItemStack>>();
	
	private final SwornParkour plugin;
	public ParkourManager(final SwornParkour plugin)
	{
		this.plugin = plugin;
	}
	
	public void joinParkour(Player player, int gameId)
	{
		if (redemption.containsKey(player.getName()))
			redemption.remove(player.getName());
		
		ParkourPlayer pPlayer = newParkourPlayer(player, gameId);
		ParkourGame pGame = newParkourGame(pPlayer, gameId);
		
		pGame.join();
	}
	
	public ParkourPlayer newParkourPlayer(Player player, int gameId)
	{
		ParkourPlayer parkourPlayer = new ParkourPlayer(plugin, player, gameId, player.getLocation());
		return parkourPlayer;
	}
	
	public ParkourGame newParkourGame(ParkourPlayer player, int gameId)
	{
		ParkourZone zone = plugin.getParkourZone(gameId);
		ParkourGame parkourGame = new ParkourGame(plugin, player, zone, gameId);
		parkourGames.add(parkourGame);
		
		return parkourGame;
	}
	
	public boolean isInParkour(Player player)
	{
		for (ParkourGame parkourGame : parkourGames)
		{
			if (parkourGame.getParkourPlayer().getPlayer().getName().equals(player.getName()))
				return true;
		}
		
		return false;
	}
	
	public ParkourPlayer getParkourPlayer(Player player)
	{
		for (ParkourGame parkourGame : parkourGames)
		{
			ParkourPlayer parkourPlayer = parkourGame.getParkourPlayer();
			if (parkourPlayer.getPlayer().getName().equals(player.getName()))
				return parkourPlayer;
		}
		
		return null;
	}
	
	public void createNewParkourGame(Player player)
	{
		ParkourCreator creator = new ParkourCreator(plugin, player, plugin.loadedArenas.size() + 1);
		creator.start();
		
		creators.add(creator);
	}
	
	public ParkourCreator getParkourCreator(Player player)
	{
		for (ParkourCreator creator : creators)
		{
			if (creator.player.getName().equals(player.getName()))
				return creator;
		}
		
		return null;
	}
	
	public boolean isCreatingArena(Player player)
	{
		for (ParkourCreator creator : creators)
		{
			if (creator.player.getName().equals(player.getName()))
				return true;
		}
		
		return false;
	}
	
	public ParkourGame getParkourGame(Player player)
	{
		if (getParkourPlayer(player) != null)
			return getParkourGame(getParkourPlayer(player));
		
		return null;
	}
	
	public ParkourGame getParkourGame(ParkourPlayer player)
	{
		for (ParkourGame game : parkourGames)
		{
			if (game.getParkourPlayer() == player)
				return game;
		}
		
		return null;
	}
	
	public void onShutdown()
	{
		for (ParkourGame game : parkourGames)
		{
			game.kick(ParkourKickReason.SHUTDOWN);
		}
		
		redemption.clear();
		parkourGames.clear();
		creators.clear();
	}
	
	public boolean deleteArena(int gameId)
	{
		for (ParkourGame game : parkourGames)
		{
			if (game.getId() == gameId)
			{
				game.kick(ParkourKickReason.DISABLE);
				parkourGames.remove(game);
			}
		}
		
		plugin.loadedArenas.remove(plugin.getParkourZone(gameId));
		
		return (plugin.getFileHelper().deleteFile(gameId));
	}
	
	public boolean inventoryHasRoom(Player player)
	{
		int count = 0;
		PlayerInventory inv = player.getInventory();
		for (ItemStack stack : inv.getContents())
		{
			if (stack == null)
				count++;
		}
		
		return count > 0;
	}
}