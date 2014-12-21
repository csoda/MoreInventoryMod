package moreinventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDisplay extends Slot
{
	public SlotDisplay(IInventory inventory, int index, int x,int y)
	{
		super(inventory, index, x, y);
	}

	@Override
	public ItemStack getStack()
	{
		ItemStack itemstack = inventory.getStackInSlot(getSlotIndex());
		ItemStack result = null;

		if (itemstack != null)
		{
			result = itemstack.copy();
			result.stackSize = 1;
		}

		return result;
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
}