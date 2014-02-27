package moreinventory.tileentity;

import moreinventory.MoreInventoryMod;
import moreinventory.network.packet.AbstractPacket;
import moreinventory.network.packet.PacketImporter;
import moreinventory.tileentity.storagebox.IStorageBoxNet;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSutil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityImporter extends TileEntityTransportManager{
	
	public boolean isRegister = false;
	public boolean Include = false;
	
    public void putInBox(IInventory iinv){
		int[] pos = CSutil.getSidePos(this.xCoord,this.yCoord,this.zCoord,topface);
		TileEntity tile = this.worldObj.getTileEntity(pos[0],pos[1],pos[2]);
		if(tile!=null&&tile instanceof IStorageBoxNet)
		{
			ItemStack itemstack;
			int k = iinv.getSizeInventory();
			if(nowSlot>=k){
				nowSlot = 0;
			}
			
			for(int i = 0; i < k;i++)
			{
				int slot = nowSlot;
				if(++nowSlot == k){
					nowSlot = 0;
				}
				
				itemstack = iinv.getStackInSlot(slot);
				if(itemstack != null&&canExtract(itemstack))
				{
					if(CSutil.canAccessFromSide(iinv, slot, getSneak(face))&&CSutil.canExtractFromSide(iinv, itemstack, slot, getSneak(face)))
					{
						
						if(((TileEntityStorageBox)tile).getStorageBoxNetworkManager().linkedPutIn(itemstack, null,isRegister))
						{
							CSutil.checkNullStack(iinv, slot);
							return;
						}

					}
				}
			}
		}
	}
    
    protected boolean canExtract(ItemStack itemstack){
    	int k = inv.length;
    	int dm = itemstack.getItemDamage();
    	boolean flg = !Include;
    	for(int i = 0;i<k;i++){
    		ItemStack itemstack1 = inv[i];
    		if(itemstack1!=null){
    			if(CSutil.compareStacksWithDamage(itemstack, itemstack1)){
    				flg = Include;
    			}
    		}
    	}
    	return flg;
    }
    
    public void doExtract(){
		int[] pos = CSutil.getSidePos(this.xCoord,this.yCoord,this.zCoord,face);
		IInventory iinv = TileEntityHopper.func_145893_b(this.worldObj, pos[0],pos[1],pos[2]);
		if(iinv != null){
			putInBox((IInventory)iinv);
		}
    }
    
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.Include = par1NBTTagCompound.getBoolean("Include");
        this.isRegister = par1NBTTagCompound.getBoolean("isRegister");
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Include",this.Include);
        par1NBTTagCompound.setBoolean("isRegister",this.isRegister);
    }

    public void sendPacket(){
        MoreInventoryMod.packetPipeline.sendPacketToAllPlayer(this.getPacket(false));
    }

	public void sendPacketToServer(boolean channel){
		MoreInventoryMod.packetPipeline.sendPacketToServer(this.getPacket(channel));
	}
    
	@Override
	public Packet getDescriptionPacket()
	{
        super.getDescriptionPacket();
        sendPacket();
        return null;
	}

    public void handlePacketClient(boolean isRegister, boolean Include, boolean channel){
        this.isRegister = isRegister;
        this.Include = Include;
    }

    public void handlePacketServer(boolean isRegister, boolean Include, boolean channel){
        if(channel){
            this.isRegister = !this.isRegister;
        }
        else
        {
            this.Include = !this.Include;
        }
        MoreInventoryMod.packetPipeline.sendPacketToAllPlayer(this.getPacket(false));

    }

	protected AbstractPacket getPacket(boolean channel)
	{
    	return new PacketImporter(xCoord, yCoord, zCoord,isRegister,Include, channel);
    }


    @Override
    public String getInventoryName() {
        return "TileEntityImporter";
    }
}
