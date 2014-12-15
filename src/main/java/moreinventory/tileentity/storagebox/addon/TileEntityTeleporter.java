package moreinventory.tileentity.storagebox.addon;

import moreinventory.tileentity.storagebox.StorageBoxNetworkManager;
import moreinventory.util.MIMItemBoxList;
import moreinventory.util.MIMUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TileEntityTeleporter extends TileEntitySBAddonBase
{
	public static final MIMItemBoxList teleporterList = new MIMItemBoxList("Teleporter");

	public TileEntityTeleporter()
	{
		this.storageItems = new ItemStack[1];
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntityTeleporter";
	}

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager manager)
	{
		super.setStorageBoxNetworkManager(manager);

		teleportConnect();
	}

	public void updateConnect()
	{
		teleporterList.registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getStackInSlot(0));

		getStorageBoxNetworkManager().recreateNetwork();
	}

	private void teleportConnect()
	{
		ItemStack itemstack = getStackInSlot(0);

		for (int i = 0; i < teleporterList.getListSize(); i++)
		{
			if (itemstack != null && MIMUtils.compareStacksWithDamage(teleporterList.getItem(i), itemstack))
			{
				int[] pos = teleporterList.getBoxPos(i);
				TileEntity tile = DimensionManager.getWorld(teleporterList.getDimensionID(i)).getTileEntity(pos[0], pos[1], pos[2]);

				if (tile != null && tile instanceof TileEntityTeleporter)
				{
					if (tile != this)
					{
						getStorageBoxNetworkManager().addNetwork(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
					}
				}
				else
				{
					teleporterList.removeBox(i);
				}
			}
		}
	}
}