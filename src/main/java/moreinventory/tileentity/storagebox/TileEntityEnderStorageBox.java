package moreinventory.tileentity.storagebox;

import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMItemInvList;
import moreinventory.util.MIMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TileEntityEnderStorageBox extends TileEntityStorageBox
{
	public static MIMItemInvList itemList;
	public static MIMItemBoxList enderBoxList;

	public TileEntityEnderStorageBox()
	{
		super("Ender");
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return slot == 0 && isSameAsContents(itemstack) && !itemstack.hasTagCompound();
	}

	@Override
	public int getContentItemCount()
	{
		contentsCount = itemList.getItemCount(getContents());

		if (storageItems[1] != null)
		{
			contentsCount += storageItems[1].stackSize;
		}

		return contentsCount;
	}

	@Override
	public void markDirty()
	{
		updateStorageItems();

		ItemStack itemstack = itemList.updateState(itemList.getItemIndex(getContents()));

		if (itemstack != null)
		{
			MIMUtils.dropItem(worldObj, itemstack, xCoord, yCoord, zCoord);
		}

		updateDisplayedSize();

		super.markDirty();
	}

	public void updateDisplayedSize()
	{
		for (int i = 0; i < enderBoxList.getListSize(); ++i)
		{
			if (isSameAsContents(enderBoxList.getItem(i)))
			{
				World world = DimensionManager.getWorld(enderBoxList.getDimensionID(i));

				if (world != null)
				{
					int[] pos = enderBoxList.getBoxPos(i);
					TileEntity tile = world.getTileEntity(pos[0], pos[1], pos[2]);

					if (tile != null && tile instanceof TileEntityEnderStorageBox)
					{
						((TileEntityStorageBox)tile).sendPacket();
					}
					else
					{
						enderBoxList.removeBox(i);
					}
				}
			}
		}
	}

	@Override
	public boolean registerItems(ItemStack itemstack)
	{
		if (super.registerItems(itemstack))
		{
			updateStorageItems();
			markDirty();

			return true;
		}

		return false;
	}

	public void updateStorageItems()
	{
		if(storageItems != itemList.getInventory(getContents()))
		{
			if(worldObj != null)
			{
				enderBoxList.registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getContents());
				itemList.registerItem(getContents());
			}
			ItemStack[] oldStorage = storageItems.clone();
			storageItems = itemList.getInventory(getContents());
			tryPutIn(oldStorage[1]);
		}
	}
}