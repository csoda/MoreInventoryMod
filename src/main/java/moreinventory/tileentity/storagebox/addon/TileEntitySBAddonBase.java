package moreinventory.tileentity.storagebox.addon;

import moreinventory.core.MoreInventoryMod;
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
	protected ItemStack[] storageItems;

	private StorageBoxNetworkManager storageBoxManager;
	private String ownerName = MoreInventoryMod.defaultOwner;
	private boolean isPrivate = false;

	@Override
	public StorageBoxNetworkManager getStorageBoxNetworkManager()
	{
		if (storageBoxManager == null)
		{
			storageBoxManager = new StorageBoxNetworkManager(worldObj, xCoord, yCoord, zCoord, ownerName);
		}

		return storageBoxManager;
	}

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager manager)
	{
		storageBoxManager = manager;
	}

	@Override
	public String getOwnerName()
	{
		return ownerName;
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

	public void setPrivate(boolean flag)
	{
		isPrivate = flag;
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
	public void onSBNetInvChanged(World world, int x, int y, int z, int id, int damage) {}

	@Override
	public void onTripleClicked(World world, int x, int y, int z, int id, int damage) {}

	@Override
	public int getSizeInventory()
	{
		return storageItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return storageItems[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		storageItems[slot] = itemstack;
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
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack itemstack = getStackInSlot(slot);

		if (itemstack != null)
		{
			if (itemstack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				itemstack = itemstack.splitStack(amount);

				if (itemstack.stackSize == 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}

		markDirty();

		return itemstack;
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
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) < 64;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return false;
	}

	public void onPlaced(EntityLivingBase entity)
	{
		if (entity instanceof EntityPlayer)
		{
			ownerName = ((EntityPlayer)entity).getDisplayName();
		}

		getStorageBoxNetworkManager().recreateNetwork();
	}

	public void onNeighborRemoved()
	{
		if (getStorageBoxNetworkManager().getBoxList().isOnBoxList(xCoord, yCoord, zCoord, worldObj.getWorldInfo().getVanillaDimension()))
		{
			getStorageBoxNetworkManager().recreateNetwork();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if (storageItems != null)
		{
			NBTTagList list = nbt.getTagList("Items", 10);

			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				storageItems[data.getShort("Slot")] = ItemStack.loadItemStackFromNBT(data);
			}
		}

		if (nbt.hasKey("isPrivate"))
		{
			isPrivate = nbt.getBoolean("isPrivate");
		}

		if (nbt.hasKey("owner"))
		{
			ownerName = nbt.getString("owner");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		if (storageItems != null)
		{
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < storageItems.length; ++i)
			{
				if (storageItems[i] != null)
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setShort("Slot", (short)i);
					storageItems[i].writeToNBT(data);
					list.appendTag(data);
				}
			}

			nbt.setTag("Items", list);
		}

		nbt.setBoolean("isPrivate", isPrivate);
		nbt.setString("owner", ownerName);
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

	public void handlePacket(boolean flag, String owner)
	{
		isPrivate = flag;
		ownerName = owner;
	}

	public void handleConfigPacketClient(byte channel, boolean flag)
	{
		if (channel == 0)
		{
			isPrivate = flag;
		}
	}

	public void handleConfigPacketServer(byte channel, boolean flag, String owner)
	{
		if (!isPrivate() || ownerName.equals(owner) || ownerName.equals(MoreInventoryMod.defaultOwner))
		{
			if (channel == 0)
			{
				isPrivate = !isPrivate;

				sendCommonGuiPacketToClient((byte) 0, this.isPrivate);
			}
		}
	}
}