package moreinventory.gui.slot;

import moreinventory.MoreInventoryMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPouch2 extends Slot
{
	public SlotPouch2(IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack == null || itemstack.getItem() != MoreInventoryMod.Pouch;
	}
}