package moreinventory.container;

import moreinventory.slot.SlotConfig;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerTeleporter extends ContainerSBAddonBase
{
	public ContainerTeleporter(InventoryPlayer player, TileEntityTeleporter tile)
	{
		this.addonBase = tile;
		this.addSlotToContainer(new SlotConfig(tile, 0, 80, 35));
		this.bindPlayerInventory(player);
	}

	@Override
	public ItemStack slotClick(int index, int button, int modifiers, EntityPlayer player)
	{

		if (index == 0)
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

			if (!player.worldObj.isRemote)
			{
				((TileEntityTeleporter)addonBase).updateConnect();
			}
		}
		else return super.slotClick(index, button, modifiers, player);

		return null;
	}
}