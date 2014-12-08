package moreinventory.container;

import moreinventory.tileentity.TileEntityCatchall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCatchall extends Container
{
	protected TileEntityCatchall tileEntity;

	public ContainerCatchall(InventoryPlayer inventoryPlayer, TileEntityCatchall te)
	{
		tileEntity = te;

		// the Slot constructor takes the IInventory and the slot number in that it binds to
		// and the x-y coordinates it resides on-screen
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(tileEntity, i, 8 + i * 18, 72 + 4));
		}

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(tileEntity, j + i * 9 + 9, 8 + j * 18, 18 + i * 18));
			}
		}

		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tileEntity.isUseableByPlayer(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 108 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 162 + 4));
		}
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

			if (i < 36)
			{
				if (!mergeItemStack(itemstack1, 36, inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!mergeItemStack(itemstack1, 0, 36, false))
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