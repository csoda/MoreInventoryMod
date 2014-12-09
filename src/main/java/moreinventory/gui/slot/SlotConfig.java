package moreinventory.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotConfig extends Slot
{
	public SlotConfig(IInventory inventory, int index, int x, int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public int getSlotStackLimit()
	{
		return 0;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return false;
	}

	@Override
	public void putStack(ItemStack itemstack)
	{
		if (itemstack != null)
		{
			ItemStack itemstack1 = itemstack.copy();
			itemstack1.stackSize = 0;
			inventory.setInventorySlotContents(getSlotIndex(), itemstack1);
		}

		onSlotChanged();
	}

	public void removeItem()
	{
		inventory.setInventorySlotContents(getSlotIndex(), null);
	}
}