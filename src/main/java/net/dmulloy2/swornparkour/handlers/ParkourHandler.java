package net.dmulloy2.swornparkour.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourCreator;
import net.dmulloy2.swornparkour.types.ParkourGame;
import net.dmulloy2.swornparkour.types.ParkourKickReason;
import net.dmulloy2.swornparkour.types.ParkourPlayer;
import net.dmulloy2.swornparkour.types.ParkourZone;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author dmulloy2
 */

@Getter
public class ParkourHandler
{
	private List<ParkourGame> parkourGames = new ArrayList<ParkourGame>();
	private List<ParkourCreator> creators = new ArrayList<ParkourCreator>();
	private HashMap<String, List<ItemStack>> redemption = new HashMap<String, List<ItemStack>>();

	private final SwornParkour plugin;

	public ParkourHandler(SwornParkour plugin)
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
		ParkourCreator creator = new ParkourCreator(plugin, player, plugin.getLoadedArenas().size() + 1);
		creator.start();

		creators.add(creator);
	}

	public ParkourCreator getParkourCreator(Player player)
	{
		for (ParkourCreator creator : creators)
		{
			if (creator.getPlayer().getName().equals(player.getName()))
				return creator;
		}

		return null;
	}

	public boolean isCreatingArena(Player player)
	{
		for (ParkourCreator creator : creators)
		{
			if (creator.getPlayer().getName().equals(player.getName()))
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

		plugin.getLoadedArenas().remove(plugin.getParkourZone(gameId));
		return plugin.getFileHandler().deleteFile(gameId);
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