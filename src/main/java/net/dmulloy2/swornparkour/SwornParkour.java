/**
 * SwornParkour - a bukkit plugin
 * Copyright (C) 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornparkour;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import lombok.Getter;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.handlers.ResourceHandler;
import net.dmulloy2.integration.VaultHandler;
import net.dmulloy2.swornparkour.commands.CmdAbandon;
import net.dmulloy2.swornparkour.commands.CmdClaim;
import net.dmulloy2.swornparkour.commands.CmdCreate;
import net.dmulloy2.swornparkour.commands.CmdDelete;
import net.dmulloy2.swornparkour.commands.CmdJoin;
import net.dmulloy2.swornparkour.commands.CmdKick;
import net.dmulloy2.swornparkour.commands.CmdLeave;
import net.dmulloy2.swornparkour.commands.CmdList;
import net.dmulloy2.swornparkour.commands.CmdReload;
import net.dmulloy2.swornparkour.commands.CmdSetPoint;
import net.dmulloy2.swornparkour.commands.CmdSpawn;
import net.dmulloy2.swornparkour.commands.CmdVersion;
import net.dmulloy2.swornparkour.handlers.FileHandler;
import net.dmulloy2.swornparkour.handlers.ParkourHandler;
import net.dmulloy2.swornparkour.integration.WorldEditHandler;
import net.dmulloy2.swornparkour.listeners.BlockListener;
import net.dmulloy2.swornparkour.listeners.PlayerListener;
import net.dmulloy2.swornparkour.tasks.ParkourJoinTask;
import net.dmulloy2.swornparkour.types.ParkourReward;
import net.dmulloy2.swornparkour.types.ParkourSign;
import net.dmulloy2.swornparkour.types.ParkourZone;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * @author dmulloy2
 */

public class SwornParkour extends SwornPlugin
{
	// Handlers
	private @Getter ResourceHandler resourceHandler;
	private @Getter ParkourHandler parkourHandler;
	private @Getter FileHandler fileHandler;

	// Integration
	private @Getter VaultHandler vault;
	private @Getter WorldEditHandler worldEdit;

	// Lists and maps
	private @Getter List<ParkourSign> signs = new ArrayList<>();
	private @Getter List<ParkourZone> loadedArenas = new ArrayList<>();
	private @Getter Map<Player, ParkourJoinTask> waiting = new HashMap<>();
	private @Getter Map<Integer, ParkourReward> parkourRewards = new HashMap<>();

	// Configuration
	// TODO: Move these to their own handlers
	private @Getter int teleportTimer, cashRewardMultiplier;
	private @Getter boolean cumulativeRewards, itemRewardsEnabled, cashRewardsEnabled, debug;

	// Prefix
	private final @Getter String prefix = FormatUtil.format("&3[&eParkour&3]&e ");

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		// Register handlers
		logHandler = new LogHandler(this);
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler("parkour");
		resourceHandler = new ResourceHandler(this);

		// Configuration
		createDirectories();
		saveDefaultConfig();
		loadConfig();

		parkourHandler = new ParkourHandler(this);
		fileHandler = new FileHandler(this);

		updateRewards();
		loadGames();

		// Load signs
		fileHandler.loadSigns();

		// Integration
		setupIntegration();

		// Register commands
		commandHandler.setCommandPrefix("parkour");
		commandHandler.registerPrefixedCommand(new CmdAbandon(this));
		commandHandler.registerPrefixedCommand(new CmdClaim(this));
		commandHandler.registerPrefixedCommand(new CmdCreate(this));
		commandHandler.registerPrefixedCommand(new CmdDelete(this));
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdJoin(this));
		commandHandler.registerPrefixedCommand(new CmdKick(this));
		commandHandler.registerPrefixedCommand(new CmdLeave(this));
		commandHandler.registerPrefixedCommand(new CmdList(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));
		commandHandler.registerPrefixedCommand(new CmdSetPoint(this));
		commandHandler.registerPrefixedCommand(new CmdSpawn(this));
		commandHandler.registerPrefixedCommand(new CmdVersion(this));

		// Register listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new BlockListener(this), this);

		log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);

		for (ParkourZone zone : loadedArenas)
		{
			fileHandler.save(zone);
		}

		clearMemory();

		log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	private final void createDirectories()
	{
		File dataFolder = getDataFolder();
		if (! dataFolder.exists())
			dataFolder.mkdirs();

		File games = new File(getDataFolder(), "games");
		if (! games.exists())
			games.mkdirs();
	}

	private final void setupIntegration()
	{
		try
		{
			vault = new VaultHandler(this);
		} catch (Throwable ex) { }

		try
		{
			worldEdit = new WorldEditHandler(this);
		} catch (Throwable ex) { }
	}

	private final void loadGames()
	{
		File folder = new File(getDataFolder(), "games");
		File[] children = folder.listFiles();
		for (File file : children)
		{
			fileHandler.load(file);
		}
	}

	public void updateSigns(int gameId)
	{
		for (ParkourSign sign : signs)
		{
			if (sign.getZone().getGameId() == gameId)
			{
				sign.update();
			}
		}
	}

	public ParkourSign getParkourSign(Location loc)
	{
		for (ParkourSign sign : signs)
		{
			if (Util.coordsEqual(sign.getLocation(), loc))
				return sign;
		}

		return null;
	}

	private final void loadConfig()
	{
		teleportTimer = getConfig().getInt("teleport-timer");
		cumulativeRewards = getConfig().getBoolean("item-rewards-cumulative");
		itemRewardsEnabled = getConfig().getBoolean("item-rewards-enabled");
		cashRewardsEnabled = getConfig().getBoolean("cash-reward.enabled");
		cashRewardMultiplier = getConfig().getInt("cash-reward.multiplier");
		debug = getConfig().getBoolean("debug");
	}

	@Override
	public void reload()
	{
		reloadConfig();
		updateRewards();
		loadConfig();
	}

	// ---- Console Logging

	public final void log(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}

	public final void log(Level level, String string, Object... objects)
	{
		logHandler.log(level, string, objects);
	}

	public final void debug(String string, Object... objects)
	{
		logHandler.debug(string, objects);
	}

	public final String getMessage(String string)
	{
		return resourceHandler.getMessage(string);
	}

	private final void updateRewards()
	{
		parkourRewards.clear();

		for (int i = 0; i < 25; i++)
		{
			String reward = getConfig().getString("item-rewards." + i);

			fileHandler.readReward(i, reward);
		}

		log("Loaded all rewards!");
	}

	public final ParkourZone getParkourZone(int gameId)
	{
		for (ParkourZone zone : loadedArenas)
		{
			if (zone.getGameId() == gameId)
			{
				return zone;
			}
		}

		return null;
	}

	private void clearMemory()
	{
		parkourHandler.onShutdown();

		parkourRewards.clear();
		loadedArenas.clear();
		waiting.clear();
	}

	public void removeSign(ParkourSign sign)
	{
		signs.remove(sign);
		fileHandler.updateSignSave();
	}
}