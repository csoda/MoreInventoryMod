package moreinventory.plugin.appeng;

import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.TileEntityGlassStorageBox;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntitySBAddonBase;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class AEExternalStorageHandler implements IExternalStorageHandler
{
	@Override
	public boolean canHandle(TileEntity tile, ForgeDirection d, StorageChannel channel, BaseActionSource mySrc)
	{
		return tile instanceof TileEntityStorageBox || tile instanceof TileEntityTeleporter;
	}

	@Override
	public IMEInventory getInventory(TileEntity tile, ForgeDirection d, StorageChannel channel, BaseActionSource src)
	{
        /*
		if (tile instanceof TileEntityEnderStorageBox)
		{
			return new MEInventoryEnderStorageBox((TileEntityEnderStorageBox)tile);
		}
        */
		if (tile instanceof TileEntityTeleporter)
		{
			return new MEInventoryStorageBoxNetwork((TileEntitySBAddonBase)tile);
		}

		if (tile instanceof TileEntityStorageBox)
		{
			return new MEInventoryStorageBox((TileEntityStorageBox)tile);
		}

		return null;
	}
}