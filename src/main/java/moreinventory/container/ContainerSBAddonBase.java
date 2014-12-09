package moreinventory.container;

import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerSBAddonBase extends Container
{
	protected TileEntitySBAddonBase addonBase;

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
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 138 + 4));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return addonBase != null && addonBase.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		return null;
	}
}