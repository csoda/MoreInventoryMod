package moreinventory.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moreinventory.core.MoreInventoryMod;
import moreinventory.network.StorageBoxConfigMessage;
import moreinventory.network.StorageBoxContentsMessage;
import moreinventory.network.StorageBoxMessage;
import moreinventory.tileentity.storagebox.StorageBoxType;
import moreinventory.util.MIMUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

public class EntityMinecartStorageBox extends EntityMinecart implements IInventory
{
	protected ItemStack[] storageItems;

	private String ownerID = MoreInventoryMod.defaultOwnerID;
	private String typeName;

	private ItemStack contents;

	public int contentsCount;
	public byte face;
	public boolean canInsert = true;
	public boolean checkNBT = true;

	protected byte clickTime = 0;
	protected byte clickCount = 0;

	private boolean dropContentsWhenDead = true;

	@SideOnly(Side.CLIENT)
	public byte displayedStackSize;
	@SideOnly(Side.CLIENT)
	public int displayedStackCount;

	public EntityMinecartStorageBox(World world)
	{
		super(world);
	}

	public EntityMinecartStorageBox(World world, double posX, double posY, double posZ)
	{
		super(world, posX, posY, posZ);
	}

	public void setTypeName(String name)
	{
		typeName = name;
	}

	public String getTypeName()
	{
		return StorageBoxType.isExistType(typeName) ? typeName : "Wood";
	}

	public void setOwner(String uuid)
	{
		ownerID = uuid;
	}

	public String getOwner()
	{
		return ownerID;
	}

	public String getOwnerName()
	{
		return MoreInventoryMod.playerNameCache.getName(getOwner());
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

	public int getUsableInventorySize()
	{
		return StorageBoxType.getInventorySize(typeName);
	}

	@Override
	public int getSizeInventory()
	{
		return getUsableInventorySize() + 1;
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

			if (!isSameAsContents(itemstack) || slot == getUsableInventorySize())
			{
				MIMUtils.dropItem(worldObj, itemstack, posX, posY, posZ);
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
		else if (slot < getUsableInventorySize())
		{
			storageItems[slot] = null;
		}

		markDirty();
	}

	@Override
	public int getMinecartType()
	{
		return 10;
	}

	@Override
	public String getInventoryName()
	{
		return "MinecartStorageBox";
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

				if (itemstack.stackSize <= 0)
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
		return isEntityAlive() && player.getDistanceSq(posX, posY, posZ) < 64.0D;
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
			sendPacket();
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return itemstack != null && !isFull() &&((slot != getSizeInventory() && (getContents() == null || isSameAsContents(itemstack))) || (slot == getSizeInventory() && canInsert));
	}

	public boolean registerItems(ItemStack itemstack)
	{
		if (itemstack != null && getContents() == null)
		{
			contents = itemstack;
			sendContents();

			return true;
		}

		return false;
	}

	protected void clearRegister()
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
		return isSameAsContents(itemstack) && MIMUtils.mergeItemStack(itemstack, this);
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
					tryPutIn(itemstack);
				}
			}
		}
	}

	public boolean canMergeItemStack(ItemStack itemstack)
	{
		int size = getUsableInventorySize();

		for (int i = 0; i < size; ++i)
		{
			if (getStackInSlot(i) == null)
			{
				return true;
			}
		}

		for (int i = 0; i < size; ++i)
		{
			ItemStack item = getStackInSlot(i);

			if (item.stackSize < item.getMaxStackSize())
			{
				if(item.getMaxStackSize() - item.stackSize >= itemstack.stackSize)
				{
					return true;
				}
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

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (clickTime > 0 && --clickTime <= 0)
		{
			clickCount = 0;
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player)))
		{
			return true;
		}

		boolean prevInsert = canInsert;

		if (!worldObj.isRemote)
		{
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
					MIMUtils.checkNull(player.inventory);
					player.onUpdate();
					break;
				default:
					clickCount = 0;
					break;
			}
		}

		return true;
	}

	@Override
	public boolean hitByEntity(Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null && current.getItem().getToolClasses(current).contains("pickaxe"))
			{
				return false;
			}

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

			return true;
		}

		return false;
	}

	public boolean isFull()
	{
		return getPowerOutput(this) == 15;
	}

	public static int getPowerOutput(EntityMinecartStorageBox entity)
	{
		if (!entity.getTypeName().equals("Glass") && !entity.getTypeName().equals("Ender") && entity.getContents() != null)
		{
			return 15 * entity.contentsCount / entity.getUsableInventorySize() / entity.getContents().getMaxStackSize();
		}

		return 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		typeName = nbt.getString("typeName");
		this.storageItems = new ItemStack[getSizeInventory()];

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
		nbt.setBoolean("canInsert", canInsert);
		nbt.setBoolean("checkNBT", checkNBT);
		nbt.setString("owner", ownerID);
		nbt.setString("typeName" , typeName);
	}

	@Override
	public void travelToDimension(int dim)
	{
		dropContentsWhenDead = false;

		super.travelToDimension(dim);
	}

	protected void dropItems()
	{
		for (int i = 0; i < getSizeInventory(); ++i)
		{
			ItemStack itemstack = getStackInSlot(i);

			if (itemstack != null)
			{
				float f = rand.nextFloat() * 0.8F + 0.1F;
				float f1 = rand.nextFloat() * 0.8F + 0.1F;
				float f2 = rand.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0)
				{
					int j = rand.nextInt(21) + 10;

					if (j > itemstack.stackSize)
					{
						j = itemstack.stackSize;
					}

					itemstack.stackSize -= j;
					EntityItem item = new EntityItem(worldObj, posX + (double)f, posY + (double)f1, posZ + (double)f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

					if (itemstack.hasTagCompound())
					{
						item.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
					}

					float f3 = 0.05F;
					item.motionX = (double)((float)rand.nextGaussian() * f3);
					item.motionY = (double)((float)rand.nextGaussian() * f3 + 0.2F);
					item.motionZ = (double)((float)rand.nextGaussian() * f3);

					worldObj.spawnEntityInWorld(item);
				}
			}
		}
	}

	@Override
	public void killMinecart(DamageSource source)
	{
		super.killMinecart(source);

		dropItems();
	}

	@Override
	public void setDead()
	{
		if (dropContentsWhenDead)
		{
			dropItems();
		}

		super.setDead();
	}

	@Override
	protected void applyDrag()
	{
		int i = 15 - Container.calcRedstoneFromInventory(this);
		float f = 0.98F + (float)i * 0.001F;

		motionX *= (double)f;
		motionY *= 0.0D;
		motionZ *= (double)f;
	}

	@SideOnly(Side.CLIENT)
	public void handlePacket(int config1, byte config2, String config3)
	{
		contentsCount = config1;
		face = config2;

		if (getContents() != null)
		{
			int size = getContents().getMaxStackSize();
			displayedStackSize = (byte)(contentsCount % size);
			displayedStackCount = (contentsCount - displayedStackSize) / size;
		}

		if (!typeName.equals(config3))
		{
			typeName = config3;
		}
	}

	@SideOnly(Side.CLIENT)
	public void handlePacketContents(ItemStack itemstack)
	{
		contents = itemstack;
	}

	@SideOnly(Side.CLIENT)
	public void handlePacketConfig(boolean config1, boolean config2, String owner)
	{
		checkNBT = config1;
		canInsert = config2;
		ownerID = owner;
	}

	public void handlePacketButton(byte channel, String owner)
	{
		if (ownerID.equals(owner) || ownerID.equals(MoreInventoryMod.defaultOwnerID))
		{
			switch (channel)
			{
				case 0:
					checkNBT = !checkNBT;
					break;
				case 1:
					canInsert = !canInsert;
					break;
			}

			sendGUIPacketToClient();
		}
	}

	public void sendPacket()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxMessage(getEntityId(), getContentItemCount(), face, getTypeName()), worldObj.provider.dimensionId);
	}

	public void sendContents()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxContentsMessage(getEntityId(), contents), worldObj.provider.dimensionId);
	}

	public void sendGUIPacketToClient()
	{
		MoreInventoryMod.network.sendToDimension(new StorageBoxConfigMessage(getEntityId(), checkNBT, canInsert, ownerID), worldObj.provider.dimensionId);
	}
}