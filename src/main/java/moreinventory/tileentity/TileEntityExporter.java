package moreinventory.tileentity;

import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityExporter extends TileEntityTransportManager
{
	int[] boxPos = new int[3];
	int nowSlot = 0;

	@Override
	protected void doExtract()
	{
		int[] pos = CSUtil.getSidePos(this.xCoord, this.yCoord, this.zCoord, topFace);
		IInventory iinv = TileEntityHopper.func_145893_b(this.worldObj, pos[0], pos[1], pos[2]);
		if (iinv != null)
		{
			extract: for (int i = 0; i < 9; i++)
			{
				ItemStack itemstack = inv[nowSlot];
				if (++nowSlot == 9)
					nowSlot = 0;
				if (itemstack != null && getBoxPos(itemstack))
				{
					TileEntityStorageBox btile = (TileEntityStorageBox) this.worldObj.getTileEntity(boxPos[0],
							boxPos[1], boxPos[2]);
					int k = btile.getSizeInventory();
					for (int t = 0; t < k; t++)
					{
						ItemStack itemstack1 = btile.getStackInSlot(t);
						if (itemstack1 != null)
						{
							if (CSUtil.mergeItemStack(itemstack1, iinv, getSneak(topFace)))
							{
								CSUtil.checkNullStack(btile, t);
								break extract;
							}
						}
					}
				}
			}
		}
	}

	private boolean getBoxPos(ItemStack itemstack)
	{
		int[] pos = CSUtil.getSidePos(this.xCoord, this.yCoord, this.zCoord, face);
		TileEntity tile = this.worldObj.getTileEntity(pos[0], pos[1], pos[2]);
		if (tile instanceof TileEntityStorageBox)
		{
			CSItemBoxList list = ((TileEntityStorageBox) tile).getStorageBoxNetworkManager().getBoxList();
			int[] pos2 = new int[3];
			int k = list.getListSize();
			for (int i = 0; i < k; i++)
			{
				if (CSUtil.compareStacksWithDamage(itemstack, list.getItem(i)))
				{
					pos2 = list.getBoxPos(i);
					TileEntityStorageBox btile = (TileEntityStorageBox) this.worldObj.getTileEntity(pos2[0], pos2[1],
							pos2[2]);
					if (btile.getContentItemCount() > 0)
					{
						boxPos[0] = pos2[0];
						boxPos[1] = pos2[1];
						boxPos[2] = pos2[2];
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntityExporter";
	}
}