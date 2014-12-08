package moreinventory.tileentity.storagebox;

import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSItemInvList;
import moreinventory.util.CSUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TileEntityEnderStorageBox extends TileEntityStorageBox
{
	public static CSItemInvList itemList;
	public static CSItemBoxList enderboxList;

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
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	{
		if (par1 == 0)
		{
			return par2ItemStack != null && par2ItemStack.getItem() == this.getContentsItem()
					&& par2ItemStack.getItemDamage() == this.getContentsDamage() && !par2ItemStack.hasTagCompound();
		}

		return false;
	}

	@Override
	public int getContentItemCount()
	{
		int t = itemList.getItemCount(this.getContents());
		if (inv[1] != null)
		{
			t += inv[1].stackSize;
		}
		this.ContentsItemCount = t;
		return this.ContentsItemCount;
	}

	@Override
	public void markDirty()
	{
		ItemStack itemstack = itemList.updateState(itemList.getItemIndex(getContents()));
		if (itemstack != null)
		{
			CSUtil.dropItem(worldObj, itemstack, xCoord, yCoord, zCoord);
		}
		updateDisplayedSize();
		super.markDirty();
	}

	public void updateDisplayedSize()
	{
		int k = TileEntityEnderStorageBox.enderboxList.getListSize();
		for (int i = 0; i < k; i++)
		{
			if (CSUtil.compareStacksWithDamage(enderboxList.getItem(i), getContents()))
			{
				int[] pos = enderboxList.getBoxPos(i);
				World world = DimensionManager.getWorld(enderboxList.getDimensionID(i));
				if (world != null)
				{
					TileEntity tile = world.getTileEntity(pos[0], pos[1], pos[2]);
					if (tile != null && tile instanceof TileEntityEnderStorageBox)
					{
						((TileEntityStorageBox) tile).getContentItemCount();
						((TileEntityStorageBox) tile).sendPacket();
					}
					else
					{
						TileEntityEnderStorageBox.enderboxList.removeBox(i);
					}
				}
			}
		}
	}

	@Override
	public boolean registerItems(ItemStack itemstack)
	{
		boolean ret = super.registerItems(itemstack);
		int dimID = worldObj.provider.dimensionId;
		enderboxList.registerItem(xCoord, yCoord, zCoord, dimID, getContents());
		itemList.registerItem(getContents());
		this.inv = itemList.getInv(getContents());
		return ret;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.inv = getInv();
	}
}