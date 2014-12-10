package moreinventory.tileentity.storagebox;

import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMItemInvList;
import moreinventory.util.MIMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TileEntityEnderStorageBox extends TileEntityStorageBox
{
	public static MIMItemInvList itemList;
	public static MIMItemBoxList enderBoxList;

	public TileEntityEnderStorageBox()
	{
		super(StorageBoxType.Ender);
	}

	public ItemStack[] getInv()
	{
		if (itemList != null)
		{
			return itemList.getInv(getContents());
		}

		return new ItemStack[2];
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return slot == 0 && itemstack != null && itemstack.getItem() == getContentsItem() && itemstack.getItemDamage() == getContentsDamage() && !itemstack.hasTagCompound();
	}

	@Override
	public int getContentItemCount()
	{
		int count = itemList.getItemCount(getContents());

		if (storageItems[1] != null)
		{
			count += storageItems[1].stackSize;
		}

		contentsCount = count;

		return contentsCount;
	}

	@Override
	public void markDirty()
	{
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
		for (int i = 0; i < enderBoxList.getListSize(); i++)
		{
			if (MIMUtils.compareStacksWithDamage(enderBoxList.getItem(i), getContents()))
			{
				int[] pos = enderBoxList.getBoxPos(i);
				World world = DimensionManager.getWorld(enderBoxList.getDimensionID(i));

				if (world != null)
				{
					TileEntity tile = world.getTileEntity(pos[0], pos[1], pos[2]);

					if (tile != null && tile instanceof TileEntityEnderStorageBox)
					{
						((TileEntityStorageBox)tile).getContentItemCount();
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
		boolean result = super.registerItems(itemstack);

		enderBoxList.registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getContents());
		itemList.registerItem(getContents());

		storageItems = itemList.getInv(getContents());

		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		storageItems = getInv();
	}
}