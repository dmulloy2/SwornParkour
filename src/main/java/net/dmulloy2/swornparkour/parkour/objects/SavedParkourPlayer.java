package net.dmulloy2.swornparkour.parkour.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class SavedParkourPlayer 
{
	private String name;
	private Location spawnback;
	private List<ItemStack> items = new ArrayList<ItemStack>();
	private List<ItemStack> armor = new ArrayList<ItemStack>();
	
	public SavedParkourPlayer(String name, Location spawnback)
	{
		this.name = name;
		this.spawnback = spawnback;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Location getSpawnBack()
	{
		return spawnback;
	}
	
	public List<ItemStack> getItems()
	{
		return items;
	}
	
	public void addItem(ItemStack itemStack)
	{
		items.add(itemStack);
	}
	
	public List<ItemStack> getArmor()
	{
		return armor;
	}
	
	public void addArmor(ItemStack itemStack)
	{
		armor.add(itemStack);
	}
}