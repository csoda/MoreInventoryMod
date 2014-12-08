package moreinventory.tileentity.storagebox;

import moreinventory.MoreInventoryMod;
import moreinventory.item.inventory.InventoryPouch;
import moreinventory.network.StorageBoxButtonMessage;
import moreinventory.network.StorageBoxConfigMessage;
import moreinventory.network.StorageBoxContentsMessage;
import moreinventory.network.StorageBoxMessage;
import moreinventory.util.CSUtil;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityStorageBox extends TileEntity implements IInventory, IStorageBoxNet
{
	protected ItemStack[] inv;
	private ItemStack contents;

	public int ContentsItemCount;
	public byte face;
	public boolean isPrivate = false;
	public boolean canInsert = true;
	public boolean checkNBT = true;
	private String ownerName = MoreInventoryMod.defaultOwner;
	private StorageBoxType type;
	private StorageBoxNetworkManager StorageBoxManager;

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
		inv = new ItemStack[type.invSize];
		face = 0;
	}

	public StorageBoxType getStorageBoxType()
	{
		return this.type;
	}

	@Override
	public StorageBoxNetworkManager getStorageBoxNetworkManager()
	{
		if (StorageBoxManager == null)
		{
			makeNewBoxList();
		}

		return StorageBoxManager;
	}

	@Override
	public void setStorageBoxNetworkManager(StorageBoxNetworkManager sbnet)
	{
		StorageBoxManager = sbnet;
	}

	@Override
	public String getOwnerName()
	{
		return this.ownerName;
	}

	@Override
	public boolean isPrivate()
	{
		return isPrivate;
	}

	@SideOnly(Side.CLIENT)
	public void setOwnerName(String st)
	{
		this.ownerName = st;
	}

	public void rotateBlock()
	{
		int t = face;
		t = t == 2 ? 5 : t == 5 ? 3 : t == 3 ? 4 : 2;
		face = (byte) t;
		markDirty();
	}

	public int getFirstItemIndex()
	{

		int t = 0;

		for (int i = 0; i < inv.length; i++)
		{
			if (inv[i] != null)
			{
				t = i;
				break;
			}
		}

		return t;
	}

	public ItemStack getContents()
	{

		return this.contents;
	}

	public Item getContentsItem()
	{
		Item item = null;
		ItemStack stack = this.getContents();
		if (stack != null)
		{
			item = stack.getItem();
		}
		return item;
	}

	public void setContents(ItemStack item)
	{
		if (item != null)
		{
			this.contents = item;
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

	/*** IInventory ***/

	@Override
	public int getSizeInventory()
	{
		return this.type.invSize + 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot != this.type.invSize ? inv[slot] : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (stack != null)
		{
			if (getContents() == null)
			{
				registerItems(stack);
			}

			if (!isSameAsContents(stack) || slot == this.type.invSize)
			{
				getStorageBoxNetworkManager().linkedPutIn(stack, this, false);
				CSUtil.dropItem(worldObj, stack, xCoord, yCoord, zCoord);
			}
			else
			{
				inv[slot] = stack;
			}

			if (stack.stackSize > getInventoryStackLimit())
			{
				stack.stackSize = getInventoryStackLimit();
			}
		}
		else
		{
			if (slot < this.type.invSize)
			{
				inv[slot] = null;
			}
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
	public void markDirty()
	{
		if (!worldObj.isRemote)
		{
			getContentItemCount();
			getStorageBoxNetworkManager().updateOnInvChanged(worldObj, xCoord, yCoord, zCoord, getContents());
			sendPacket();
		}
		super.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	{

		boolean flg = par2ItemStack != null
				&& (par1 != inv.length && (getContents() == null || isSameAsContents(par2ItemStack)) || par1 == inv.length
						&& canInsert && getStorageBoxNetworkManager().canLinkedImport(par2ItemStack, this));
		return flg;
	}

	/*** MainMethod ***/

	public boolean registerItems(ItemStack itemstack)
	{
		if (itemstack != null && getContents() == null)
		{
			contents = itemstack;
			int dimID = worldObj.provider.dimensionId;
			getStorageBoxNetworkManager().getBoxList().registerItem(xCoord, yCoord, zCoord, dimID, getContents());
			sendContents();
			return true;
		}
		return false;
	}

	public boolean isSameAsContents(ItemStack itemstack)
	{
		boolean flg = itemstack != null
				&& (this.getContents() == null || CSUtil.compareStacksWithDamage(itemstack, this.getContents()));
		if (flg && itemstack.hasTagCompound() && checkNBT)
		{
			for (int i = 0; i < this.getSizeInventory(); i++)
			{
				ItemStack item = getStackInSlot(i);
				if (item != null && !ItemStack.areItemStackTagsEqual(itemstack, item))
				{
					return false;
				}
			}
		}

		return flg;
	}

	public int getContentItemCount()
	{
		ContentsItemCount = 0;
		int k = inv.length;
		for (int i = 0; i < k; i++)
		{
			if (getStackInSlot(i) != null)
			{
				ContentsItemCount += getStackInSlot(i).stackSize;
			}
		}
		return ContentsItemCount;
	}

	public boolean tryPutIn(ItemStack parItemStack)
	{
		if (isSameAsContents(parItemStack))
		{
			CSUtil.mergeItemStack(parItemStack, this);
			return true;
		}

		return false;
	}

	public void collectAllItemStack(IInventory perInv)
	{
		if (this.getContents() != null)
		{
			int k = perInv.getSizeInventory();
			for (int i = 0; i < k; i++)
			{
				ItemStack itemstack = perInv.getStackInSlot(i);
				if (itemstack != null)
				{
					if (itemstack.getItem() == MoreInventoryMod.Pouch)
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

	public boolean canMergeItemStack(ItemStack parItemStack)
	{
		int t = this.type.invSize;
		ItemStack itemstack;
		for (int i = 0; i < t; i++)
		{
			itemstack = this.getStackInSlot(i);
			if (itemstack == null)
			{
				return true;
			}
		}
		for (int i = 0; i < t; i++)
		{
			itemstack = this.getStackInSlot(i);
			if (itemstack.stackSize != itemstack.getMaxStackSize())
			{
				return itemstack.getMaxStackSize() - itemstack.stackSize >= parItemStack.stackSize;
			}
		}

		return false;
	}

	public ItemStack loadItemStack(int flg)
	{
		int maxCount = 0;
		ItemStack retItemStack = null;

		for (int i = 0; i < inv.length; i++)
		{
			ItemStack itemstack = inv[i];
			if (itemstack != null)
			{
				if (retItemStack == null)
				{
					retItemStack = itemstack.copy();
					retItemStack.stackSize = 0;
					maxCount = flg == 0 ? itemstack.getMaxStackSize() : flg;
					if (maxCount > itemstack.getMaxStackSize())
					{
						maxCount = itemstack.getMaxStackSize();
					}
				}

				if (ItemStack.areItemStackTagsEqual(retItemStack, itemstack))
				{
					int l = maxCount - retItemStack.stackSize;

					if (itemstack.stackSize < l)
					{
						l = itemstack.stackSize;
					}
					decrStackSize(i, l);
					retItemStack.stackSize += l;

					if (retItemStack.stackSize == maxCount)
					{
						break;
					}
				}
			}
		}
		return retItemStack;
	}

	public boolean rightClickEvent(World world, EntityPlayer player, int x, int y, int z)
	{
		boolean imp = canInsert;

		clickCount++;
		if (clickCount == 1)
		{
			clickTime = 16;
			ItemStack itemstack = player.getCurrentEquippedItem();
			registerItems(itemstack);
			canInsert = false;
			tryPutIn(itemstack);
			canInsert = imp;
		}
		else if (clickCount == 2)
		{
			canInsert = false;
			collectAllItemStack(player.inventory);
			canInsert = imp;
			updatePlayerInventory(player);
			player.onUpdate();
		}
		else if (clickCount == 3)
		{
			clickCount = 0;
			getStorageBoxNetworkManager().linkedCollect(player.inventory);
			StorageBoxManager.updateOnTripleClicked(worldObj, xCoord, yCoord, zCoord, getContents());
			updatePlayerInventory(player);
			player.onUpdate();

		}

		return true;
	}

	public void leftClickEvent(EntityPlayer player)
	{
		int slot = player.inventory.getFirstEmptyStack();
		if (getContents() != null && slot != -1)
		{
			int num = !player.isSneaking() ? 0 : 1;
			ItemStack itemstack = loadItemStack(num);
			player.inventory.addItemStackToInventory(itemstack);
		}
	}

	public void updatePlayerInventory(EntityPlayer player)
	{
		ItemStack[] pinv = player.inventory.mainInventory;
		for (int i = 0; i < pinv.length; i++)
		{
			ItemStack itemstack = pinv[i];
			if (itemstack != null && itemstack.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(i, null);
			}
		}
	}

	@Override
	public void updateEntity()
	{
		if (clickTime > 0 && --clickTime == 0)
			clickCount = 0;
	}

	public boolean isFull()
	{
		return getPowerOutput(this) == 15;
	}

	public static int getPowerOutput(TileEntityStorageBox tile)
	{
		int ret = 0;
		if (tile.type != StorageBoxType.Glass && tile.type != StorageBoxType.Ender && tile.getContents() != null)
		{
			ret = 15 * tile.ContentsItemCount / tile.type.invSize / tile.getContents().getMaxStackSize();
		}
		return ret;
	}

	/*** SBNetwork ***/
	public void makeNewBoxList()
	{
		StorageBoxManager = new StorageBoxNetworkManager(worldObj, xCoord, yCoord, zCoord, ownerName);
	}

	public void onPlaced(EntityLivingBase entity)
	{
		if (entity instanceof EntityPlayer)
		{
			this.ownerName = ((EntityPlayer) entity).getDisplayName();
		}
		for (int i = 0; i < 6; i++)
		{
			int[] pos = CSUtil.getSidePos(xCoord, yCoord, zCoord, Facing.oppositeSide[i]);
			TileEntity tile = worldObj.getTileEntity(pos[0], pos[1], pos[2]);
			if (tile != null && tile instanceof IStorageBoxNet)
			{
				StorageBoxNetworkManager sbnet = ((IStorageBoxNet) tile).getStorageBoxNetworkManager();
				sbnet.addNetwork(worldObj, xCoord, yCoord, zCoord);
				sbnet.getBoxList().registerItem(xCoord, yCoord, zCoord, this.worldObj.provider.dimensionId,
						getContents());
				return;
			}
		}
		if (StorageBoxManager == null)
		{
			this.makeNewBoxList();
		}
	}

	public void onNeighborRemoved()
	{
		if (getStorageBoxNetworkManager().getBoxList().isOnBoxList(xCoord, yCoord, zCoord,
				worldObj.getWorldInfo().getVanillaDimension()))
		{
			getStorageBoxNetworkManager().reCreateNetwork();
		}
	}

	/*** Upgrade ***/
	public TileEntityStorageBox upgrade(int type)
	{
		TileEntityStorageBox tile = StorageBoxType.makeEntity(type);
		System.arraycopy(this.inv, 0, tile.inv, 0, this.inv.length);
		tile.face = this.face;
		tile.contents = this.contents;
		tile.ContentsItemCount = this.ContentsItemCount;
		tile.isPrivate = this.isPrivate;
		tile.ownerName = this.ownerName;

		return tile;
	}

	/*** Data ***/

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getShort("Slot2");
			this.inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
		this.contents = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("Contents"));
		this.ContentsItemCount = par1NBTTagCompound.getInteger("ContentsItemCount");
		this.face = par1NBTTagCompound.getByte("face");
		this.canInsert = par1NBTTagCompound.getBoolean("canInsert");
		this.checkNBT = par1NBTTagCompound.getBoolean("checkNBT");
		this.isPrivate = par1NBTTagCompound.getBoolean("isPrivate");
		this.ownerName = par1NBTTagCompound.getString("owner");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inv.length; ++i)
		{
			if (this.inv[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setShort("Slot2", (short) i);
				this.inv[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		par1NBTTagCompound.setTag("Items", nbttaglist);
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.contents != null)
		{
			contents.writeToNBT(nbt);
		}
		par1NBTTagCompound.setTag("Contents", nbt);
		par1NBTTagCompound.setInteger("ContentsItemCount", this.ContentsItemCount);
		par1NBTTagCompound.setInteger("face", this.face);
		par1NBTTagCompound.setBoolean("isPrivate", this.isPrivate);
		par1NBTTagCompound.setBoolean("canInsert", canInsert);
		par1NBTTagCompound.setBoolean("checkNBT", checkNBT);
		par1NBTTagCompound.setString("owner", this.ownerName);
	}

	public TileEntityStorageBox updateFromMetadata(int k)
	{
		if (worldObj != null && worldObj.isRemote)
		{
			if (k != type.ordinal())
			{
				worldObj.setTileEntity(xCoord, yCoord, zCoord, StorageBoxType.makeEntity(k));
				return (TileEntityStorageBox) worldObj.getTileEntity(xCoord, yCoord, zCoord);
			}
		}
		return this;
	}

	/*** Packet ***/
	@Override
	public Packet getDescriptionPacket()
	{
		sendContents();
		sendPacket();
		return null;
	}

	public void handlePacket(int count, byte face)
	{
		this.ContentsItemCount = count;
		this.face = face;
		if (getContents() != null)
		{
			int maxStackSize = getContents().getMaxStackSize();
			displayedStackSize = (byte) (ContentsItemCount % maxStackSize);
			displayedStackCount = (ContentsItemCount - displayedStackSize) / maxStackSize;
			if (displayedStackSize > 9999)
			{
				displayedStackCount = 1;
				while (displayedStackSize > 99999)
				{
					displayedStackSize /= 10;
					displayedStackCount++;
				}
			}
		}

	}

	public void handlePacketContents(ItemStack itemstack)
	{
		this.contents = itemstack;
	}

	public void handlePacketConfig(boolean isPrivate, boolean checkNBT, boolean canInsert, int connectCount,
			String owner)
	{
		this.isPrivate = isPrivate;
		this.checkNBT = checkNBT;
		this.canInsert = canInsert;
		this.connectCount = connectCount;
		this.ownerName = owner;
	}

	public void handlePacketButton(byte channel, String owner)
	{
		if (!isPrivate() || ownerName.equals(owner) || ownerName.equals(MoreInventoryMod.defaultOwner))
		{
			if (channel == 0)
			{
				this.isPrivate = !isPrivate;
			}
			else if (channel == 1)
			{
				this.checkNBT = !checkNBT;
			}
			else if (channel == 2)
			{
				this.canInsert = !canInsert;
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
		MoreInventoryMod.network.sendToDimension(new StorageBoxConfigMessage(xCoord, yCoord, zCoord, isPrivate, checkNBT, canInsert, getStorageBoxNetworkManager().getKnownList().getListSize(), ownerName), worldObj.provider.dimensionId);
	}

	public void sendGUIPacketToServer(byte channel)
	{
		MoreInventoryMod.network.sendToServer(new StorageBoxButtonMessage(xCoord, yCoord, zCoord, channel));
	}
}