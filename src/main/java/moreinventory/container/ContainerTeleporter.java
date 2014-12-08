package moreinventory.container;

import moreinventory.gui.slot.SlotConfig;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerTeleporter extends ContainerSBAddonBase
{

	public ContainerTeleporter(InventoryPlayer player, TileEntityTeleporter tile)
	{
		this.tile = tile;
		addSlotToContainer(new SlotConfig(tile, 0, 80, 35));
		bindPlayerInventory(player);
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
	{
		ItemStack retItemStack = null;
		if (par1 == 0)
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
			((TileEntityTeleporter) tile).updateConnect();
		}
		else
		{
			retItemStack = super.slotClick(par1, par2, par3, par4EntityPlayer);

		}

		return retItemStack;
	}
}
