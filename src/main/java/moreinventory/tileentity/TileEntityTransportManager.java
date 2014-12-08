package moreinventory.tileentity;

import moreinventory.MoreInventoryMod;
import moreinventory.network.TransportManagerMessage;
import moreinventory.util.CSUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityTransportManager extends TileEntity implements IInventory
{
	protected ItemStack[] inv = new ItemStack[9];
	private byte updateTime = 20;
	public byte face = 0;
	public byte topFace = 1;
	public byte sneak = 6;
	public int currentSlot = 0;

	public void rotateBlock()
	{
		boolean flag = false;

		for (int i = 0; i < 6; i++)
		{
			for (int t = 0; t < 5; t++)
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

	public boolean haveIInventory(int dir)
	{
		int[] pos = CSUtil.getSidePos(xCoord, yCoord, zCoord, dir);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

		if (tile instanceof IInventory)
		{
			return true;
		}

		return false;
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

	public void setConfigItem(int no, ItemStack itemstack)
	{
		if (itemstack != null)
		{
			inv[no] = itemstack.copy();
			inv[no].stackSize = 1;
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
		return this.inv.length;
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

		if (stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt)
	{
		ItemStack itemstack = getStackInSlot(slot);

		if (itemstack != null)
		{
			if (itemstack.stackSize <= amt)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				itemstack = itemstack.splitStack(amt);

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
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
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
		NBTTagList list = nbt.getTagList("items", 10);
		inv = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound data = list.getCompoundTagAt(i);
			int j = data.getByte("Slot") & 255;

			if (j >= 0 && j < inv.length)
			{
				inv[j] = ItemStack.loadItemStackFromNBT(data);
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

		for (int i = 0; i < inv.length; ++i)
		{
			if (inv[i] != null)
			{
				NBTTagCompound data = new NBTTagCompound();
				data.setByte("Slot", (byte)i);
				inv[i].writeToNBT(data);
				list.appendTag(data);
			}
		}

		nbt.setTag("items", list);
		nbt.setByte("face", face);
		nbt.setByte("topFace", topFace);
		nbt.setByte("sneak", sneak);
		nbt.setInteger("currentSlot", currentSlot);
	}

	public void handleCommonPacketData(byte face, byte topFace, byte sneak)
	{
		this.face = face;
		this.topFace = topFace;
		this.sneak = sneak;
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