package moreinventory.container;

import moreinventory.inventory.InventoryPotionHolder;
import moreinventory.slot.SlotDisplay;
import moreinventory.slot.SlotPotion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPotionHolder extends Container
{
	private final InventoryPotionHolder holderInventory;

	public ContainerPotionHolder(InventoryPlayer inventory, InventoryPotionHolder holderInventory)
	{
		this.holderInventory = holderInventory;

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				this.addSlotToContainer(new SlotPotion(holderInventory, j + i * 3, 54 + 8 + j * 18, 17 + i * 18));
			}
		}

		this.bindPlayerInventory(inventory);
	}

	protected void bindPlayerInventory(InventoryPlayer inventory)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			if (i != inventory.currentItem)
			{
				addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 138 + 4));
			}
			else
			{
				addSlotToContainer(new SlotDisplay(inventory, i, 8 + i * 18, 138 + 4));
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return holderInventory != null && holderInventory.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(i);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (!holderInventory.isItemValidForSlot(0, itemstack1))
			{
				return null;
			}

			if (i < 9)
			{
				if (!mergeItemStack(itemstack1, 9, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 0, 9, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
}