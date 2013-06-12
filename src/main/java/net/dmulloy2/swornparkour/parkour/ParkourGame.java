package net.dmulloy2.swornparkour.parkour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.commands.CmdRedeem;
import net.dmulloy2.swornparkour.parkour.objects.ParkourKickReason;
import net.dmulloy2.swornparkour.parkour.objects.ParkourPlayer;
import net.dmulloy2.swornparkour.parkour.objects.ParkourReward;
import net.dmulloy2.swornparkour.parkour.objects.ParkourZone;
import net.dmulloy2.swornparkour.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;

/**
 * @author dmulloy2
 */

public class ParkourGame 
{
	private ParkourZone pz;
	private int gameId;
	private int checkpoint = 0;
	private List<ItemStack> itemContents = new ArrayList<ItemStack>();
	private List<ItemStack> armorContents = new ArrayList<ItemStack>();
	
	private boolean firstCheckpoint = false;
	private boolean secondCheckpoint = false;
	
	private ParkourPlayer player;
	public SwornParkour plugin;
	public ParkourGame(SwornParkour plugin, ParkourPlayer player, ParkourZone zone, int gameId)
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
	
	public List<ItemStack> getSavedInventory()
	{
		return itemContents;
	}
	
	public List<ItemStack> getSavedArmor()
	{
		return armorContents;
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
				
		// If essentials is found, remove god mode.
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Essentials"))
		{
			Plugin essPlugin = pm.getPlugin("Essentials");
			IEssentials ess = (IEssentials)essPlugin;
			User user = ess.getUser(player.getPlayer());
			if (user.isGodModeEnabled())
				user.setGodModeEnabled(false);
			if (user.isFlying())
				user.setFlying(false);
		}
		
		player.sendMessage("&eInitiation Complete, Welcome to Parkour!");
		
		player.sendMessage("&eThis parkour course will test your ability to jump, as well as mental dexterity and ability to find hidden objects.");
		
		player.sendMessage("&eIf at any time, this course becomes too dificult for your mind to bear, /parkour leave is always there!");
		
		player.sendMessage("&eWhen you are ready to begin, simply walk forward and begin!");
	}
	
	public void kick(ParkourKickReason reason)
	{
		if (reason == ParkourKickReason.TOO_MANY_DEATHS)
		{
			player.sendMessage("&eYou have reached the max number of deaths! Sorry!");
			
			returnInventory();
			
			endGame();
		}
		
		if (reason == ParkourKickReason.FORCE_KICK)
		{
			player.sendMessage("&eYou have been kicked from the arena!");
			
			returnInventory();
			
			endGame();
		}
		
		if (reason == ParkourKickReason.SHUTDOWN)
		{
			player.sendMessage("&eThe server is shutting down!");
			plugin.getFileHelper().savePlayer(player, true);

			plugin.getParkourManager().parkourGames.remove(this);
		}
		
		if (reason == ParkourKickReason.QUIT)
		{
			plugin.outConsole("Player {0} leaving game {1} from quit!", player.getPlayer().getName(), gameId);
			
			plugin.getFileHelper().savePlayer(player, false);
			
			plugin.getServer().broadcastMessage(FormatUtil.format("&eParkour Game &b{0} &ehas completed!", getId()));
			
			plugin.getParkourManager().parkourGames.remove(this);
		}
		
		if (reason == ParkourKickReason.LEAVE)
		{
			player.sendMessage("&eYou have left the arena!");
			
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
		
		for (Entry<Integer, ParkourReward> rewards : plugin.parkourRewards.entrySet())
		{
			int pointValue = rewards.getKey();
			if (plugin.cumulativeRewards)
			{
				if (points >= pointValue)
				{
					ParkourReward reward = rewards.getValue();
					ItemStack stack = reward.getItemStack();
					
					List<ItemStack> redemption = new ArrayList<ItemStack>();
					if (plugin.getParkourManager().inventoryHasRoom(player.getPlayer(), stack))
					{
						InventoryWorkaround.addItems(player.getPlayer().getInventory(), stack);
					}
					else
					{
						redemption.add(stack);
					}
					
					if (redemption.size() > 0)
					{
						player.sendMessage("&eYou have until the next restart to claim the rest of your rewards!");
						player.sendMessage("&eUse {0}!", new CmdRedeem(plugin).getUsageTemplate(false));
						
						plugin.getParkourManager().redemption.put(player.getPlayer().getName(), redemption);
					}
				}
			}
			else
			{
				if (points == pointValue)
				{
					ParkourReward reward = rewards.getValue();
					ItemStack stack = reward.getItemStack();

					List<ItemStack> redemption = new ArrayList<ItemStack>();
					if (plugin.getParkourManager().inventoryHasRoom(player.getPlayer(), stack))
					{
						InventoryWorkaround.addItems(player.getPlayer().getInventory(), stack);
					}
					else
					{
						redemption.add(stack);
					}
					
					if (redemption.size() > 0)
					{
						player.sendMessage("&eYou have until the next restart to claim the rest of your rewards!");
						player.sendMessage("&eUse {0}!", new CmdRedeem(plugin).getUsageTemplate(false));
						
						plugin.getParkourManager().redemption.put(player.getPlayer().getName(), redemption);
					}
				}
			}
		}
		
		if (plugin.getEconomy() != null)
		{
			if (plugin.cashRewardsEnabled)
			{
				int reward = plugin.cashRewardMultiplier * points;
				plugin.getEconomy().depositPlayer(player.getPlayer().getName(), reward);
				player.sendMessage("&a{0} has been added to your balance!", plugin.getEconomy().format(reward));
			}
		}
	}
	
	public void endGame()
	{	
		teleport(player.getSpawnBack());
		
		plugin.getServer().broadcastMessage(FormatUtil.format("&eParkour Game &b{0} &ehas completed!", getId()));
		
		plugin.getParkourManager().parkourGames.remove(this);
	}
	
	public void saveInventory()
	{
		PlayerInventory inv = player.getPlayer().getInventory();
		for (ItemStack itemStack : inv.getContents())
		{
			if (itemStack != null && itemStack.getType() != Material.AIR)
			{
				itemContents.add(itemStack);
			}
		}
		
		for (ItemStack armor : inv.getArmorContents())
		{
			if (armor != null && armor.getType() != Material.AIR)
			{
				armorContents.add(armor);
			}
		}
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
			kick(ParkourKickReason.TOO_MANY_DEATHS);
		}
		else
		{
			if (checkpoint == 0)
			{
				teleport(pz.getSpawn());
			}
			
			if (checkpoint == 1)
			{
				teleport(pz.getCheckpoint1());
			}
			
			if (checkpoint == 2)
			{
				teleport(pz.getCheckpoint2());
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
		for (ItemStack itemStack : itemContents)
		{
			InventoryWorkaround.addItems(player.getPlayer().getInventory(), itemStack);
		}
		
		for (ItemStack armor : armorContents)
		{
			String type = armor.getType().toString().toLowerCase();
			if (type.contains("helmet"))
			{
				inv.setHelmet(armor);
			}
				
			if (type.contains("chestplate"))
			{
				inv.setChestplate(armor);
			}
				
			if (type.contains("leggings"))
			{
				inv.setLeggings(armor);
			}
				
			if (type.contains("boots"))
			{
				inv.setBoots(armor);
			}
		}
	}
}