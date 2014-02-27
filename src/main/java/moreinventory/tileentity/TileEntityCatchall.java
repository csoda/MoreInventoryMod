package moreinventory.tileentity;

import moreinventory.MoreInventoryMod;
import moreinventory.network.packet.AbstractPacket;
import moreinventory.network.packet.PacketCatchall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCatchall extends TileEntity implements IInventory {


    private ItemStack[] inv;
    private ItemStack[] displayedItem = new ItemStack[1];
    private String teName = "Catchall";

    public TileEntityCatchall(){
            inv = new ItemStack[36];
    }

    @Override
    public int getSizeInventory() {
            return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
            return inv[slot];
    }
    public ItemStack[] getDisplayedItem(){
    	return this.displayedItem;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
            inv[slot] = stack;
            if (stack != null && stack.stackSize > getInventoryStackLimit()) {
                    stack.stackSize = getInventoryStackLimit();
            }
            sendPacket();
            this.markDirty();

    }

    @Override
    public String getInventoryName() {
        return "TileEntityCatchall";
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
         this.markDirty();

        return stack;
    }

    public void transferToBlock(EntityPlayer player){
      InventoryPlayer playerinv = player.inventory;
      for (int i=0;i<36;i++){
            this.setInventorySlotContents(i,playerinv.getStackInSlot(i));
            playerinv.mainInventory[i] = null;
      }
      player.onUpdate();
      writeToNBT(new NBTTagCompound());
    }



    public void transferToPlayer(EntityPlayer player){
    	InventoryPlayer playerinv = player.inventory;
		ItemStack noitemstack = null;
    	for (int i=0;i<36;i++){
    		ItemStack itemstack = this.getStackInSlot(i);
    		if(itemstack!=null)playerinv.mainInventory[i] = itemstack;
    		this.setInventorySlotContents(i,noitemstack);
    	}
     	player.onUpdate();
    	writeToNBT(new NBTTagCompound());

    }

    public boolean transferTo(EntityPlayer player){
    	if(!this.isFilled()){
    		this.transferToBlock(player);
    		return true;
    	}
    	else
    	{
    		if(this.canTransferToPlayer(player)){
    			transferToPlayer(player);
    			return true;
    		}
    	}
    	return false;
    }

    public boolean isFilled(){
    		for(int i=0;i<inv.length;i++){
    			if(this.getStackInSlot(i)!=null){
    				return true;
    			}
    		}
    	return false;
    }

    public boolean canTransferToPlayer(EntityPlayer player){
    	for(int i=0;i<36;i++){
    		if(this.getStackInSlot(i)!=null&&player.inventory.mainInventory[i]!=null){
    			return false;
    		}
    	}
    	return true;
    }
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
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
    public boolean isUseableByPlayer(EntityPlayer player) {
            return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
            player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }


    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.inv = new ItemStack[this.getSizeInventory()];

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.teName = par1NBTTagCompound.getString("CustomName");
        }

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);

            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.inv.length)
            {
                this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inv.length; ++i)
        {
            if (this.inv[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.inv[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);
    }

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	} 
    public void handlePacketData(ItemStack[] dItems){
    	this.displayedItem = dItems;
    }
    
	@Override
	public Packet getDescriptionPacket()
	{
        sendPacket();
		return null;
	}

    private void sendPacket(){
        MoreInventoryMod.packetPipeline.sendPacketToAllPlayer(getPacket());
    }

    
    private AbstractPacket getPacket()
    {
        return new PacketCatchall(xCoord, yCoord, zCoord, inv);
    }

}
