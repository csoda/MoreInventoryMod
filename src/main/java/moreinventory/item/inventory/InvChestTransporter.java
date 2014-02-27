package moreinventory.item.inventory;


import moreinventory.MoreInventoryMod;
import moreinventory.util.CSutil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class InvChestTransporter implements IInventory{


	private ItemStack chestTP;
    private ItemStack[] inv;
    private ItemStack tItemBlock;

    public InvChestTransporter(ItemStack par1ItemStack){
            chestTP = par1ItemStack;
        	readFromNBT();
    }


    public boolean placeBlock(World world,EntityPlayer player, int x,int y,int z, int side,float k ,float l, float m)
    {
        if(tItemBlock == null)return false;

    	boolean flg = false;

        Block block = Block.getBlockFromItem(tItemBlock.getItem());

    	if(block != null)
        {
            if(block.isReplaceable(world, x, y, z))
            {
                flg = true;
            }

            if(tItemBlock.getItem().onItemUse(tItemBlock, player, world, x, y, z, side, k, l, m))
            {
                TileEntity tileEntity;
                if(!flg){
                    int[] pos = CSutil.getSidePos(x, y, z, side);
                    tileEntity = world.getTileEntity(pos[0], pos[1], pos[2]);
                }
                else
                {
                    tileEntity = world.getTileEntity(x, y, z);
                }
                this.transferToBlock(tileEntity);
                block.onBlockPlacedBy(world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, player, tItemBlock);

                return true;
            }
        }
    	
    	return false;
    }

    
    public boolean transferToPlayer(TileEntity tile){
    	
    	if(checkMatryoshka((IInventory)tile))
    	{
        	NBTTagCompound nbt =  chestTP.getTagCompound();
            if (nbt == null) {
            	nbt = new NBTTagCompound();
            }
            Block block = tile.getWorldObj().getBlock(tile.xCoord,tile.yCoord,tile.zCoord);
            int meta = tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord);
            tItemBlock = new ItemStack(block,1,meta);
            
        	tile.writeToNBT(nbt);
        	
        	IInventory iinv = (IInventory)tile;
        	for(int i = 0; i < iinv.getSizeInventory(); i++)
        	{
        		iinv.setInventorySlotContents(i, null);
        	}
        	
        	this.writeToNBT(nbt);
        	
        	return true;
    	}
    	
    	return false;
    }
    
    public boolean transferToBlock(TileEntity tile)
    {
    	NBTTagCompound nbt =  chestTP.getTagCompound();
        if (nbt == null) {
        	nbt = new NBTTagCompound();
        }
        nbt.setInteger("x", tile.xCoord);
        nbt.setInteger("y", tile.yCoord);
        nbt.setInteger("z", tile.zCoord);
    	tile.readFromNBT(nbt);

    	return false;
    }
    
    
    
    private boolean checkMatryoshka(IInventory iinv){
    	ItemStack itemstack;
    	for(int i = 0;i<iinv.getSizeInventory();i++){
    		itemstack = iinv.getStackInSlot(i);
    		if(itemstack != null){
    			if(itemstack.getItem() == MoreInventoryMod.ChestTransporter&&itemstack.getItemDamage()!=0){
    				return false;
    			}
	    		if(itemstack.getItem() == MoreInventoryMod.Pouch){
					InvPouch pouch = new InvPouch(itemstack);
	    			if(!checkMatryoshka(pouch)){
	    				return false;
	    			}
	    		}
    		}
    	}
    	return true;
    }
    
    public String getContentsItemName(ItemStack itemstack){
    	String name = "Empty";
    	NBTTagCompound nbt = itemstack.stackTagCompound;
    	if(nbt!=null)
    	{
			ItemStack item = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Contents"));
			if(item != null)
			{
				name = item.getDisplayName();
			}
    	}
    	return name;
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
    }

    @Override
    public String getInventoryName() {
        return "transporter";
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

    public void readFromNBT()
    {
        NBTTagCompound nbttagcompound = chestTP.getTagCompound();
        if (nbttagcompound == null) {
            return;
        }
        tItemBlock = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("tItemBlock"));
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagCompound tag = new NBTTagCompound();
        if(this.tItemBlock != null){
            tItemBlock.writeToNBT(tag);
        }
        nbt.setTag("tItemBlock", tag);
        chestTP.setTagCompound(nbt);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        ItemStack itemstack = entityplayer.getCurrentEquippedItem();
        if (itemstack != chestTP) {
            return false;
        }
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public void markDirty() {}

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }



}
