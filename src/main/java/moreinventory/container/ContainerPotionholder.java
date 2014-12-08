package moreinventory.container;

import moreinventory.gui.slot.SlotPotion;
import moreinventory.item.inventory.InventoryPotionholder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPotionholder extends Container
{
	private InventoryPotionholder inv;

	public ContainerPotionholder(InventoryPlayer inventoryPlayer, InventoryPotionholder te)
	{
		inv = te;

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				addSlotToContainer(new SlotPotion(inv, j + i * 3, 54 + 8 + j * 18, 17 + i * 18));
			}
		}

		bindPlayerInventory(inventoryPlayer);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 138 + 4));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return inv.isUseableByPlayer(player);
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
			if (!inv.isItemValidForSlot(0, itemstack1))
				return null;
			if (i < 9)
			{
				if (!mergeItemStack(itemstack1, 9, this.inventorySlots.size(), true))
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
				slot.putStack((ItemStack) null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

}
