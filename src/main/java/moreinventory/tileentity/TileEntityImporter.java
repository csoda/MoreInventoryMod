package moreinventory.tileentity;

import moreinventory.core.MoreInventoryMod;
import moreinventory.network.ImporterMessage;
import moreinventory.tileentity.storagebox.IStorageBoxNet;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
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
		int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, topFace);
		TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

		if (tile != null && tile instanceof IStorageBoxNet)
		{
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

				ItemStack itemstack = inventory.getStackInSlot(slot);

				if (itemstack != null && canExtract(itemstack))
				{
					if (MIMUtils.canAccessFromSide(inventory, slot, getSneak(face)) && MIMUtils.canExtractFromSide(inventory, itemstack, slot, getSneak(face)))
					{
						if (((TileEntityStorageBox)tile).getStorageBoxNetworkManager().linkedPutIn(itemstack, null, register))
						{
							MIMUtils.checkNullStack(inventory, slot);

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

		for (int i = 0; i < inventoryItems.length; i++)
		{
			ItemStack itemstack1 = inventoryItems[i];

			if (itemstack1 != null)
			{
				if (MIMUtils.compareStacksWithDamage(itemstack, itemstack1))
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
		int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, face);
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
		MoreInventoryMod.network.sendToDimension(new ImporterMessage(xCoord, yCoord, zCoord, register, include, false), worldObj.provider.dimensionId);
	}

	public void sendPacketToServer(boolean channel)
	{
		MoreInventoryMod.network.sendToServer(new ImporterMessage(xCoord, yCoord, zCoord, register, include, channel));
	}

	@Override
	public Packet getDescriptionPacket()
	{
		super.getDescriptionPacket();
		sendPacket();

		return null;
	}

	public void handlePacketClient(boolean config1, boolean config2, boolean config3)
	{
		register = config1;
		include = config2;
	}

	public void handlePacketServer(boolean config1, boolean config2, boolean config3)
	{
		if (config3)
		{
			register = !register;
		}
		else
		{
			include = !include;
		}

		sendPacket();
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntityImporter";
	}
}