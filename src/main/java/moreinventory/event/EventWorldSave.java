package moreinventory.event;

import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSItemInvList;
import moreinventory.util.CSWorldSaveHelper;
import net.minecraftforge.event.world.WorldEvent;

import com.google.common.collect.Lists;

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
				TileEntityEnderStorageBox.enderboxList = new CSItemBoxList("EnderStorageBox");
				TileEntityTeleporter.teleporterList = new CSItemBoxList("Teleporter");
				MoreInventoryMod.saveHelper = new CSWorldSaveHelper(event.world, "MoreInvData", Lists.newArrayList(TileEntityEnderStorageBox.itemList, TileEntityEnderStorageBox.enderboxList, TileEntityTeleporter.teleporterList));
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