package moreinventory.event;

import java.util.ArrayList;
import java.util.List;

import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSItemInvList;
import moreinventory.util.CSWorldSaveHelper;
import moreinventory.util.IWorldDataSave;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventWorldSave
{

	@SubscribeEvent
	public void WorldEnterEvent(WorldEvent.Load event)
	{
		if (!event.world.isRemote)
		{
			if (MoreInventoryMod.saveHelper == null || MoreInventoryMod.saveHelper.world.getSaveHandler().getWorldDirectoryName() != event.world.getSaveHandler().getWorldDirectoryName())
			{
				TileEntityEnderStorageBox.itemList = new CSItemInvList("EnderStorageBoxInv");
				TileEntityEnderStorageBox.enderboxList = new CSItemBoxList(event.world, "EnderStorageBox");
				TileEntityTeleporter.teleporterList = new CSItemBoxList(event.world, "Teleporter");
				List<IWorldDataSave> SaveList = new ArrayList();
				SaveList.add(TileEntityEnderStorageBox.itemList);
				SaveList.add(TileEntityEnderStorageBox.enderboxList);
				SaveList.add(TileEntityTeleporter.teleporterList);
				MoreInventoryMod.saveHelper = new CSWorldSaveHelper(event.world, "MoreInvData", SaveList);
			}
		}
	}

	@SubscribeEvent
	public void WorldExitEvent(WorldEvent.Save event)
	{
		if (!event.world.isRemote && event.world.provider.dimensionId == 0)
		{
			if (MoreInventoryMod.saveHelper != null)
			{
				MoreInventoryMod.saveHelper.saveData();
			}
		}
	}
}