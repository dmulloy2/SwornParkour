package net.dmulloy2.swornparkour.commands;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.dmulloy2.swornparkour.SwornParkour;
import net.dmulloy2.swornparkour.util.InventoryWorkaround;

/**
 * @author dmulloy2
 */

public class CmdRedeem extends SwornParkourCommand
{
	public CmdRedeem(SwornParkour plugin)
	{
		super(plugin);
		this.name = "claim";
		this.description = "Claim parkour rewards";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (!getManager().redemption.containsKey(player.getName()))
		{
			err("&cYou have no rewards to claim!");
			return;
		}
		
		List<ItemStack> items = getManager().redemption.get(player.getName());
		
		for (ItemStack item : items)
		{
			InventoryWorkaround.addItems(player.getInventory(), item);
		}
	}
}
