package moreinventory.container;


import moreinventory.MoreInventoryMod;
import moreinventory.gui.slot.SlotPouch;
import moreinventory.gui.slot.SlotPouch2;
import moreinventory.gui.slot.SlotPouchConfig;
import moreinventory.item.inventory.InvPouch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

//@ChestContainer
public class ContainerPouch extends Container{
	protected InvPouch tileEntity;

    public ContainerPouch (InventoryPlayer inventoryPlayer, InvPouch po){
            tileEntity = po;

            //configSlot
            int k = po.getGrade()+2;
            for(int i=0;i<k;i++){
            	for(int j=0;j<3;j++){
            		addSlotToContainer(new SlotPouchConfig(tileEntity, j + i * 3, 182 + j * 18, 24 + i * 18));
            	}
            }
            for(int i=k;i<6;i++){
            	for(int j=0;j<3;j++){
            		addSlotToContainer(new SlotPouchConfig(tileEntity, j + i * 3, -2000, -2000));
            	}
            }
            
            for (int i = 0; i < 6; i++) {
                    for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new SlotPouch2(tileEntity, j + i * 9 + 18, 8 + j * 18, 18 + i * 18));
                    }
            }

            //commonly used vanilla code that adds the player's inventory
            bindPlayerInventory(inventoryPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
            return tileEntity.isUseableByPlayer(player);
    }


    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
            for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                                            8 + j * 18, 140 + i * 18));
                    }
            }

            int Iusing = inventoryPlayer.currentItem;
            for (int i = 0; i < 9; i++) {
            	if(i!=Iusing){
                    addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 194+4));
            	}
            	else
            	{
            		addSlotToContainer(new SlotPouch(inventoryPlayer, i, 8 + i * 18, 194+4));
            	}
            }
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(itemstack1 !=null && itemstack1.getItem() == MoreInventoryMod.Pouch)return null;

            if (18 <= par2 && par2 < 72)
            {
                if (!this.mergeItemStack(itemstack1, 72, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 18, 72, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer)
    {
    	ItemStack retItemStack =null;
    	if(0<=par1&&par1<18)
    	{
    		SlotPouchConfig slot1 = (SlotPouchConfig)this.inventorySlots.get(par1);
    		if(par2==0)
    		{
    			slot1.putStack(par4EntityPlayer.inventory.getItemStack());
    		}
    		else
    		{
       			slot1.removeItem();
    		}
    	}
    	else
    	{
    	   	retItemStack = super.slotClick(par1,par2,par3,par4EntityPlayer);
    	   	
    	}
    	return retItemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);
    	tileEntity.closeInventory();
    }
    
    /*
    @RowSizeCallback
    public int getRowSize(){
		return 54;
    	
    }
    
    //InvTweaks API.
    
    @ContainerSectionCallback
    public Map<ContainerSection, List<Slot>> getSlot(){
    	Map<ContainerSection, List<Slot>> retMap = new HashMap();
    	List<Slot> slotList = new ArrayList();
    	for(int i = 0; i  < 54; i++){
    		slotList.add((Slot) this.inventorySlots.get(i+18));
    	}
    	retMap.put(ContainerSection.CHEST, slotList);
    	
    	return retMap;
    }
    */

}
