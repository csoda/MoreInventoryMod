package moreinventory.slot;

import moreinventory.inventory.InventoryPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPouchConfig extends Slot
{
	public SlotPouchConfig(IInventory inventory, int index, int x, int y)
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
		ItemStack item = null;

		if (itemstack != null)
		{
			item = itemstack.copy();
			item.stackSize = 0;
		}

		((InventoryPouch)inventory).setConfigItem(getSlotIndex(), item);
		onSlotChanged();
	}

	public void removeItem()
	{
		inventory.setInventorySlotContents(this.getSlotIndex(), (ItemStack) null);
	}
}