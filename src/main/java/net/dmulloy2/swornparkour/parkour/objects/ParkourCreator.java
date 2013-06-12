package net.dmulloy2.swornparkour.parkour.objects;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.commands.CmdSetPoint;
import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

/**
 * @author dmulloy2
 */

public class ParkourCreator
{
	public int step;
	public int gameId;
	
	public ParkourField field;
	
	public Location spawn;
	public Location check1;
	public Location check2;
	public Location end;
	
	public Player player;
	public SwornParkour plugin;
	public ParkourCreator(SwornParkour plugin, Player player, int gameId)
	{
		this.plugin = plugin;
		this.player = player;
		this.gameId = gameId;
	}

	public void start()
	{
		step = 1;
		
		sendMessage("&eThis will guide you through the parkour creation process!");
		sendMessage("&eStep 1: Select the WorldEdit region for this arena.");
		sendMessage("&eThis region should be a cuboid and encase the entire arena.");
		sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
	}
	
	public int getStep()
	{
		return step;
	}
	
	public void stepUp()
	{
		step++;
	}
	
	public void setPoint(int step)
	{
		if (step == 1)
		{
			WorldEditPlugin worldEdit = plugin.getWorldEdit();
			Selection sel = worldEdit.getSelection(player);
			if (sel == null)
			{
				sendMessage("&cError, you must have a valid WorldEdit selection to continue!");
				return;
			}
			
			Location min = sel.getMinimumPoint();
			Location max = sel.getMaximumPoint();
			
			field = new ParkourField(min, max, player.getWorld());
			
			sendMessage("&aParkour Region successfully set!");
			
			sendMessage("&eStep 2: Set the location where players will spawn.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			
			stepUp();
		}
		
		else if (step == 2)
		{
			Location loc = player.getLocation();
			if (!field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}
			
			spawn = loc;
			
			sendMessage("&aSpawn location successfully set!");
			
			sendMessage("&eStep 3: Set the first checkpoint.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			
			stepUp();
		}
		
		else if (step == 3)
		{
			Location loc = player.getLocation();
			if (!field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}
			
			check1 = loc;
			
			sendMessage("&aFirst checkpoint successfully set!");
			
			sendMessage("&eStep 4: Set the second checkpoint.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			
			stepUp();
		}
		
		else if (step == 4)
		{
			Location loc = player.getLocation();
			if (!field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}
			
			check2 = loc;
			
			sendMessage("&aSecond checkpoint successfully set!");
			
			sendMessage("&eStep 5: Set the end location for parkour.");
			sendMessage("&eUse {0} &ewhen complete!", new CmdSetPoint(plugin).getUsageTemplate(false));
			
			stepUp();
		}
		
		else if (step == 5)
		{
			Location loc = player.getLocation();
			if (!field.isInside(loc))
			{
				sendMessage("&cPlease select a location inside the WorldEdit selection!");
				return;
			}
			
			end = loc;
			
			sendMessage("&aEnd location successfully set!");
			
			sendMessage("&aYou have finished the creation of parkour arena {0}!", gameId);
			
			sendMessage("&eRemember to place 24 LapisLazuli blocks in the arena so players can get points!");
			
			complete();
		}
		
		else
		{
			sendMessage("&cInvalid step specified!");
		}
	}
	
	public void complete()
	{
		plugin.getParkourManager().creators.remove(this);
		
		ParkourZone pz = new ParkourZone(plugin, gameId);
		
		pz.setField(field);
		
		pz.setSpawn(spawn);
		pz.setCheckpoint1(check1);
		pz.setCheckpoint2(check2);
		pz.setEnd(end);
		
		plugin.getFileHelper().save(pz);
		
		plugin.loadedArenas.add(pz);
	}
	
	public void sendMessage(String string, Object...objects)
	{
		player.sendMessage(FormatUtil.format(string, objects));
	}
}