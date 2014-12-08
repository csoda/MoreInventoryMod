package moreinventory.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class CSUtil
{
	public static int[] getSidePos(int x, int y, int z, int side)
	{
		int rx = x;
		int ry = y;
		int rz = z;

		switch (side)
		{
			case 0:
				ry--;
				break;
			case 1:
				ry++;
				break;
			case 2:
				rz--;
				break;
			case 3:
				rz++;
				break;
			case 4:
				rx--;
				break;
			case 5:
				rx++;
				break;
		}
		int[] rpos = { rx, ry, rz };
		return rpos;
	}

	public static boolean compareItems(ItemStack item1, Item item2)
	{
		if (item1 != null)
		{
			return item1.getItem() == item2;
		}
		return false;
	}

	public static boolean compareItems(Item item1, ItemStack item2)
	{
		return compareItems(item2, item1);
	}

	public static boolean compareStacksWithDamage(ItemStack item1, ItemStack item2)
	{
		if (item1 != null && item2 != null)
		{
			if (item1.getItem() == item2.getItem() && (item1.isItemDamaged() && item2.isItemDamaged() || item1.getItemDamage() == item2.getItemDamage()))
			{
				return true;
			}
		}

		return false;
	}

	public static Item getItemBlock(Block block)
	{
		return new ItemStack(block).getItem();
	}

	public static void checkNull(IInventory iinv)
	{
		for (int i = 0; i < iinv.getSizeInventory(); i++)
		{
			ItemStack item = iinv.getStackInSlot(i);

			if (item != null && item.stackSize == 0)
			{
				iinv.setInventorySlotContents(i, null);
			}
		}
	}

	public static void checkNullStack(IInventory iinv, int slot)
	{
		ItemStack item = iinv.getStackInSlot(slot);

		if (item != null && item.stackSize == 0)
		{
			iinv.setInventorySlotContents(slot, null);
		}
	}

	public static void dropItem(World world, ItemStack itemstack, double x, double y, double z)
	{
		if (itemstack != null && itemstack.stackSize > 0)
		{
			Random rand = new Random();
			float rx = rand.nextFloat() * 0.8F + 0.1F;
			float ry = rand.nextFloat() * 0.8F + 0.1F;
			float rz = rand.nextFloat() * 0.8F + 0.1F;
			EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(itemstack.getItem(), itemstack.stackSize, itemstack.getItemDamage()));

			if (itemstack.hasTagCompound())
			{
				entityItem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
			}

			float factor = 0.05F;
			entityItem.motionX = rand.nextGaussian() * factor;
			entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
			entityItem.motionZ = rand.nextGaussian() * factor;
			entityItem.delayBeforeCanPickup = 20;
			world.spawnEntityInWorld(entityItem);
			itemstack.stackSize = 0;
		}
	}

	public static boolean mergeItemStack(ItemStack perItemStack, IInventory iinv)
	{
		return mergeItemStack(perItemStack, iinv, 0);
	}

	public static boolean mergeItemStack(ItemStack parItemStack, IInventory iinv, int side)
	{
		boolean flg = false;
		int k = iinv.getSizeInventory();

		if (parItemStack == null)
		{
			return false;
		}

		if (parItemStack.isStackable())
		{
			for (int i = 0; i < k; i++)
			{
				ItemStack itemstack = iinv.getStackInSlot(i);

				if (itemstack != null && itemstack.getItem() == parItemStack.getItem()
					&& (!parItemStack.getHasSubtypes() || parItemStack.getItemDamage() == itemstack.getItemDamage())
					&& ItemStack.areItemStackTagsEqual(parItemStack, itemstack))
				{
					if (canAccessFromSide(iinv, i, side) && canInsertFromSide(iinv, parItemStack, i, side))
					{
						int l = itemstack.stackSize + parItemStack.stackSize;

						if (l <= parItemStack.getMaxStackSize())
						{
							parItemStack.stackSize = 0;
							ItemStack itemstack1 = itemstack.copy();
							itemstack1.stackSize = l;
							iinv.setInventorySlotContents(i, itemstack1);
							flg = true;
						}
						else if (itemstack.stackSize < parItemStack.getMaxStackSize())
						{
							parItemStack.stackSize -= parItemStack.getMaxStackSize() - itemstack.stackSize;
							ItemStack itemstack1 = itemstack.copy();
							itemstack1.stackSize = parItemStack.getMaxStackSize();
							iinv.setInventorySlotContents(i, itemstack1);
							flg = true;
						}
					}
				}

				if (parItemStack.stackSize == 0)
				{
					parItemStack = null;
					return flg;
				}
			}
		}

		if (parItemStack.stackSize > 0)
		{
			for (int i = 0; i < k; i++)
			{
				ItemStack itemstack = iinv.getStackInSlot(i);

				if (itemstack == null && canAccessFromSide(iinv, i, side) && canInsertFromSide(iinv, parItemStack, i, side))
				{
					iinv.setInventorySlotContents(i, parItemStack.copy());
					parItemStack.stackSize = 0;
					flg = true;
					break;
				}
			}
		}

		return flg;
	}

	public static boolean canAccessFromSide(IInventory iinv, int slot, int side)
	{
		if (iinv instanceof ISidedInventory)
		{
			int[] sideInv = ((ISidedInventory) iinv).getAccessibleSlotsFromSide(side);
			int k = sideInv.length;
			for (int t = 0; t < k; t++)
			{
				if (sideInv[t] == slot)
				{
					return true;
				}
			}
			return false;
		}

		return true;
	}

	public static boolean canExtractFromSide(IInventory par0IInventory, ItemStack par1ItemStack, int par2, int par3)
	{
		return !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory)par0IInventory).canExtractItem(par2, par1ItemStack, par3);
	}

	public static boolean canInsertFromSide(IInventory par0IInventory, ItemStack par1ItemStack, int par2, int par3)
	{
		return !par0IInventory.isItemValidForSlot(par2, par1ItemStack) ? false : !(par0IInventory instanceof ISidedInventory) || ((ISidedInventory) par0IInventory).canInsertItem(par2, par1ItemStack, par3);
	}
}