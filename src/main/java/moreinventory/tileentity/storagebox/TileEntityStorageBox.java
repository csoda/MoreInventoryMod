package moreinventory.tileentity.storagebox;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.inventory.InventoryPouch;
import moreinventory.network.StorageBoxButtonMessage;
import moreinventory.network.StorageBoxConfigMessage;
import moreinventory.network.StorageBoxContentsMessage;
import moreinventory.network.StorageBoxMessage;
import moreinventory.util.MIMUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class TileEntityStorageBox extends TileEntity implements IInventory, IStorageBoxNet
{
	protected ItemStack[] storageItems;

	private String ownerID = MoreInventoryMod.defaultOwnerID;
	private StorageBoxType type;
	private StorageBoxNetworkManager storageBoxManager;

	private ItemStack contents;

	public int contentsCount;
	public byte face;
	public boolean isPrivate = false;
	public boolean canInsert = true;
	public boolean checkNBT = true;

	protected byte clickTime = 0;
	protected byte clickCount = 0;

	@SideOnly(Side.CLIENT)
	public byte displayedStackSize;
	@SideOnly(Side.CLIENT)
	public int displayedStackCount;
	@SideOnly(Side.CLIENT)
	public int connectCount;

	public TileEntityStorageBox()
	{
		this(StorageBoxType.Wood);
	}

	public TileEntityStorageBox(StorageBoxType type)
	{
		this.type = type;
		this.storageItems = new ItemStack[type.inventorySize];
		this.face = 0;
	}

	public StorageBoxType getStorageBoxType()
	{
		return type;
	}

	@Override
	public StorageBoxNetworkManager getStorageBoxNetworkManager()
	{
		if (storageBoxManager == null)
		{
			makeNewBoxList();
		}

		return storageBoxManager;
	}

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager manager)
	{
		storageBoxManager = manager;
	}

	@Override
	public String getOwner()
	{
		return ownerID;
	}

	public String getOwnerName()
	{
		return MoreInventoryMod.playerNameCache.getName(getOwner());
	}

	@Override
	public boolean isPrivate()
	{
		return isPrivate;
	}

	public void rotateBlock()
	{
		face = (byte)(face == 2 ? 5 : face == 5 ? 3 : face == 3 ? 4 : 2);

		markDirty();
	}

	public int getFirstItemIndex()
	{
		int index = 0;

		for (int i = 0; i < storageItems.length; i++)
		{
			if (storageItems[i] != null)
			{
				index = i;
				break;
			}
		}

		return index;
	}

	public ItemStack getContents()
	{
		return contents;
	}

	public Item getContentsItem()
	{
		ItemStack itemstack = getContents();

		if (itemstack != null)
		{
			return itemstack.getItem();
		}

		return null;
	}

	public void setContents(ItemStack itemstack)
	{
		if (itemstack != null)
		{
			contents = itemstack;
		}
	}

	public int getContentsDamage()
	{
		if (contents != null)
		{
			return contents.getItemDamage();
		}

		return 0;
	}

	@Override
	public int getSizeInventory()
	{
		return type.inventorySize + 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 0 && slot < storageItems.length ? storageItems[slot] : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		if (itemstack != null)
		{
			if (getContents() == null)
			{
				registerItems(itemstack);
			}

			if (!isSameAsContents(itemstack) || slot == type.inventorySize)
			{
				getStorageBoxNetworkManager().linkedPutIn(itemstack, this, false);

				MIMUtils.dropItem(worldObj, itemstack, xCoord, yCoord, zCoord);
			}
			else
			{
				storageItems[slot] = itemstack;
			}

			if (itemstack.stackSize > getInventoryStackLimit())
			{
				itemstack.stackSize = getInventoryStackLimit();
			}
		}
		else if (slot < type.inventorySize)
		{
			storageItems[slot] = null;
		}

		markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return "TileEntityStorageBox";
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
	public void markDirty()
	{
		if (!worldObj.isRemote)
		{
			getStorageBoxNetworkManager().updateOnInvChanged(worldObj, xCoord, yCoord, zCoord, getContents());
			sendPacket();
		}

		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return itemstack != null && (slot != storageItems.length && (getContents() == null || isSameAsContents(itemstack)) ||
			slot == storageItems.length && canInsert && getStorageBoxNetworkManager().canLinkedImport(itemstack, this));
	}

	public boolean registerItems(ItemStack itemstack)
	{
		if (itemstack != null && getContents() == null)
		{
			contents = itemstack;
			getStorageBoxNetworkManager().getBoxList().registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getContents());
			sendContents();

			return true;
		}

		return false;
	}

	private void clearRegister()
	{
		if (contentsCount == 0)
		{
			contents = null;
			sendContents();
		}
	}

	public boolean isSameAsContents(ItemStack itemstack)
	{
		boolean result = itemstack != null && (getContents() == null || MIMUtils.compareStacksWithDamage(itemstack, getContents()));

		if (result && itemstack.hasTagCompound() && checkNBT)
		{
			for (int i = 0; i < getSizeInventory(); i++)
			{
				ItemStack item = getStackInSlot(i);

				if (item != null && !ItemStack.areItemStackTagsEqual(itemstack, item))
				{
					return false;
				}
			}
		}

		return result;
	}

	public int getContentItemCount()
	{
		contentsCount = 0;

		for (int i = 0; i < storageItems.length; i++)
		{
			if (getStackInSlot(i) != null)
			{
				contentsCount += getStackInSlot(i).stackSize;
			}
		}

		return contentsCount;
	}

	public boolean tryPutIn(ItemStack itemstack)
	{
		if (isSameAsContents(itemstack))
		{
			MIMUtils.mergeItemStack(itemstack, this);

			return true;
		}

		return false;
	}

	public void collectAllItemStack(IInventory inventory)
	{
		if (getContents() != null)
		{
			for (int i = 0; i < inventory.getSizeInventory(); ++i)
			{
				ItemStack itemstack = inventory.getStackInSlot(i);

				if (itemstack != null)
				{
					if (itemstack.getItem() == MoreInventoryMod.pouch)
					{
						InventoryPouch pouch = new InventoryPouch(itemstack);

						if (pouch.isCollectedByBox)
						{
							pouch.collectedByBox(this);
						}
					}
					else
					{
						tryPutIn(itemstack);
					}
				}
			}
		}
	}

	public boolean canMergeItemStack(ItemStack itemstack)
	{
		int size = type.inventorySize;
		ItemStack item;

		for (int i = 0; i < size; i++)
		{
			item = getStackInSlot(i);

			if (item == null)
			{
				return true;
			}
		}

		for (int i = 0; i < size; i++)
		{
			item = getStackInSlot(i);

			if (item.stackSize != item.getMaxStackSize())
			{
				return item.getMaxStackSize() - item.stackSize >= itemstack.stackSize;
			}
		}

		return false;
	}

	public ItemStack loadItemStack(int max)
	{
		int maxCount = 0;
		ItemStack result = null;

		for (int i = 0; i < storageItems.length; i++)
		{
			ItemStack itemstack = storageItems[i];

			if (itemstack != null)
			{
				if (result == null)
				{
					result = itemstack.copy();
					result.stackSize = 0;
					maxCount = max == 0 ? itemstack.getMaxStackSize() : max;

					if (maxCount > itemstack.getMaxStackSize())
					{
						maxCount = itemstack.getMaxStackSize();
					}
				}

				if (ItemStack.areItemStackTagsEqual(result, itemstack))
				{
					int j = maxCount - result.stackSize;

					if (itemstack.stackSize < j)
					{
						j = itemstack.stackSize;
					}

					decrStackSize(i, j);

					result.stackSize += j;

					if (result.stackSize == maxCount)
					{
						break;
					}
				}
			}
		}

		return result;
	}

	public boolean rightClickEvent(World world, EntityPlayer player, int x, int y, int z)
	{
		boolean prevInsert = canInsert;

		switch (++clickCount)
		{
			case 1:
				clickTime = 16;
				ItemStack itemstack = player.getCurrentEquippedItem();

				if (itemstack != null)
				{
					registerItems(itemstack);

					canInsert = false;
					tryPutIn(itemstack);
					canInsert = prevInsert;
				}
				else if (player.isSneaking())
				{
					clearRegister();
				}

				break;
			case 2:
				canInsert = false;
				collectAllItemStack(player.inventory);
				canInsert = prevInsert;
				updatePlayerInventory(player);
				break;
			case 3:
				clickCount = 0;

				getStorageBoxNetworkManager().linkedCollect(player.inventory);
				storageBoxManager.updateOnTripleClicked(worldObj, xCoord, yCoord, zCoord, getContents());
				updatePlayerInventory(player);
				break;
			default:
				clickCount = 0;
				break;
		}

		return true;
	}

	public void leftClickEvent(EntityPlayer player)
	{
		if (getContents() != null)
		{
			if (player.isSneaking())
			{
				player.inventory.addItemStackToInventory(loadItemStack(1));
			}
			else if (player.inventory.getFirstEmptyStack() != -1)
			{
				player.inventory.addItemStackToInventory(loadItemStack(0));
			}
		}
	}

	public void updatePlayerInventory(EntityPlayer player)
	{
		ItemStack[] items = player.inventory.mainInventory;

		for (int i = 0; i < items.length; i++)
		{
			ItemStack itemstack = items[i];

			if (itemstack != null && itemstack.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(i, null);
			}
		}

		player.onUpdate();
	}

	@Override
	public void updateEntity()
	{
		if (clickTime > 0 && --clickTime <= 0)
		{
			clickCount = 0;
		}
	}

	public boolean isFull()
	{
		return getPowerOutput(this) == 15;
	}

	public static int getPowerOutput(TileEntityStorageBox tile)
	{
		if (tile.type != StorageBoxType.Glass && tile.type != StorageBoxType.Ender && tile.getContents() != null)
		{
			return 15 * tile.contentsCount / tile.type.inventorySize / tile.getContents().getMaxStackSize();
		}

		return 0;
	}

	public void makeNewBoxList()
	{
		storageBoxManager = new StorageBoxNetworkManager(worldObj, xCoord, yCoord, zCoord, ownerID);
	}

	public void onPlaced(EntityLivingBase entity)
	{
		if (entity instanceof EntityPlayer)
		{
			ownerID = entity.getUniqueID().toString();
		}

		markDirty();

		for (int i = 0; i < 6; ++i)
		{
			int[] pos = MIMUtils.getSidePos(xCoord, yCoord, zCoord, Facing.oppositeSide[i]);
			TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);

			if (tile != null && tile instanceof IStorageBoxNet)
			{
				StorageBoxNetworkManager manager = ((IStorageBoxNet)tile).getStorageBoxNetworkManager();
				manager.addNetwork(worldObj, xCoord, yCoord, zCoord);
				manager.getBoxList().registerItem(xCoord, yCoord, zCoord, worldObj.provider.dimensionId, getContents());

				return;
			}
		}

		if (storageBoxManager == null)
		{
			makeNewBoxList();
		}
	}

	public void onNeighborRemoved()
	{
		if (getStorageBoxNetworkManager().getBoxList().isOnBoxList(xCoord, yCoord, zCoord, worldObj.getWorldInfo().getVanillaDimension()))
		{
			getStorageBoxNetworkManager().recreateNetwork();
		}
	}

	public TileEntityStorageBox upgrade(int type)
	{
		TileEntityStorageBox tile = StorageBoxType.makeEntity(type);
		System.arraycopy(storageItems, 0, tile.storageItems, 0, storageItems.length);
		tile.face = face;
		tile.contents = contents;
		tile.contentsCount = contentsCount;
		tile.isPrivate = isPrivate;
		tile.ownerID = ownerID;

		return tile;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList list = (NBTTagList)nbt.getTag("Items");

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				storageItems[data.getShort("Slot2")] = ItemStack.loadItemStackFromNBT(data);
			}
		}

		contents = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Contents"));
		contentsCount = nbt.getInteger("ContentsItemCount");
		face = nbt.getByte("face");
		canInsert = nbt.getBoolean("canInsert");
		checkNBT = nbt.getBoolean("checkNBT");
		isPrivate = nbt.getBoolean("isPrivate");
		ownerID = nbt.getString("owner");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < storageItems.length; ++i)
		{
			if (storageItems[i] != null)
			{
				NBTTagCompound data = new NBTTagCompound();
				data.setShort("Slot2", (short)i);
				storageItems[i].writeToNBT(data);
				list.appendTag(data);
			}
		}

		nbt.setTag("Items", list);

		NBTTagCompound data = new NBTTagCompound();

		if (contents != null)
		{
			contents.writeToNBT(data);
		}

		nbt.setTag("Contents", data);
		nbt.setInteger("ContentsItemCount", contentsCount);
		nbt.setInteger("face", face);
		nbt.setBoolean("isPrivate", isPrivate);
		nbt.setBoolean("canInsert", canInsert);
		nbt.setBoolean("checkNBT", checkNBT);
		nbt.setString("owner", ownerID);
	}

	public TileEntityStorageBox updateFromMetadata(int meta)
	{
		if (worldObj != null && worldObj.isRemote)
		{
			if (meta != type.ordinal())
			{
				worldObj.setTileEntity(xCoord, yCoord, zCoord, StorageBoxType.makeEntity(meta));

				return (TileEntityStorageBox)worldObj.getTileEntity(xCoord, yCoord, zCoord);
			}
		}

		return this;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		sendContents();
		sendPacket();

		return null;
	}

	@SideOnly(Side.CLIENT)
	public void handlePacket(int config1, byte config2)
	{
		contentsCount = config1;
		face = config2;

		if (getContents() != null)
		{
			int size = getContents().getMaxStackSize();
			displayedStackSize = (byte)(contentsCount % size);
			displayedStackCount = (contentsCount - displayedStackSize) / size;
		}
	}

	@SideOnly(Side.CLIENT)
	public void handlePacketContents(ItemStack itemstack)
	{
		contents = itemstack;
	}

	@SideOnly(Side.CLIENT)
	public void handlePacketConfig(boolean config1, boolean config2, boolean config3, int config4, String owner)
	{
		isPrivate = config1;
		checkNBT = config2;
		canInsert = config3;
		connectCount = config4;
		ownerID = owner;
	}

	public void handlePacketButton(byte channel, String owner)
	{
		if (!isPrivate() || ownerID.equals(owner) || ownerID.equals(MoreInventoryMod.defaultOwnerID))
		{
			switch (channel)
			{
				case 0:
					isPrivate = !isPrivate;
					break;
				case 1:
					checkNBT = !checkNBT;
					break;
				case 2:
					canInsert = !canInsert;
					break;
			}

			sendGUIPacketToClient();
		}
	}

	public void sendPacket()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxMessage(xCoord, yCoord, zCoord, getContentItemCount(), face), worldObj.provider.dimensionId);
	}

	public void sendContents()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxContentsMessage(xCoord, yCoord, zCoord, contents), worldObj.provider.dimensionId);
	}

	public void sendGUIPacketToClient()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxConfigMessage(xCoord, yCoord, zCoord, isPrivate, checkNBT, canInsert, getStorageBoxNetworkManager().getKnownList().getListSize(), ownerID), worldObj.provider.dimensionId);
	}

	public void sendGUIPacketToServer(byte channel)
	{
		MoreInventoryMod.network.sendToServer(new StorageBoxButtonMessage(xCoord, yCoord, zCoord, channel));
	}
}