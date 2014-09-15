package net.dmulloy2.swornparkour.commands;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourKickReason;
import net.dmulloy2.swornparkour.types.Permission;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdKick extends SwornParkourCommand
{
	public CmdKick(SwornParkour plugin)
	{
		super(plugin);
		this.name = "kick";
		this.addRequiredArg("player");
		this.description = "Kick a player from a game";
		this.permission = Permission.CMD_KICK;
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		Player target = Util.matchPlayer(args[0]);
		if (player == null)
		{
			err("&cPlayer not found!");
			return;
		}
		
		if (!handler.isInParkour(target))
		{
			err("&cThis player is not in a game!");
			return;
		}
		
		handler.getParkourGame(player).kick(ParkourKickReason.FORCE);
		
		sendMessage("&eYou have kicked {0} from the game!", target.getName());
	}
}