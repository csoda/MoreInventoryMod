package moreinventory.tileentity.storagebox.addon;

import moreinventory.MoreInventoryMod;
import moreinventory.network.SBAddonBaseConfigMessage;
import moreinventory.network.SBAddonBaseMessage;
import moreinventory.tileentity.storagebox.IStorageBoxAddon;
import moreinventory.tileentity.storagebox.StorageBoxNetworkManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class TileEntitySBAddonBase extends TileEntity implements IInventory, IStorageBoxAddon
{

	private StorageBoxNetworkManager StorageBoxManager;
	protected ItemStack[] inv;
	private boolean isPrivate = false;
	private String ownerName = MoreInventoryMod.defaultOwner;

	@Override
	public StorageBoxNetworkManager getStorageBoxNetworkManager()
	{
		if (StorageBoxManager == null)
		{
			StorageBoxManager = new StorageBoxNetworkManager(worldObj, xCoord, yCoord, zCoord, ownerName);
		}
		return StorageBoxManager;
	}

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager SBNetManager)
	{
		StorageBoxManager = SBNetManager;
	}

	@Override
	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String newName)
	{
		ownerName = newName;
	}

	public boolean isOwner(String name)
	{
		return ownerName.equals(name) || ownerName.equals(MoreInventoryMod.defaultOwner);
	}

	@Override
	public boolean isPrivate()
	{
		return isPrivate;
	}

	public void setPrivate(boolean flg)
	{
		isPrivate = flg;
	}

	public void togglePrivate(String name)
	{
		if (ownerName.equals(name) || ownerName.equals(MoreInventoryMod.defaultOwner))
		{
			isPrivate = !isPrivate;
			sendCommonPacket();
		}
	}

	@Override
	public void onSBNetInvChanged(World world, int x, int y, int z, int id, int damage)
	{
	}

	@Override
	public void onTripleClicked(World world, int x, int y, int z, int id, int damage)
	{
	}

	/*** IInventory ***/

	@Override
	public int getSizeInventory()
	{
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		inv[slot] = stack;
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntitySBAddonBase";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt)
	{
		ItemStack stack = getStackInSlot(slot);
		if (stack != null)
		{
			if (stack.stackSize <= amt)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}
		this.markDirty();
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return getStackInSlot(slot);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openInventory()
	{

	}

	@Override
	public void closeInventory()
	{

	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	{
		return false;
	}

	public void onPlaced(EntityLivingBase entity)
	{
		if (entity instanceof EntityPlayer)
		{
			this.ownerName = ((EntityPlayer) entity).getDisplayName();
		}
		getStorageBoxNetworkManager().reCreateNetwork();
	}

	public void onNeighborRemoved()
	{
		if (getStorageBoxNetworkManager().getBoxList().isOnBoxList(xCoord, yCoord, zCoord,
				worldObj.getWorldInfo().getVanillaDimension()))
		{
			this.getStorageBoxNetworkManager().reCreateNetwork();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		if (inv != null)
		{
			NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
			for (int i = 0; i < nbttaglist.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getShort("Slot");
				this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
		if (par1NBTTagCompound.hasKey("isPrivate"))
			this.isPrivate = par1NBTTagCompound.getBoolean("isPrivate");
		if (par1NBTTagCompound.hasKey("owner"))
			this.ownerName = par1NBTTagCompound.getString("owner");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		if (inv != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < this.inv.length; ++i)
			{
				if (this.inv[i] != null)
				{
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setShort("Slot", (short) i);
					this.inv[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			par1NBTTagCompound.setTag("Items", nbttaglist);
		}
		par1NBTTagCompound.setBoolean("isPrivate", this.isPrivate);
		par1NBTTagCompound.setString("owner", this.ownerName);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		sendCommonPacket();
		return null;
	}

	public void sendCommonPacket()
	{
		MoreInventoryMod.network.sendToDimension(new SBAddonBaseMessage(xCoord, yCoord, zCoord, isPrivate, ownerName), worldObj.provider.dimensionId);
	}

	public void sendCommonGuiPacketToClient(byte channel, boolean flag)
	{
		MoreInventoryMod.network.sendToDimension(new SBAddonBaseConfigMessage(xCoord, yCoord, zCoord, channel, flag), worldObj.provider.dimensionId);
	}

	public void sendCommonGuiPacketToServer(byte channel, boolean flag)
	{
		MoreInventoryMod.network.sendToServer(new SBAddonBaseConfigMessage(xCoord, yCoord, zCoord, channel, flag));
	}

	public void handlePacket(boolean isPrivate, String owner)
	{
		this.isPrivate = isPrivate;
		this.ownerName = owner;
	}

	public void handleConfigPacketClient(byte channel, boolean flg)
	{
		if (channel == 0)
		{
			this.isPrivate = flg;
		}
	}

	public void handleConfigPacketServer(byte channel, boolean flg, String owner)
	{
		if (!isPrivate() || ownerName.equals(owner) || ownerName.equals(MoreInventoryMod.defaultOwner))
		{
			if (channel == 0)
			{
				this.isPrivate = !this.isPrivate;
				sendCommonGuiPacketToClient((byte) 0, this.isPrivate);
			}
		}
	}
}