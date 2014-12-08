package moreinventory.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPotion extends Slot
{
	private IInventory inv;

	public SlotPotion(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
		inv = par1iInventory;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return inv.isItemValidForSlot(0, par1ItemStack);
	}
}