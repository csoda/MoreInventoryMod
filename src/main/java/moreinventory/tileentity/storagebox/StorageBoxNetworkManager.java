package moreinventory.tileentity.storagebox;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InvPouch;
import moreinventory.util.CSBoxList;
import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSutil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class StorageBoxNetworkManager 
{
	private String ownerName;
	private CSItemBoxList StorageBoxList;
	private CSBoxList AddonList;
	
	public StorageBoxNetworkManager(World world ,int x, int y, int z , String name){
		ownerName = name;
		StorageBoxList = new CSItemBoxList();
		AddonList = new CSBoxList();
		createNetwork(new CSBoxList() , world , x, y, z);
	}
	
	public CSItemBoxList getBoxList(){
		
		return StorageBoxList;
	}
	
	public CSBoxList getAddonList(){
		
		return AddonList;
	}
	
	public CSBoxList getKnownList(){
		CSBoxList knownList = new CSBoxList();
		knownList.addAllBox(StorageBoxList);
		knownList.addAllBox(AddonList);
		return knownList;
	}
	
	
	/*** Network ***/
	
	private void createNetwork(CSBoxList knownList ,World world ,int x,int y,int z){
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile instanceof IStorageBoxNet)
		{
			IStorageBoxNet itile = (IStorageBoxNet)tile;
			if(knownList.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId))
			{
				if(!itile.isPrivate()||itile.getOwnerName().equals(MoreInventoryMod.defaultOwner)||itile.getOwnerName().equals(this.ownerName))
				{
					if(tile instanceof TileEntityStorageBox)
					{
						StorageBoxList.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId, ((TileEntityStorageBox)tile).getContents());
					}
					if(itile instanceof IStorageBoxAddon)
					{
						AddonList.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);
					}
					itile.setStorageBoxNetworkManager(this);
				}
			}
			
			int[] pos;
			for(int i = 0; i < 6;i++)
			{
				pos = CSutil.getSidePos(x,y,z,i);
				if(!knownList.isOnBoxList(pos[0], pos[1], pos[2], world.provider.dimensionId))
				{
					createNetwork(knownList, world, pos[0], pos[1], pos[2]);
				}
			}
			
		}
	}
	
	public void addNetwork(World world ,int x,int y,int z ){
		CSBoxList knownList = getKnownList();
		createNetwork(knownList, world, x, y, z);	
	}
	
	public void reCreateNetwork(){
		CSBoxList knownList = getKnownList();
		int ssss = 0;
		while (knownList.getListSize() > 0)	
		{
			if(knownList.getTileBeyondDim(0)==null){
				knownList.removeBox(0);
			}
			else
			{
				int[] pos = knownList.getBoxPos(0);
				World world = DimensionManager.getWorld(knownList.getDimensionID(0));
				StorageBoxNetworkManager newNet = new StorageBoxNetworkManager(world , pos[0], pos[1], pos[2],ownerName);
				knownList = knownList.getDifference(newNet.getKnownList());
				
			}
			ssss++;
			if(ssss > 100) break;
		}
	}
	
	public CSBoxList getMatchingList(ItemStack itemstack){
		
		CSBoxList retList = new CSBoxList();
		int size = StorageBoxList.getListSize();
		
		if(itemstack!=null){
			for(int i = 0; i < size;i++)
			{
				if(CSutil.compareStacksWithDamage(itemstack,StorageBoxList.getItem(i)))
				{
					TileEntityStorageBox tile = (TileEntityStorageBox)StorageBoxList.getTileBeyondDim(i);
					retList.addBox(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj().provider.dimensionId);
				}
			}
		}
		return retList;
	}
	
	
	/*** StorageBox ***/
	
	public void linkedCollect(IInventory iinv){
		
		for(int i = 0; i < iinv.getSizeInventory(); i++)
		{
			ItemStack item = iinv.getStackInSlot(i);
			if(item!=null)
			{
				if(item.getItem() ==  MoreInventoryMod.Pouch)
				{
					InvPouch pouch = new InvPouch(item);
					if(pouch.isCollectedByBox)
					{
						pouch.linkedPutIn(this);
					}
				}
				else
				{
					linkedPutIn(item, null,false);
				}
			}
		}
	}
	

	public boolean linkedPutIn(ItemStack itemstack, TileEntityStorageBox parTile, boolean isRegister){
		
		CSBoxList list = this.getMatchingList(itemstack);
		for(int i = 0; i < list.getListSize();i++)
		{
			TileEntityStorageBox tile = (TileEntityStorageBox) list.getTileBeyondDim(i);
			if(tile!=parTile&&!tile.isFull())
			{
				tile.tryPutIn(itemstack);
				if(itemstack ==null||itemstack.stackSize==0)
				{
					return true;
				}
			}
		}
		
		if(isRegister)
		{
			int size = StorageBoxList.getListSize();		
			for(int i = 0; i < size;i++)
			{
				if(StorageBoxList.getItem(i) == null)
				{
					TileEntityStorageBox tile = (TileEntityStorageBox)StorageBoxList.getTileBeyondDim(i);
					tile.registerItems(itemstack);
					tile.tryPutIn(itemstack);
					if(itemstack == null)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected boolean canLinkedImport(ItemStack itemstack, TileEntityStorageBox parTile){
		int size = StorageBoxList.getListSize();
		for(int i = 0; i < size;i++)
		{
			if(CSutil.compareStacksWithDamage(itemstack,StorageBoxList.getItem(i)))
			{
				TileEntityStorageBox tile = (TileEntityStorageBox)StorageBoxList.getTileBeyondDim(i);
				if(tile != parTile && tile.canMergeItemStack(itemstack))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void updateOnInvChanged(World world, int x, int y, int z, ItemStack item){

	}
	
	public void updateOnTripleClicked(World world, int x, int y, int z, ItemStack item){

	}
	
}
