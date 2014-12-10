package moreinventory.inventory;

import moreinventory.core.MoreInventoryMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryPotionHolder implements IInventory
{
	private final ItemStack usingItem;
	private final ItemStack[] holderItems = new ItemStack[9];

	public InventoryPotionHolder(ItemStack itemstack)
	{
		this.usingItem = itemstack;
		this.readFromNBT();
	}

	public int getFirstPotionIndex()
	{
		ItemStack itemstack;

		for (int i = 0; i < holderItems.length; i++)
		{
			itemstack = getStackInSlot(i);

			if (itemstack != null && itemstack.getItem() == Items.potionitem && itemstack.getItemDamage() != 0)
			{
				return i;
			}
		}

		return -1;
	}

	public ItemStack getFirstPotion()
	{
		int first = getFirstPotionIndex();

		return first != -1 ? getStackInSlot(first) : null;
	}

	public void drinkPotion(World world, EntityPlayer player)
	{
		int first = getFirstPotionIndex();

		if (first != -1)
		{
			ItemStack itemstack = getStackInSlot(first);
			itemstack.onFoodEaten(world, player);

			setInventorySlotContents(first, new ItemStack(Items.glass_bottle));
		}

		writeToNBT();
	}

	public void throwPotion(World world, EntityPlayer player)
	{
		int first = getFirstPotionIndex();
		ItemStack itemstack = getStackInSlot(first);

		if (itemstack != null)
		{
			itemstack.useItemRightClick(world, player);

			setInventorySlotContents(first, null);
		}

		writeToNBT();
	}

	@Override
	public int getSizeInventory()
	{
		return holderItems.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return holderItems[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
		holderItems[slot] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName()
	{
		return "InvPotionHolder";
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
	public void markDirty()
	{
		writeToNBT();
	}

	public void readFromNBT()
	{
		if (usingItem != null)
		{
			NBTTagCompound nbt = usingItem.getTagCompound();

			if (nbt == null)
			{
				return;
			}

			NBTTagList list = nbt.getTagList("Items", 10);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				int slot = data.getByte("Slot") & 0xFF;

				if (slot >= 0 && slot < holderItems.length)
				{
					holderItems[slot] = ItemStack.loadItemStackFromNBT(data);
				}
			}
		}
	}

	public void writeToNBT()
	{
		if (usingItem != null)
		{
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < holderItems.length; i++)
			{
				if (holderItems[i] != null)
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setByte("Slot", (byte)i);
					holderItems[i].writeToNBT(data);
					list.appendTag(data);
				}
			}

			NBTTagCompound nbt = usingItem.getTagCompound();

			if (nbt == null)
			{
				nbt = new NBTTagCompound();
			}

			nbt.setTag("Items", list);
			usingItem.setTagCompound(nbt);
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		ItemStack current = player.getCurrentEquippedItem();

		if (current != usingItem)
		{
			current = usingItem;
		}

		readFromNBT();

		if (player.getCurrentEquippedItem().getItem() != MoreInventoryMod.Potionholder)
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
		if (itemstack == null)
		{
			return false;
		}

		return itemstack.getItem() == Items.glass_bottle || itemstack.getItem() == Items.potionitem;
	}
}