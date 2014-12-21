package moreinventory.slot;

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
		return 1;
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
			ItemStack item = itemstack.copy();
			item.stackSize = 1;

			inventory.setInventorySlotContents(getSlotIndex(), item);
		}

		onSlotChanged();
	}

	public void removeItem()
	{
		inventory.setInventorySlotContents(getSlotIndex(), null);
	}
}