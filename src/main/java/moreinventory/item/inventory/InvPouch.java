package moreinventory.item.inventory;


import moreinventory.MoreInventoryMod;
import moreinventory.network.packet.AbstractPacket;
import moreinventory.network.packet.PacketPouch;
import moreinventory.tileentity.storagebox.StorageBoxNetworkManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSutil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InvPouch implements IInventory{

    private ItemStack[] inv;
    private final int invlength = 54;
    private final int invConfiglength = 18;
    private EntityPlayer usingPlayer;
    private ItemStack usingItem;
    public String  customName;
    public boolean isCollectedByBox=true;
    public boolean isAutoCollect=true;
    public boolean isCollectMainInv=true;

    public InvPouch(EntityPlayer player,ItemStack itemstack){
        inv = new ItemStack[invlength+invConfiglength];
        usingPlayer = player;
        usingItem = itemstack;
        this.customName = itemstack.getDisplayName();
        readFromNBT();
    }
    
    public InvPouch(ItemStack itemstack){
        inv = new ItemStack[invlength+invConfiglength];
        usingItem = itemstack;
        readFromNBT();
    }
    
    public int getGrade(){
    	int dm = usingItem.getItemDamage();
    	return (dm-(dm%17))/17;
    }
    
    public void setConfigItem(int no,ItemStack itemstack){
    	if(itemstack!=null&&itemstack.getItem() != MoreInventoryMod.Pouch){
    		this.inv[no] = itemstack.copy();
    		this.inv[no].stackSize = 1;
    	}
    }

    public boolean canAutoCollect(ItemStack itemstack){
    	return this.isAutoCollect && this.isCollectableItem(itemstack);
    }

    public boolean isCollectableItem(ItemStack itemstack){
        for(int i=0;i<18;i++)
        {
            if(inv[i]!=null)
            {
                if(CSutil.compareStacksWithDamage(inv[i], itemstack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void collectedByBox(TileEntityStorageBox tile){
        for(int i = 18; i < this.invlength + 18; i++){
            tile.tryPutIn(getStackInSlot(i));
            CSutil.checkNullStack(this,i);
        }
    }

    public void linkedPutIn(StorageBoxNetworkManager sbnet){
        for(int i = 18; i < this.invlength + 18; i++){
            sbnet.linkedPutIn(getStackInSlot(i), null, false);
            CSutil.checkNullStack(this,i);
        }
    }
    
	public void collectAllItemStack(IInventory perInv,boolean flg){
		ItemStack itemstack;
		int k = perInv.getSizeInventory();
		int j = 0;
		
		if(!this.isCollectMainInv){
			j = 9;
		}
		
		for (int i = j;i<k;i++)
		{
			itemstack = perInv.getStackInSlot(i);
			if(itemstack!=null)
			{
				if(itemstack.getItem() == MoreInventoryMod.Pouch)
				{
					InvPouch pouch = new InvPouch(itemstack);
					if(pouch.isAutoCollect&&flg&&itemstack!=usingItem)
					{
						pouch.collectAllItemStack((IInventory)perInv,false);
					}
					perInv.setInventorySlotContents(i,itemstack);
				}
				else
				{
					if(this.isCollectableItem(itemstack))
					{
						CSutil.mergeItemStack(itemstack,this);
					}
				}
			}
		}
		CSutil.checkNull(perInv);
	}
    
    public void transferToChest(IInventory tile){
    	if(tile.getSizeInventory()>=27)
    	{
    		int m = this.invlength + 18;
    		for(int i=18;i<m;i++)
    		{
                ItemStack itemstack = this.getStackInSlot(i);
    			if(itemstack!=null)
    			{
                    CSutil.mergeItemStack(itemstack, tile);
                    CSutil.checkNullStack(this,i);
                }
    		}
    	}
    }
    
    public void onCrafting(ItemStack itemstack){
    	InvPouch po = new InvPouch(itemstack);
    	po.inv = this.inv;
    	po.isAutoCollect = this.isAutoCollect;
    	po.isCollectedByBox = this.isCollectedByBox;
    	po.isCollectMainInv = this.isCollectMainInv;
    	po.customName = this.customName;
    	po.writeToNBT(itemstack);
    }
    

    @Override
    public int getSizeInventory() {
            return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv[slot];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
            this.inv[slot] = stack;
            if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                    stack.stackSize = getInventoryStackLimit();
            }
            markDirty();
    }

    @Override
    public String getInventoryName() {
        return "InvPouch";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null) {
                    if (stack.stackSize <= amt) {
                            setInventorySlotContents(slot, null);
                    } else {
                            stack = stack.splitStack(amt);
                            if (stack.stackSize == 0) {
                                    setInventorySlotContents(slot, null);
                            }
                    }
            }
            markDirty();
            return stack;
    }
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
    	if(slot < 18)return null;
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
                setInventorySlotContents(slot, null);
        }
        return stack;
    }

    @Override
    public int getInventoryStackLimit() {
            return 64;
    }

    @Override
    public void markDirty() {
        writeToNBT(usingItem);
    }

    public void readFromNBT()
    {
        if(usingItem != null){
            NBTTagCompound nbttagcompound = usingItem.getTagCompound();
            inv = new ItemStack[getSizeInventory()];

            if (nbttagcompound == null) {
                return;
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("Items",10);
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if (j >= 0 && j < inv.length) {
                    inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
                }
            }
            this.isCollectedByBox = nbttagcompound.getBoolean("config1");
            this.isCollectMainInv   = nbttagcompound.getBoolean("config2");
            this.isAutoCollect		 = nbttagcompound.getBoolean("config3");
        }
    }


    public void writeToNBT(ItemStack saveItemStack)
    {
        if(usingItem != null){
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < inv.length; i++) {
                if (inv[i] != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    inv[i].writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }

            NBTTagCompound nbttagcompound = saveItemStack.getTagCompound();
            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
            }
            nbttagcompound.setTag("Items", nbttaglist);
            nbttagcompound.setBoolean("config1", this.isCollectedByBox);
            nbttagcompound.setBoolean("config2", this.isCollectMainInv);
            nbttagcompound.setBoolean("config3", this.isAutoCollect);
            saveItemStack.setTagCompound(nbttagcompound);
        }
    }

    //Check isUseable and Current equipped Item Update.
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
		ItemStack eqItem = usingPlayer.getCurrentEquippedItem();
        usingItem = eqItem;
        readFromNBT();

    	if(!CSutil.compareItems(entityplayer.getCurrentEquippedItem() , MoreInventoryMod.Pouch)){
    		return false;
    	}

    	return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return 18<=i;
	}

    public void handleClientPacket(boolean b1, boolean b2, boolean b3){
        this.isCollectedByBox = b1;
        this.isCollectMainInv = b2;
        this.isAutoCollect = b3;
        this.writeToNBT(usingItem);
    }

    public void handleServerPacket(boolean b1, boolean b2, boolean b3)
    {
        if(b1){
            this.isCollectedByBox = !this.isCollectedByBox;
        }
        else if(b2)
        {
            this.isCollectMainInv = !this.isCollectMainInv;
        }
        else if(b3){
            this.isAutoCollect = !this.isAutoCollect;
        }
        this.writeToNBT(usingItem);
        sendPacketToClient();
    }


	public void sendPacketToServer(int channel){
		MoreInventoryMod.packetPipeline.sendPacketToServer(this.getClientPacket(channel));
	}

    public void sendPacketToClient(){
        MoreInventoryMod.packetPipeline.sendPacketToPlayer(this.getServerPacket(), (EntityPlayerMP) this.usingPlayer);

    }

    private  AbstractPacket getServerPacket(){
        return new PacketPouch(this.isCollectedByBox, this.isCollectMainInv, this.isAutoCollect);
    }

    private  AbstractPacket getClientPacket(int channel){
        boolean b1 = channel == 0;
        boolean b2 = channel == 1;
        boolean b3 = channel == 2;
        return new PacketPouch(b1, b2, b3);
    }

}
