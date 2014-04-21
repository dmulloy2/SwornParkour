package net.dmulloy2.swornparkour.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.commands.CmdClaim;
import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

/**
 * @author dmulloy2
 */

public class ParkourGame
{
	private ParkourZone pz;
	private int gameId;
	private int checkpoint = 0;

	private ItemStack[] itemContents;
	private ItemStack[] armorContents;

	private boolean firstCheckpoint = false;
	private boolean secondCheckpoint = false;

	private ParkourPlayer player;
	private final SwornParkour plugin;

	public ParkourGame(final SwornParkour plugin, ParkourPlayer player, ParkourZone zone, int gameId)
	{
		this.plugin = plugin;
		this.player = player;
		this.gameId = gameId;
		this.pz = zone;
	}

	public ParkourPlayer getParkourPlayer()
	{
		return player;
	}

	public ParkourZone getParkourZone()
	{
		return pz;
	}

	public int getId()
	{
		return gameId;
	}

	public void join()
	{
		player.sendMessage("&eCommencing Initiation...");

		player.getPlayer().teleport(pz.getSpawn().clone().add(0, 2.0D, 0));

		saveInventory();
		clearInventory();

		// Basic things players need to play
		player.getPlayer().setGameMode(GameMode.SURVIVAL);
		player.getPlayer().setHealth(20);
		player.getPlayer().setFoodLevel(20);
		player.getPlayer().setFireTicks(0);

		player.getPlayer().setAllowFlight(false);
		player.getPlayer().setFlying(false);

		// If essentials is found, remove god mode.
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Essentials"))
		{
			Plugin essPlugin = pm.getPlugin("Essentials");
			IEssentials ess = (IEssentials) essPlugin;
			User user = ess.getUser(player.getPlayer());
			if (user.isGodModeEnabled())
				user.setGodModeEnabled(false);
		}

		pz.setTimesPlayed(pz.getTimesPlayed() + 1);

		player.sendMessage("&eInitiation Complete, Welcome to Parkour!");
		player.sendMessage("&eThis parkour course will test your ability to jump, as well as mental dexterity and ability to find hidden objects.");
		player.sendMessage("&eIf at any time, this course becomes too dificult for your mind to bear, /parkour leave is always there!");
		player.sendMessage("&eWhen you are ready to begin, simply walk forward!");

		plugin.updateSigns(gameId);
	}

	public void kick(ParkourKickReason reason)
	{
		if (reason == ParkourKickReason.DEATHS)
		{
			player.sendMessage("&eYou have reached the max number of deaths! Sorry!");

			returnInventory();

			endGame();
		}

		if (reason == ParkourKickReason.FORCE)
		{
			player.sendMessage("&eYou have been kicked from the arena!");

			returnInventory();

			endGame();
		}

		if (reason == ParkourKickReason.SHUTDOWN)
		{
			player.sendMessage("&eThe server is shutting down!");

			returnInventory();

			endGame();
		}

		if (reason == ParkourKickReason.QUIT)
		{
			plugin.outConsole("Player {0} leaving game {1} from quit!", player.getPlayer().getName(), gameId);

			returnInventory();

			endGame();
		}

		if (reason == ParkourKickReason.LEAVE)
		{
			player.sendMessage("&eYou have left the arena!");

			returnInventory();

			endGame();
		}

		if (reason == ParkourKickReason.DISABLE)
		{
			player.sendMessage("&cThis arena has been disabled!");

			returnInventory();

			endGame();
		}
	}

	public void onComplete()
	{
		player.sendMessage("&aCongratulations, you have completed parkour!");

		returnInventory();

		reward();

		endGame();
	}

	public void reward()
	{
		int points = player.getPoints();
		player.sendMessage("&eYou won with a total of &b{0} &epoints!", points);

		List<ItemStack> redemption = new ArrayList<ItemStack>();
		for (Entry<Integer, ParkourReward> rewards : plugin.getParkourRewards().entrySet())
		{
			int pointValue = rewards.getKey();
			if (plugin.isCumulativeRewards())
			{
				if (points >= pointValue)
				{
					ParkourReward reward = rewards.getValue();
					ItemStack stack = reward.getItemStack();

					if (plugin.getParkourHandler().inventoryHasRoom(player.getPlayer()))
					{
						player.getPlayer().getInventory().addItem(stack);
					}
					else
					{
						redemption.add(stack);
					}
				}
			}
			else
			{
				if (points == pointValue)
				{
					ParkourReward reward = rewards.getValue();
					ItemStack stack = reward.getItemStack();

					if (plugin.getParkourHandler().inventoryHasRoom(player.getPlayer()))
					{
						player.getPlayer().getInventory().addItem(stack);
					}
					else
					{
						redemption.add(stack);
					}
				}
			}
		}

		if (redemption.size() > 0)
		{
			StringBuilder line = new StringBuilder();
			line.append("&eYou have until the next restart to claim the rest of your rewards!");
			line.append(" &eUse " + new CmdClaim(plugin).getUsageTemplate(false));
			player.sendMessage(line.toString());

			plugin.getParkourHandler().getRedemption().put(player.getPlayer().getName(), redemption);
		}

		if (plugin.getEconomy() != null)
		{
			if (plugin.isCashRewardsEnabled())
			{
				int reward = plugin.getCashRewardMultiplier() * points;
				plugin.getEconomy().depositPlayer(player.getPlayer().getName(), reward);
				player.sendMessage("&a{0} has been added to your balance!", plugin.getEconomy().format(reward));
			}
		}
	}

	public void endGame()
	{
		teleport(player.getSpawnBack());

		plugin.getServer().broadcastMessage(FormatUtil.format("&eParkour Game &b{0} &ehas completed!", getId()));

		plugin.getParkourHandler().getParkourGames().remove(this);

		plugin.updateSigns(gameId);
	}

	public void saveInventory()
	{
		PlayerInventory inv = player.getPlayer().getInventory();

		this.itemContents = inv.getContents();
		this.armorContents = inv.getArmorContents();
	}

	public void clearInventory()
	{
		PlayerInventory inv = player.getPlayer().getInventory();

		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	public void onDeath()
	{
		player.onDeath();

		if (player.getDeaths() == 3)
		{
			kick(ParkourKickReason.DEATHS);
		}
		else
		{
			if (checkpoint == 0)
			{
				teleport(pz.getSpawn());
			}

			if (checkpoint == 1)
			{
				teleport(pz.getCheck1());
			}

			if (checkpoint == 2)
			{
				teleport(pz.getCheck2());
			}
		}
	}

	public void firstCheckpoint()
	{
		checkpoint = 1;
		firstCheckpoint = true;

		player.sendMessage("&eCheckpoint &b1 &ereached!");
	}

	public void secondCheckpoint()
	{
		checkpoint = 2;
		secondCheckpoint = true;

		player.sendMessage("&eCheckpoint &b2 &ereached!");
	}

	public int getCheckpoint()
	{
		return checkpoint;
	}

	public void teleport(Location loc)
	{
		player.getPlayer().teleport(loc);
	}

	public boolean hasFirstCheckpoint()
	{
		return firstCheckpoint;
	}

	public boolean hasSecondCheckpoint()
	{
		return secondCheckpoint;
	}

	public void returnInventory()
	{
		PlayerInventory inv = player.getPlayer().getInventory();

		inv.setContents(itemContents);
		inv.setArmorContents(armorContents);
	}
}