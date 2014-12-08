package moreinventory.container;

import moreinventory.gui.slot.SlotConfig;
import moreinventory.tileentity.TileEntityTransportManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTransportManager extends Container
{
	protected TileEntityTransportManager tileEntity;

	public ContainerTransportManager(InventoryPlayer inventoryPlayer, TileEntityTransportManager te)
	{
		tileEntity = te;

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				addSlotToContainer(new SlotConfig(tileEntity, j + i * 3, 54 + 8 + j * 18, 17 + i * 18));
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
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 84 + i * 18));
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
		return tileEntity.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		return null;
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
	{
		ItemStack retItemStack = null;
		if (0 <= par1 && par1 < 9)
		{
			SlotConfig slot1 = (SlotConfig) this.inventorySlots.get(par1);
			if (par2 == 0)
			{
				slot1.putStack(par4EntityPlayer.inventory.getItemStack());
			}
			else
			{
				slot1.removeItem();
			}
		}
		else
		{
			retItemStack = super.slotClick(par1, par2, par3, par4EntityPlayer);

		}
		return retItemStack;
	}
}