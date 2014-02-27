package moreinventory.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotConfig extends Slot{
		IInventory iinv;

	public SlotConfig(IInventory par1iInventory, int par2, int par3,int par4) {
		super(par1iInventory, par2, par3, par4);
		iinv = this.inventory;
	}

	@Override
    public int getSlotStackLimit()
    {
        return 0;
    }

	@Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer)
    {
        return false;
    }
	
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }
    
	@Override
    public void putStack(ItemStack par1ItemStack)
    {
        this.setConfigItem(this.getSlotIndex(), par1ItemStack);
        this.onSlotChanged();
        
    }
	
	public void removeItem(){
		iinv.setInventorySlotContents(this.getSlotIndex(), (ItemStack)null);
	}
	
	private void setConfigItem(int no,ItemStack itemstack){
    	if(itemstack!=null){
    		ItemStack itemstack1 = itemstack.copy();
    		itemstack1.stackSize = 0;
    		iinv.setInventorySlotContents(no,itemstack1);
    	}
	}

}
