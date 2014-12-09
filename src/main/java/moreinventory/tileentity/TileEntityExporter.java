package moreinventory.tileentity;

import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityExporter extends TileEntityTransportManager
{
	private final int[] boxPos = new int[3];
	private int currentSlot = 0;

	@Override
	protected void doExtract()
	{
		int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, topFace);
		IInventory inventory = TileEntityHopper.func_145893_b(worldObj, pos[0], pos[1], pos[2]);

		if (inventory != null)
		{
			extract: for (int i = 0; i < 9; i++)
			{
				ItemStack itemstack = inventoryItems[currentSlot];

				if (++currentSlot == 9)
				{
					currentSlot = 0;
				}

				if (itemstack != null && getBoxPos(itemstack))
				{
					TileEntityStorageBox tile = (TileEntityStorageBox)worldObj.getTileEntity(boxPos[0], boxPos[1], boxPos[2]);

					for (int j = 0; j < tile.getSizeInventory(); j++)
					{
						ItemStack itemstack1 = tile.getStackInSlot(j);

						if (itemstack1 != null)
						{
							if (MIMUtils.mergeItemStack(itemstack1, inventory, getSneak(topFace)))
							{
								MIMUtils.checkNullStack(tile, j);

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
		int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, face);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

		if (tile != null && tile instanceof TileEntityStorageBox)
		{
			MIMItemBoxList list = ((TileEntityStorageBox)tile).getStorageBoxNetworkManager().getBoxList();

			for (int i = 0; i < list.getListSize(); i++)
			{
				if (MIMUtils.compareStacksWithDamage(itemstack, list.getItem(i)))
				{
					pos = list.getBoxPos(i);
					TileEntityStorageBox storageBox = (TileEntityStorageBox)worldObj.getTileEntity(pos[0], pos[1], pos[2]);

					if (storageBox.getContentItemCount() > 0)
					{
						boxPos[0] = pos[0];
						boxPos[1] = pos[1];
						boxPos[2] = pos[2];

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