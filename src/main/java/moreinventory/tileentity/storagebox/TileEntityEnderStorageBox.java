package moreinventory.tileentity.storagebox;

import moreinventory.util.CSItemBoxList;
import moreinventory.util.CSItemInvList;
import moreinventory.util.CSutil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TileEntityEnderStorageBox extends TileEntityStorageBox{

	public static CSItemInvList itemList;
	public static CSItemBoxList enderboxList;

	public TileEntityEnderStorageBox(){
		super(StorageBoxType.Ender);
	}
	public ItemStack[] getInv(){
        if(this.itemList != null){
           return this.itemList.getInv(getContents());
        }
        return new ItemStack[2];
    }

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack){
    	if(par1 == 0){
    		return par2ItemStack != null && par2ItemStack.getItem() == this.getContentsItem() && par2ItemStack.getItemDamage()==this.getContentsDamage()&&!par2ItemStack.hasTagCompound();
    	}
    	else
    	{
    		return false;
    	}
    }

    @Override
	public int getContentItemCount(){
		int t = this.itemList.getItemCount(this.getContents());
		if(inv[1] != null){
			t += inv[1].stackSize;
		}
		this.ContentsItemCount = t;
		return this.ContentsItemCount;
	}

    @Override
    public void markDirty()
    {
		ItemStack itemstack  = this.itemList.updateState(this.itemList.getItemIndex(getContents()));
		if(itemstack != null){
			CSutil.dropItem(worldObj, itemstack, xCoord, yCoord, zCoord);
		}
		updateDisplayedSize();
    	super.markDirty();
    }


	public void updateDisplayedSize(){
		int k = this.enderboxList.getListSize();
		for(int i = 0; i < k; i++){
			if(CSutil.compareStacksWithDamage(enderboxList.getItem(i) , getContents())){
				int[] pos = enderboxList.getBoxPos(i);
                World world = DimensionManager.getWorld(enderboxList.getDimensionID(i));
                if(world != null){
                    TileEntity tile = world.getTileEntity(pos[0], pos[1], pos[2]);
                    if(tile != null&&tile instanceof TileEntityEnderStorageBox){
                        ((TileEntityStorageBox)tile).getContentItemCount();
                        ((TileEntityStorageBox)tile).sendPacket();
                    }
                    else
                    {
                        this.enderboxList.removeBox(i);
                    }
                }
			}
		}
	}

	@Override
	public boolean registerItems(ItemStack itemstack){
		boolean ret = super.registerItems(itemstack);
		int dimID = worldObj.provider.dimensionId;
		enderboxList.registerItem(xCoord, yCoord, zCoord, dimID, getContents());
		itemList.registerItem(getContents());
		this.inv = this.itemList.getInv(getContents());
		return ret;
	}
	
	@Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.inv = getInv();
    }

}
