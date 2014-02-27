package moreinventory.tileentity;


import moreinventory.MoreInventoryMod;
import moreinventory.network.packet.AbstractPacket;
import moreinventory.network.packet.PacketTPManager;
import moreinventory.util.CSutil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public  abstract class TileEntityTransportManager extends TileEntity implements IInventory{

	protected ItemStack[] inv;
	private byte updateTime;
	public String invName = "TransportManager";
	public byte face = 0;
	public byte topface = 1;
	public byte sneak = 6;
	public int nowSlot = 0;

	public TileEntityTransportManager(){
		inv = new ItemStack[9];
		this.updateTime = 20;
	}
	
	public void rotateBlock(){	
		boolean flg = false;
		for(int i = 0; i < 6; i++)
		{
			for(int t = 0; t < 5; t++)
			{
				rotateTop();
				if(haveIInventory(face)&&haveIInventory(topface)){
					flg = true;
					break;
				}
			}
			if(flg){
				break;
			}
		}
		
		if(!flg){
			face = 0;
			topface = 1;
		}
		sendCommonPacket();
    	this.markDirty();
	}
	
	private void rotateTop()
	{
		if(++topface > 5){
			topface = 0;
			if(++face > 5){
				face = 0;
			}
		}
		
		if(topface == face)
		{
			rotateTop();
		}
	}
	
	public boolean haveIInventory(int dir){
		int[] pos = CSutil.getSidePos(xCoord, yCoord, zCoord, dir);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
		if(tile instanceof IInventory)
		{
			return true;
		}
			return false;
	}
	
	@Override
	public void updateEntity()
	{
		if(!this.worldObj.isRemote&&!this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)){
			if(updateTime>0){
	           if(--updateTime==0){
	        	   updateTime = 20;
	        	   doExtract();
	           }
	        }
		}
	}
	
    public void setConfigItem(int no,ItemStack itemstack){
    	if(itemstack!=null){
    		this.inv[no] = itemstack.copy();
    		this.inv[no].stackSize = 1;
    	}
    }
    
    public int getSneak(int face){
    	return sneak != 6 ? sneak : face;
    	
    }
    
    /**abstract**/
    
	protected abstract void doExtract();
	
	/**Implements**/

	   @Override
	    public int getSizeInventory() {
	            return this.inv.length;
	    }

	    @Override
	    public ItemStack getStackInSlot(int slot) {
	            return inv[slot];
	    }

	    @Override
	    public void setInventorySlotContents(int slot, ItemStack stack) {
	            inv[slot] = stack;
	            if (stack != null && stack.stackSize > getInventoryStackLimit()) {
	                    stack.stackSize = getInventoryStackLimit();
	            }
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
	            return stack;
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

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	    {
	        return false;
	    }

	    @Override
	    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.readFromNBT(par1NBTTagCompound);
	        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items",10);
	        this.inv = new ItemStack[this.getSizeInventory()];
	        for (int i = 0; i < nbttaglist.tagCount(); ++i)
	        {
	            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
	            int j = nbttagcompound1.getByte("Slot") & 255;

	            if (j >= 0 && j < this.inv.length)
	            {
	                this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
	            }
	        }
	        this.face = par1NBTTagCompound.getByte("face");
	        this.topface = par1NBTTagCompound.getByte("topface");
	        this.sneak = par1NBTTagCompound.getByte("sneak");
	        this.nowSlot = par1NBTTagCompound.getInteger("nowSlot");
	        
	    }

	    /**
	     * Writes a tile entity to NBT.
	     */
	    @Override
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

	        par1NBTTagCompound.setByte("face", this.face);
	        par1NBTTagCompound.setByte("topface", this.topface);
	        par1NBTTagCompound.setByte("sneak", sneak);
	        par1NBTTagCompound.setInteger("nowSlot",nowSlot);
        }

        public void handleCommonPacketData(byte face, byte topface, byte sneak){
            this.face = face;
            this.topface = topface;
            this.sneak = sneak;
        }

		@Override
		public Packet getDescriptionPacket()
		{
            this.sendCommonPacket();
			return null;
		}
		
		public void sendCommonPacket()
		{
            MoreInventoryMod.packetPipeline.sendPacketToAllPlayer(this.getCommonPacket());
		}

		public void sendCommonPacketToServer(){
			MoreInventoryMod.packetPipeline.sendPacketToServer(this.getCommonPacket());
		}
		
		protected AbstractPacket getCommonPacket(){
	    	return new PacketTPManager(xCoord, yCoord, zCoord, face, topface, sneak);
		}
		
}
