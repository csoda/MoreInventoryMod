package moreinventory.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moreinventory.MoreInventoryMod;
import moreinventory.tileentity.storagebox.TileEntityEnderStorageBox;
import moreinventory.tileentity.storagebox.addon.TileEntityTeleporter;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSItemInvList;
import moreinventory.util.CSWorldSaveHelper;
import moreinventory.util.IWorldDataSave;
import net.minecraftforge.event.world.WorldEvent;

import java.util.ArrayList;
import java.util.List;

public class EventWorldSave {

    @SubscribeEvent
	public void WorldEnterEvent(WorldEvent.Load event)
	{
		if(!event.world.isRemote){
			if(MoreInventoryMod.saveHelper == null || MoreInventoryMod.saveHelper.world.getSaveHandler().getWorldDirectoryName() != event.world.getSaveHandler().getWorldDirectoryName())
			{
				TileEntityEnderStorageBox.itemList = new CSItemInvList("EnderStorageBoxInv");
				TileEntityEnderStorageBox.enderboxList = new CSItemBoxList(event.world,"EnderStorageBox");
				TileEntityTeleporter.teleporterList = new CSItemBoxList(event.world,"Teleporter");
				
				List<IWorldDataSave> SaveList = new ArrayList();
				SaveList.add((IWorldDataSave) TileEntityEnderStorageBox.itemList);
				SaveList.add((IWorldDataSave) TileEntityEnderStorageBox.enderboxList);
				SaveList.add((IWorldDataSave) TileEntityTeleporter.teleporterList);
				MoreInventoryMod.saveHelper = new CSWorldSaveHelper(event.world, "MoreInvData" ,SaveList);
			}
		}
	}

    @SubscribeEvent
	public void WorldExitEvent(WorldEvent.Save event)
	{
		if(!event.world.isRemote&&event.world.provider.dimensionId==0){
			if(MoreInventoryMod.saveHelper != null)
			{
				MoreInventoryMod.saveHelper.saveData();
			}
		}
	}
}
