package moreinventory.gui.slot;

import moreinventory.core.MoreInventoryMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPouch2 extends Slot
{
	public SlotPouch2(IInventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return itemstack == null || itemstack.getItem() != MoreInventoryMod.Pouch;
	}
}