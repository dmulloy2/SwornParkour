package net.dmulloy2.swornparkour.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.commands.CmdSetPoint;
import net.dmulloy2.swornparkour.integration.WorldEditHandler;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

@Getter
@RequiredArgsConstructor
public class ParkourCreator
{
	private int step;

	private ParkourField field;

	private Location spawn;
	private Location check1;
	private Location check2;
	private Location end;

	private final int gameId;
	private final Player player;
	private final SwornParkour plugin;

	public final void start()
	{
		this.step = 1;

		sendMessage("&eThis will guide you through the parkour creation process!");
		sendMessage("&eStep 1: Select the WorldEdit region for this arena.");
		sendMessage("&eThis region should be a cuboid and encase the entire arena.");
		sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
	}

	public void stepUp()
	{
		step++;
	}

	public void setPoint(int step)
	{
		if (step == 1)
		{
			WorldEditHandler worldEdit = plugin.getWorldEdit();
			if (worldEdit != null && worldEdit.isEnabled())
			{
				if (! worldEdit.hasSelection(player))
				{
					sendMessage("&cYou must have a valid WorldEdit selection to continue!");
					return;
				}

				Location min = worldEdit.getMinPoint(player);
				Location max = worldEdit.getMaxPoint(player);
				if (min == null || max == null)
				{
					sendMessage("&cYou must complete your selection!");
					return;
				}

				field = new ParkourField(min, max, player.getWorld());

				sendMessage("&eParkour Region successfully set!");
				sendMessage("&eStep 2: Set the location where players will spawn.");
				sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
				stepUp();
			}
			else
			{
				// TODO Fallback
			}
		}
		else if (step == 2)
		{
			Location loc = player.getLocation();
			if (! field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}

			this.spawn = loc;

			sendMessage("&aSpawn location successfully set!");
			sendMessage("&eStep 3: Set the first checkpoint.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			stepUp();
		}
		else if (step == 3)
		{
			Location loc = player.getLocation();
			if (! field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}

			this.check1 = loc;

			sendMessage("&aFirst checkpoint successfully set!");

			sendMessage("&eStep 4: Set the second checkpoint.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));

			stepUp();
		}
		else if (step == 4)
		{
			Location loc = player.getLocation();
			if (! field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}

			this.check2 = loc;

			sendMessage("&aSecond checkpoint successfully set!");
			sendMessage("&eStep 5: Set the end location for parkour.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			stepUp();
		}
		else if (step == 5)
		{
			Location loc = player.getLocation();
			if (! field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}

			this.end = loc;

			sendMessage("&aEnd location successfully set!");
			sendMessage("&aYou have finished the creation of parkour arena {0}!", gameId);
			sendMessage("&eRemember to place 24 LapisLazuli blocks in the arena so players can get points!");
			complete();
		}

		else
		{
			sendMessage("&cInvalid step: {0}!", step);
		}
	}

	public void complete()
	{
		plugin.getParkourHandler().getCreators().remove(this);

		ParkourZone pz = new ParkourZone(plugin, gameId);

		pz.setField(field);

		pz.setSpawn(spawn);
		pz.setCheck1(check1);
		pz.setCheck2(check2);
		pz.setEnd(end);

		plugin.getFileHandler().save(pz);
		plugin.getLoadedArenas().add(pz);
	}

	public void sendMessage(String string, Object... objects)
	{
		player.sendMessage(plugin.getPrefix() + FormatUtil.format(string, objects));
	}
}