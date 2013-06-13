package net.dmulloy2.swornparkour.parkour.objects;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * @author dmulloy2
 */

public class ParkourReward
{
	private int itemId;
	private byte data;
	private int amount;
	private List<CompositeEnchantment> enchantments;
	private String displayName;
	private List<String> lore;
	
	public ParkourReward(int itemId, byte data, int amount, List<CompositeEnchantment> enchantments, String displayName, List<String> lore)
	{
		this.itemId = itemId;
		this.data = data;
		this.amount = amount;
		this.enchantments = enchantments;
		this.displayName = displayName;
		this.lore = lore;
	}
	
	public ItemStack getItemStack()
	{
		ItemStack stack = new ItemStack(itemId, amount);
		if (data > 0)
		{
			MaterialData materialData = stack.getData();
			materialData.setData(data);
			stack.setData(materialData);
		}
		
		if (enchantments.size() > 0)
		{
			for (CompositeEnchantment enchantment : enchantments)
			{
				Enchantment enchant = enchantment.getType();
				int level = enchantment.getLevel();
				
				stack.addUnsafeEnchantment(enchant, level);
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		if (displayName != "")
		{
			meta.setDisplayName(displayName);
		}
		
		if (lore.size() > 0)
		{
			meta.setLore(lore);
		}
		
		stack.setItemMeta(meta);
		
		return stack;
	}
}