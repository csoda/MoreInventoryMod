package moreinventory.item.inventory;

import moreinventory.MoreInventoryMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryPotionholder implements IInventory
{
	private ItemStack usingItem;
	private ItemStack[] inv;

	public InventoryPotionholder(ItemStack par1ItemStack)
	{
		usingItem = par1ItemStack;
		inv = new ItemStack[9];
		readFromNBT();
	}

	public int getFirstPotionIndex()
	{
		ItemStack itemstack;

		int k = inv.length;
		for (int i = 0; i < k; i++)
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
		int k = getFirstPotionIndex();
		return k != -1 ? this.getStackInSlot(k) : null;
	}

	public void drinkPotion(World par2World, EntityPlayer par3EntityPlayer)
	{
		int k = getFirstPotionIndex();
		if (k != -1)
		{
			ItemStack itemstack = this.getStackInSlot(k);
			itemstack.onFoodEaten(par2World, par3EntityPlayer);
			this.setInventorySlotContents(k, new ItemStack(Items.glass_bottle));
		}
		writeToNBT();
	}

	public void throwPotion(World par2World, EntityPlayer par3EntityPlayer)
	{
		int k = getFirstPotionIndex();
		ItemStack itemstack = this.getStackInSlot(k);
		if (itemstack != null)
		{
			itemstack.useItemRightClick(par2World, par3EntityPlayer);
			this.setInventorySlotContents(k, null);
		}
		writeToNBT();
	}

	/*** IInventory ***/

	@Override
	public int getSizeInventory()
	{
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
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
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		if (stack != null)
		{
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		this.writeToNBT();
	}

	public void readFromNBT()
	{
		if (usingItem != null)
		{
			NBTTagCompound nbttagcompound = usingItem.getTagCompound();
			if (nbttagcompound == null)
				return;
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xff;
				if (j >= 0 && j < inv.length)
				{
					inv[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				}
			}
		}
	}

	public void writeToNBT()
	{
		if (usingItem != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < inv.length; i++)
			{
				if (inv[i] != null)
				{
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					inv[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			NBTTagCompound nbttagcompound = usingItem.getTagCompound();
			if (nbttagcompound == null)
			{
				nbttagcompound = new NBTTagCompound();
			}
			nbttagcompound.setTag("Items", nbttaglist);
			usingItem.setTagCompound(nbttagcompound);
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		ItemStack eqItem = entityplayer.getCurrentEquippedItem();
		if (eqItem != usingItem)
		{
			eqItem = usingItem;
		}
		readFromNBT();
		if (entityplayer.getCurrentEquippedItem().getItem() != MoreInventoryMod.Potionholder)
		{
			return false;
		}
		return true;
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		if (itemstack == null)
			return false;
		return itemstack.getItem() == Items.glass_bottle || itemstack.getItem() == Items.potionitem;
	}

}
