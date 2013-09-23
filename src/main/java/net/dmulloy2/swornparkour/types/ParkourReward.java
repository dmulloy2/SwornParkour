package net.dmulloy2.swornparkour.types;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class ParkourReward
{
	private Material type;
	private short data;
	private int amount;
	private Map<Enchantment, Integer> enchantments;
	private String displayName;
	private List<String> lore;
	
	public ParkourReward(Material type, short data, int amount, Map<Enchantment, Integer> enchantments, String displayName, List<String> lore)
	{
		this.type = type;
		this.data = data;
		this.amount = amount;
		this.enchantments = enchantments;
		this.displayName = displayName;
		this.lore = lore;
	}

	public ItemStack getItemStack()
	{
		ItemStack stack = new ItemStack(type, amount, data);
		
		if (! enchantments.isEmpty())
		{
			for (Entry<Enchantment, Integer> enchantment : enchantments.entrySet())
			{
				Enchantment ench = enchantment.getKey();
				int level = enchantment.getValue();
				
				if (ench != null && level > 0)
				{
					stack.addUnsafeEnchantment(ench, level);
				}
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		if (! displayName.isEmpty())
		{
			meta.setDisplayName(displayName);
		}
		
		if (! lore.isEmpty())
		{
			meta.setLore(lore);
		}
		
		stack.setItemMeta(meta);
		
		return stack;
	}
}