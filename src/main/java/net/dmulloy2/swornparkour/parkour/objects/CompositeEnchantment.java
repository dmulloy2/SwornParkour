package net.dmulloy2.swornparkour.parkour.objects;

import org.bukkit.enchantments.Enchantment;

/**
 * @author dmulloy2
 */

public class CompositeEnchantment
{
	private final Enchantment enchantment;
	private final int level;
	
	public CompositeEnchantment(final Enchantment enchantment, final int level)
	{
		this.enchantment = enchantment;
		this.level = level;
	}
	
	public Enchantment getType()
	{
		return enchantment;
	}
	
	public int getLevel()
	{
		return level;
	}
}