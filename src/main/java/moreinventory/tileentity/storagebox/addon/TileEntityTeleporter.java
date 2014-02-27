package moreinventory.tileentity.storagebox.addon;

import moreinventory.tileentity.storagebox.StorageBoxNetworkManager;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSutil;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TileEntityTeleporter extends TileEntitySBAddonBase{

	public static CSItemBoxList teleporterList;
	
	
	public TileEntityTeleporter(){
		inv = new ItemStack[1];
	}
    public String getInventoryName(){
        return "TileEntityTeleporter";
    }

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager SBNetManager)
	{
		super.setStorageBoxNetworkManager(SBNetManager);
		teleportConnect();
	}
	
	public void updateConnect()
	{
        ItemStack itemstack = getStackInSlot(0);
		int dimID = worldObj.provider.dimensionId;
		this.teleporterList.registerItem(xCoord, yCoord, zCoord, dimID, itemstack);
		getStorageBoxNetworkManager().reCreateNetwork();
	}
	
	private void teleportConnect()
	{
		ItemStack itemstack = this.getStackInSlot(0);
		int k = this.teleporterList.getListSize();
		for(int i = 0; i < k; i++){
			if(itemstack != null && CSutil.compareStacksWithDamage(teleporterList.getItem(i) , itemstack)){
				int[] pos = teleporterList.getBoxPos(i);
				TileEntity tile = DimensionManager.getWorld(teleporterList.getDimensionID(i)).getTileEntity(pos[0], pos[1], pos[2]);
				if(tile != null && tile instanceof TileEntityTeleporter){
					if(tile!=this){
						getStorageBoxNetworkManager().addNetwork(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
					}
				}
				else
				{
					this.teleporterList.removeBox(i);
				}
			}
		}
	}
}
