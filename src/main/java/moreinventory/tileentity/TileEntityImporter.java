package moreinventory.tileentity;

import moreinventory.MoreInventoryMod;
import moreinventory.network.ImporterMessage;
import moreinventory.tileentity.storagebox.IStorageBoxNet;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.CSUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityImporter extends TileEntityTransportManager
{
	public boolean register = false;
	public boolean include = false;

	public void putInBox(IInventory inventory)
	{
		int[] pos = CSUtil.getSidePos(xCoord, yCoord, zCoord, topFace);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

		if (tile != null && tile instanceof IStorageBoxNet)
		{
			ItemStack itemstack;
			int size = inventory.getSizeInventory();

			if (currentSlot >= size)
			{
				currentSlot = 0;
			}

			for (int i = 0; i < size; i++)
			{
				int slot = currentSlot;

				if (++currentSlot == size)
				{
					currentSlot = 0;
				}

				itemstack = inventory.getStackInSlot(slot);

				if (itemstack != null && canExtract(itemstack))
				{
					if (CSUtil.canAccessFromSide(inventory, slot, getSneak(face)) && CSUtil.canExtractFromSide(inventory, itemstack, slot, getSneak(face)))
					{
						if (((TileEntityStorageBox)tile).getStorageBoxNetworkManager().linkedPutIn(itemstack, null, register))
						{
							CSUtil.checkNullStack(inventory, slot);

							return;
						}
					}
				}
			}
		}
	}

	protected boolean canExtract(ItemStack itemstack)
	{
		boolean result = !include;

		for (int i = 0; i < inv.length; i++)
		{
			ItemStack itemstack1 = inv[i];

			if (itemstack1 != null)
			{
				if (CSUtil.compareStacksWithDamage(itemstack, itemstack1))
				{
					result = include;
				}
			}
		}

		return result;
	}

	@Override
	public void doExtract()
	{
		int[] pos = CSUtil.getSidePos(xCoord, yCoord, zCoord, face);
		IInventory inventory = TileEntityHopper.func_145893_b(worldObj, pos[0], pos[1], pos[2]);

		if (inventory != null)
		{
			putInBox(inventory);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		include = nbt.getBoolean("include");
		register = nbt.getBoolean("register");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("include", include);
		nbt.setBoolean("register", register);
	}

	public void sendPacket()
	{
		MoreInventoryMod.network.sendToDimension(getPacket(false), worldObj.provider.dimensionId);
	}

	public void sendPacketToServer(boolean channel)
	{
		MoreInventoryMod.network.sendToServer(getPacket(channel));
	}

	@Override
	public Packet getDescriptionPacket()
	{
		super.getDescriptionPacket();
		sendPacket();

		return null;
	}

	public void handlePacketClient(boolean register, boolean include, boolean channel)
	{
		this.register = register;
		this.include = include;
	}

	public void handlePacketServer(boolean register, boolean include, boolean channel)
	{
		if (channel)
		{
			this.register = !this.register;
		}
		else
		{
			this.include = !this.include;
		}

		sendPacket();
	}

	protected ImporterMessage getPacket(boolean channel)
	{
		return new ImporterMessage(xCoord, yCoord, zCoord, register, include, channel);
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntityImporter";
	}
}
