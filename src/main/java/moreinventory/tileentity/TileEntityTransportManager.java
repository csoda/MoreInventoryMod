package moreinventory.tileentity;

import moreinventory.core.MoreInventoryMod;
import moreinventory.network.TransportManagerMessage;
import moreinventory.util.MIMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityTransportManager extends TileEntity implements IInventory
{
	protected ItemStack[] inventoryItems = new ItemStack[9];

	public byte face = 0;
	public byte topFace = 1;
	public byte sneak = 6;
	public int currentSlot = 0;

	private byte updateTime = 20;

	public void rotateBlock()
	{
		boolean flag = false;

		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				rotateTop();

				if (haveIInventory(face) && haveIInventory(topFace))
				{
					flag = true;
					break;
				}
			}

			if (flag)
			{
				break;
			}
		}

		if (!flag)
		{
			face = 0;
			topFace = 1;
		}

		sendCommonPacket();
		markDirty();
	}

	private void rotateTop()
	{
		if (++topFace > 5)
		{
			topFace = 0;

			if (++face > 5)
			{
				face = 0;
			}
		}

		if (topFace == face)
		{
			rotateTop();
		}
	}

	public boolean haveIInventory(int side)
	{
		int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, side);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

		return tile != null && tile instanceof IInventory;
	}

	@Override
	public void updateEntity()
	{
		if (!worldObj.isRemote && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
		{
			if (updateTime > 0)
			{
				if (--updateTime == 0)
				{
					updateTime = 20;

					doExtract();
				}
			}
		}
	}

	public void setConfigItem(int slot, ItemStack itemstack)
	{
		if (itemstack != null)
		{
			inventoryItems[slot] = itemstack.copy();
			inventoryItems[slot].stackSize = 1;
		}
	}

	public int getSneak(int face)
	{
		return sneak != 6 ? sneak : face;
	}

	protected abstract void doExtract();

	@Override
	public int getSizeInventory()
	{
		return inventoryItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventoryItems[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		inventoryItems[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
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

		return itemstack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack itemstack = getStackInSlot(slot);

		if (itemstack != null)
		{
			setInventorySlotContents(slot, null);
		}

		return itemstack;
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

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = (NBTTagList)nbt.getTag("items");
		inventoryItems = new ItemStack[getSizeInventory()];

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				int slot = data.getByte("Slot") & 255;

				if (slot >= 0 && slot < inventoryItems.length)
				{
					inventoryItems[slot] = ItemStack.loadItemStackFromNBT(data);
				}
			}
		}

		face = nbt.getByte("face");
		topFace = nbt.getByte("topFace");
		sneak = nbt.getByte("sneak");
		currentSlot = nbt.getInteger("currentSlot");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventoryItems.length; ++i)
		{
			if (inventoryItems[i] != null)
			{
				NBTTagCompound data = new NBTTagCompound();
				data.setByte("Slot", (byte)i);
				inventoryItems[i].writeToNBT(data);
				list.appendTag(data);
			}
		}

		nbt.setTag("items", list);
		nbt.setByte("face", face);
		nbt.setByte("topFace", topFace);
		nbt.setByte("sneak", sneak);
		nbt.setInteger("currentSlot", currentSlot);
	}

	public void handleCommonPacketData(byte config1, byte config2, byte config3)
	{
		face = config1;
		topFace = config2;
		sneak = config3;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		sendCommonPacket();

		return null;
	}

	public void sendCommonPacket()
	{
		MoreInventoryMod.network.sendToDimension(new TransportManagerMessage(xCoord, yCoord, zCoord, face, topFace, sneak), worldObj.provider.dimensionId);
	}

	public void sendCommonPacketToServer()
	{
		MoreInventoryMod.network.sendToServer(new TransportManagerMessage(xCoord, yCoord, zCoord, face, topFace, sneak));
	}
}