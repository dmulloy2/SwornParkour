package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.tasks.ParkourJoinTask;
import net.dmulloy2.swornparkour.types.ParkourGame;

/**
 * @author dmulloy2
 */

public class CmdJoin extends SwornParkourCommand
{
	public CmdJoin(SwornParkour plugin)
	{
		super(plugin);
		this.name = "join";
		this.aliases.add("j");
		this.addRequiredArg("game");
		this.description = "Join a parkour game!";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		int gameId = argAsInt(0, true);
		if (gameId == -1)
			return;
		
		if (plugin.getLoadedArenas().size() < gameId)
		{
			err("&cNo arena by that number exists!");
			return;
		}
		
		for (ParkourGame game : handler.getParkourGames())
		{
			if (game.getId() == gameId)
			{
				err("&cThat game is already in progress!");
				return;
			}
		}
		
		if (handler.isInParkour(player))
		{
			err("&cYou are already in a game!");
			return;
		}
		
		int teleportTimer = plugin.getTeleportTimer() * 20;
		
		sendMessage("&ePlease stand still for {0} seconds!", plugin.getTeleportTimer());
		
		new ParkourJoinTask(plugin, player, gameId).runTaskLater(plugin, teleportTimer);
	}
}