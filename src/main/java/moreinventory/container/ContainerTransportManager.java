package moreinventory.container;

import moreinventory.slot.SlotConfig;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTransportManager extends Container
{
	private final TileEntityTransportManager transportManager;

	public ContainerTransportManager(InventoryPlayer inventory, TileEntityTransportManager tile)
	{
		this.transportManager = tile;

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				this.addSlotToContainer(new SlotConfig(tile, j + i * 3, 54 + 8 + j * 18, 17 + i * 18));
			}
		}

		this.bindPlayerInventory(inventory);
	}

	protected void bindPlayerInventory(InventoryPlayer player)
	{
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(player, i, 8 + i * 18, 138 + 4));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return transportManager != null && transportManager.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		return null;
	}

	@Override
	public ItemStack slotClick(int index, int button, int modifiers, EntityPlayer player)
	{
		if (0 <= index && index < 9)
		{
			SlotConfig slot = (SlotConfig)inventorySlots.get(index);

			if (button == 0)
			{
				slot.putStack(player.inventory.getItemStack());
			}
			else
			{
				slot.removeItem();
			}
		}
		else return super.slotClick(index, button, modifiers, player);

		return null;
	}
}