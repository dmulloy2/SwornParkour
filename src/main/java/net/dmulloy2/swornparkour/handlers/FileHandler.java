package net.dmulloy2.swornparkour.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.types.ParkourField;
import net.dmulloy2.swornparkour.types.ParkourReward;
import net.dmulloy2.swornparkour.types.ParkourSign;
import net.dmulloy2.swornparkour.types.ParkourZone;
import net.dmulloy2.types.EnchantmentType;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class FileHandler
{
	private final SwornParkour plugin;

	public final void save(ParkourZone pz)
	{
		try
		{
			File folder = new File(plugin.getDataFolder(), "games");
			File file = new File(folder, pz.getGameId() + ".dat");

			if (file.exists())
				file.delete();

			file.createNewFile();

			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			fc.set("id", pz.getGameId());

			ParkourField field = pz.getField();
			fc.set("world", field.getWorld().getName());

			fc.set("minx", field.getMinx());
			fc.set("miny", field.getMiny());
			fc.set("minz", field.getMinz());

			fc.set("maxx", field.getMaxx());
			fc.set("maxy", field.getMaxy());
			fc.set("maxz", field.getMaxz());

			Location spawn = pz.getSpawn();
			String spath = "spawn.";
			fc.set(spath + "x", spawn.getBlockX());
			fc.set(spath + "y", spawn.getBlockY());
			fc.set(spath + "z", spawn.getBlockZ());

			Location check1 = pz.getCheck1();
			String cpath1 = "check1.";
			fc.set(cpath1 + "x", check1.getBlockX());
			fc.set(cpath1 + "y", check1.getBlockY());
			fc.set(cpath1 + "z", check1.getBlockZ());

			Location check2 = pz.getCheck2();
			String cpath2 = "check2.";
			fc.set(cpath2 + "x", check2.getBlockX());
			fc.set(cpath2 + "y", check2.getBlockY());
			fc.set(cpath2 + "z", check2.getBlockZ());

			Location end = pz.getEnd();
			String epath = "end.";
			fc.set(epath + "x", end.getBlockX());
			fc.set(epath + "y", end.getBlockY());
			fc.set(epath + "z", end.getBlockZ());

			fc.set("timesPlayed", pz.getTimesPlayed());

			fc.save(file);
		}
		catch (Exception e)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(e, "saving game " + pz.getGameId()));
		}
	}

	public final void load(File file)
	{
		try
		{
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			int id = fc.getInt("id");

			ParkourZone pz = new ParkourZone(plugin, id);

			String worldName = fc.getString("world");
			if (worldName == null || worldName.isEmpty())
			{
				plugin.outConsole(Level.WARNING, "Encountered an exception loading game {0}: World cannot be null!", id);
				return;
			}

			World world = plugin.getServer().getWorld(worldName);
			if (world == null)
			{
				plugin.outConsole(Level.WARNING, "Encountered an exception loading game {0}: World cannot be null!", id);
				return;
			}

			Location min = new Location(world, fc.getInt("minx"), fc.getInt("miny"), fc.getInt("minz"));
			Location max = new Location(world, fc.getInt("maxx"), fc.getInt("maxy"), fc.getInt("maxz"));

			ParkourField field = new ParkourField(min, max, world);
			pz.setField(field);

			Location spawn = new Location(world, fc.getInt("spawn.x"), fc.getInt("spawn.y"), fc.getInt("spawn.z"));
			pz.setSpawn(spawn);

			Location check1 = new Location(world, fc.getInt("check1.x"), fc.getInt("check1.y"), fc.getInt("check1.z"));
			pz.setCheck1(check1);

			Location check2 = new Location(world, fc.getInt("check2.x"), fc.getInt("check2.y"), fc.getInt("check2.z"));
			pz.setCheck2(check2);

			Location end = new Location(world, fc.getInt("end.x"), fc.getInt("end.y"), fc.getInt("end.z"));
			pz.setEnd(end);

			pz.setTimesPlayed(fc.getInt("timesPlayed"));

			plugin.getLoadedArenas().add(pz);
			plugin.outConsole("Loaded game {0}!", id);
		}
		catch (Exception e)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(e, "loading " + file.getName()));
		}
	}

	public void readReward(int pointValue, String line)
	{
		boolean readEnchants = line.contains("enchants:");
		boolean readName = line.contains("name:");
		boolean readLore = line.contains("lore:");
		
		Material mat = null;
		short dat = 0;
		int amt = 0;
		
		String[] split = line.split(" ");
		
		String idSection = split[0];
		if (idSection.contains(":"))
		{
			mat = MaterialUtil.getMaterial(idSection.split(":")[0]);
			dat = Short.parseShort(idSection.split(":")[1]);
		}
		else
		{
			mat = MaterialUtil.getMaterial(idSection);
		}
		
		amt = Integer.parseInt(split[1]);
		
		Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		if (readEnchants)
		{
			String str = line.substring(line.indexOf("enchants:") + 9);
			if (str.contains(","))
			{
				String[] split2 = str.split(",");
				for (String s : split2)
				{
					String[] split3 = s.split(":");
					Enchantment enchant = EnchantmentType.toEnchantment(split3[0]);
					
					int level = 0;
					if (split3[1].contains(" name"))
					{
						String[] split4 = split3[1].split(" ");
						level = Integer.parseInt(split4[0]);
						
					}
					else
					{
						level = Integer.parseInt(split3[1]);
					}
					
					if (enchant != null && level > 0)
					{
						enchantments.put(enchant, level);
					}
				}
			}
			else
			{
				String[] split2 = str.split(":");
				Enchantment enchant = EnchantmentType.toEnchantment(split2[0]);
				int level = Integer.parseInt(split2[1]);
				
				enchantments.put(enchant, level);
			}
		}
		
		String displayName = ""; 
		if (readName)
		{
			String str = line.substring(line.indexOf("name:") + 5);
			str = FormatUtil.format(str);
			displayName = str.replaceAll("_", " ");
		}
		
		List<String> lore = new ArrayList<String>();
		if (readLore)
		{
			String str = line.substring(line.indexOf("lore:") + 5);
			if (str.contains(","))
			{
				String[] split2 = str.split(",");
				for (String s : split2)
				{
					lore.add(FormatUtil.format(s));
				}
			}
			else
			{
				lore.add(FormatUtil.format(str));
			}
		}
		
		ParkourReward reward = new ParkourReward(mat, dat, amt, enchantments, displayName, lore);
		plugin.getParkourRewards().put(pointValue, reward);
	}
	
	public boolean deleteFile(int gameId)
	{
		File folder = new File(plugin.getDataFolder(), "games");
		File file = new File(folder, gameId + ".dat");
		
		try
		{
			file.delete();
		}
		catch (Exception e)
		{
			plugin.outConsole(Level.SEVERE, "Error deleting file \"{0}\":", gameId + ".dat");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void updateSignSave()
	{
		File file = new File(plugin.getDataFolder(), "signs.yml");
		if (file.exists())
		{
			file.delete();
		}
		
		try 
		{ 
			file.createNewFile();
		}
		catch (Exception e) 
		{
			plugin.outConsole(Level.SEVERE, "Could not update sign save: {0}", e.getMessage());
			return;
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
		for (ParkourSign sign : plugin.getSigns())
		{
			String path = "signs." + sign.getId() + ".";

			Location loc = sign.getLocation();
			
			fc.set(path + "gameId", sign.getZone().getGameId());
			
			fc.set(path + "world", loc.getWorld().getName());
			fc.set(path + "x", loc.getBlockX());
			fc.set(path + "y", loc.getBlockY());
			fc.set(path + "z", loc.getBlockZ());
		}
		
		fc.set("total", plugin.getSigns().size());
		
		try 
		{ 
			fc.save(file);
		}
		catch (Exception e) 
		{
			plugin.outConsole(Level.SEVERE, "Could not update sign save: {0}", e.getMessage());
			return;
		}
	}
	
	public void loadSigns()
	{
		File file = new File(plugin.getDataFolder(), "signs.yml");
		if (! file.exists()) return;
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		
		for (int i = 0; i < fc.getInt("total"); i++)
		{
			String path = "signs." + i + ".";
			if (fc.isSet(path))
			{
				World world = plugin.getServer().getWorld(fc.getString(path + "world"));
				if (world != null)
				{
					Location loc = new Location(world, fc.getInt(path + "x"), fc.getInt(path + "y"), fc.getInt(path + "z"));
					if (loc != null)
					{
						ParkourZone zone = plugin.getParkourZone(fc.getInt(path + "gameId"));
						if (zone != null)
						{
							ParkourSign sign = new ParkourSign(plugin, loc, zone, i);
							plugin.getSigns().add(sign);
						}
					}
				}
			}
		}
	}
}