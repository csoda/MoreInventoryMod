package moreinventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
			ItemStack item = new ItemStack(itemstack.getItem(), 1, itemstack.getItemDamage());

			if (itemstack.getTagCompound() != null)
			{
				item.setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
			}

			inventory.setInventorySlotContents(getSlotIndex(), item);
		}

		onSlotChanged();
	}

	public void removeItem()
	{
		inventory.setInventorySlotContents(getSlotIndex(), null);
	}
}