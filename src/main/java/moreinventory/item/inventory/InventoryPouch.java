package moreinventory.item.inventory;

import moreinventory.core.MoreInventoryMod;
import moreinventory.network.PouchMessage;
import moreinventory.tileentity.storagebox.StorageBoxNetworkManager;
import moreinventory.tileentity.storagebox.TileEntityStorageBox;
import moreinventory.util.MIMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryPouch implements IInventory
{
	private ItemStack[] pouchItems = new ItemStack[54 + 18];
	private EntityPlayer usingPlayer;
	private ItemStack usingItem;

	public String customName;
	public boolean isCollectedByBox = true;
	public boolean isAutoCollect = true;
	public boolean isCollectMainInv = true;

	public InventoryPouch(EntityPlayer player, ItemStack itemstack)
	{
		this.usingPlayer = player;
		this.usingItem = itemstack;
		this.customName = itemstack.getDisplayName();
		this.readFromNBT();
	}

	public InventoryPouch(ItemStack itemstack)
	{
		this.usingItem = itemstack;
		this.readFromNBT();
	}

	public int getGrade()
	{
		int damage = usingItem.getItemDamage();

		return (damage - damage % 17) / 17;
	}

	public void setConfigItem(int slot, ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != MoreInventoryMod.Pouch)
		{
			pouchItems[slot] = itemstack.copy();
			pouchItems[slot].stackSize = 1;
		}
	}

	public boolean canAutoCollect(ItemStack itemstack)
	{
		return isAutoCollect && isCollectableItem(itemstack);
	}

	public boolean isCollectableItem(ItemStack itemstack)
	{
		for (int i = 0; i < 18; i++)
		{
			if (pouchItems[i] != null)
			{
				if (MIMUtils.compareStacksWithDamage(pouchItems[i], itemstack))
				{
					return true;
				}
			}
		}

		return false;
	}

	public void collectedByBox(TileEntityStorageBox tile)
	{
		for (int i = 18; i < pouchItems.length; i++)
		{
			tile.tryPutIn(getStackInSlot(i));

			MIMUtils.checkNullStack(this, i);
		}
	}

	public void linkedPutIn(StorageBoxNetworkManager sbnet)
	{
		for (int i = 18; i < pouchItems.length; i++)
		{
			sbnet.linkedPutIn(getStackInSlot(i), null, false);

			MIMUtils.checkNullStack(this, i);
		}
	}

	public void collectAllItemStack(IInventory inventory, boolean flag)
	{
		ItemStack itemstack;
		int origin = 0;

		if (!isCollectMainInv)
		{
			origin = 9;
		}

		for (int i = origin; i < inventory.getSizeInventory(); i++)
		{
			itemstack = inventory.getStackInSlot(i);

			if (itemstack != null)
			{
				if (itemstack.getItem() == MoreInventoryMod.Pouch)
				{
					InventoryPouch pouch = new InventoryPouch(itemstack);

					if (pouch.isAutoCollect && flag && itemstack != usingItem)
					{
						pouch.collectAllItemStack(inventory, false);
					}

					inventory.setInventorySlotContents(i, itemstack);
				}
				else
				{
					if (isCollectableItem(itemstack))
					{
						MIMUtils.mergeItemStack(itemstack, this);
					}
				}
			}
		}

		MIMUtils.checkNull(inventory);
	}

	public void transferToChest(IInventory tile)
	{
		if (tile.getSizeInventory() >= 27)
		{
			for (int i = 18; i < pouchItems.length; i++)
			{
				ItemStack itemstack = getStackInSlot(i);

				if (itemstack != null)
				{
					MIMUtils.mergeItemStack(itemstack, tile);
					MIMUtils.checkNullStack(this, i);
				}
			}
		}
	}

	public void onCrafting(ItemStack itemstack)
	{
		InventoryPouch pouch = new InventoryPouch(itemstack);
		pouch.pouchItems = pouchItems;
		pouch.isAutoCollect = isAutoCollect;
		pouch.isCollectedByBox = isCollectedByBox;
		pouch.isCollectMainInv = isCollectMainInv;
		pouch.customName = customName;
		pouch.writeToNBT(itemstack);
	}

	@Override
	public int getSizeInventory()
	{
		return pouchItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return pouchItems[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		pouchItems[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return "InvPouch";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack itemtack = getStackInSlot(slot);

		if (itemtack != null)
		{
			if (itemtack.stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				itemtack = itemtack.splitStack(amount);

				if (itemtack.stackSize == 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}

		markDirty();

		return itemtack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (slot < 18)
		{
			return null;
		}

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
	public void markDirty()
	{
		writeToNBT(usingItem);
	}

	public void readFromNBT()
	{
		if (usingItem != null)
		{
			NBTTagCompound nbt = usingItem.getTagCompound();
			pouchItems = new ItemStack[getSizeInventory()];

			if (nbt == null)
			{
				return;
			}

			NBTTagList list = nbt.getTagList("Items", 10);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				int slot = data.getByte("Slot") & 0xFF;

				if (slot >= 0 && slot < pouchItems.length)
				{
					pouchItems[slot] = ItemStack.loadItemStackFromNBT(data);
				}
			}

			isCollectedByBox = nbt.getBoolean("isCollectedByBox");
			isCollectMainInv = nbt.getBoolean("isCollectMainInv");
			isAutoCollect = nbt.getBoolean("isAutoCollect");
		}
	}

	public void writeToNBT(ItemStack itemstack)
	{
		if (usingItem != null)
		{
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < pouchItems.length; i++)
			{
				if (pouchItems[i] != null)
				{
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setByte("Slot", (byte)i);
					pouchItems[i].writeToNBT(nbt);
					list.appendTag(nbt);
				}
			}

			NBTTagCompound nbt = itemstack.getTagCompound();

			if (nbt == null)
			{
				nbt = new NBTTagCompound();
			}

			nbt.setTag("Items", list);
			nbt.setBoolean("isCollectedByBox", isCollectedByBox);
			nbt.setBoolean("isCollectMainInv", isCollectMainInv);
			nbt.setBoolean("isAutoCollect", isAutoCollect);
			itemstack.setTagCompound(nbt);
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		if (player == null)
		{
			return false;
		}

		usingItem = usingPlayer.getCurrentEquippedItem();
		readFromNBT();

		if (!MIMUtils.compareItems(player.getCurrentEquippedItem(), MoreInventoryMod.Pouch))
		{
			return false;
		}

		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return 18 <= slot;
	}

	public void handleClientPacket(boolean config1, boolean config2, boolean config3)
	{
		isCollectedByBox = config1;
		isCollectMainInv = config2;
		isAutoCollect = config3;

		writeToNBT(usingItem);
	}

	public void handleServerPacket(boolean config1, boolean config2, boolean config3)
	{
		if (config1)
		{
			isCollectedByBox = !isCollectedByBox;
		}
		else if (config2)
		{
			isCollectMainInv = !isCollectMainInv;
		}
		else if (config3)
		{
			isAutoCollect = !isAutoCollect;
		}

		writeToNBT(usingItem);
		sendPacket();
	}

	public void sendPacket()
	{
		MoreInventoryMod.network.sendTo(new PouchMessage(isCollectedByBox, isCollectMainInv, isAutoCollect), (EntityPlayerMP)usingPlayer);
	}

	public void sendPacketToServer(int channel)
	{
		MoreInventoryMod.network.sendToServer(new PouchMessage(channel == 0, channel == 1, channel == 2));
	}
}