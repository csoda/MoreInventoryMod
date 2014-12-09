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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public final class MIMUtils
{
	public static String getUniqueName(Block block)
	{
		if (block == null)
		{
			return "";
		}

		UniqueIdentifier unique = GameRegistry.findUniqueIdentifierFor(block);

		return unique == null ? "" : unique.toString();
	}

	public static int[] getSidePos(int x, int y, int z, int side)
	{
		int sideX = x;
		int sideY = y;
		int sideZ = z;

		switch (side)
		{
			case 0:
				--sideY;
				break;
			case 1:
				++sideY;
				break;
			case 2:
				--sideZ;
				break;
			case 3:
				++sideZ;
				break;
			case 4:
				--sideX;
				break;
			case 5:
				++sideX;
				break;
		}

		return new int[] {sideX, sideY, sideZ};
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

	public static void checkNull(IInventory inventory)
	{
		for (int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize == 0)
			{
				inventory.setInventorySlotContents(i, null);
			}
		}
	}

	public static void checkNullStack(IInventory inventory, int slot)
	{
		ItemStack item = inventory.getStackInSlot(slot);

		if (item != null && item.stackSize == 0)
		{
			inventory.setInventorySlotContents(slot, null);
		}
	}

	public static void dropItem(World world, ItemStack itemstack, double x, double y, double z)
	{
		if (!world.isRemote && itemstack != null && itemstack.stackSize > 0)
		{
			Random rand = new Random();
			float randX = rand.nextFloat() * 0.8F + 0.1F;
			float randY = rand.nextFloat() * 0.8F + 0.1F;
			float randZ = rand.nextFloat() * 0.8F + 0.1F;
			EntityItem entity = new EntityItem(world, x + randX, y + randY, z + randZ, new ItemStack(itemstack.getItem(), itemstack.stackSize, itemstack.getItemDamage()));

			if (itemstack.hasTagCompound())
			{
				entity.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
			}

			float factor = 0.05F;
			entity.motionX = rand.nextGaussian() * factor;
			entity.motionY = rand.nextGaussian() * factor + 0.2F;
			entity.motionZ = rand.nextGaussian() * factor;
			entity.delayBeforeCanPickup = 20;
			world.spawnEntityInWorld(entity);
			itemstack.stackSize = 0;
		}
	}

	public static boolean mergeItemStack(ItemStack itemstack, IInventory inventory)
	{
		return mergeItemStack(itemstack, inventory, 0);
	}

	public static boolean mergeItemStack(ItemStack itemstack, IInventory inventory, int side)
	{
		boolean flag = false;
		int size = inventory.getSizeInventory();

		if (itemstack == null)
		{
			return false;
		}

		if (itemstack.isStackable())
		{
			for (int i = 0; i < size; i++)
			{
				ItemStack item = inventory.getStackInSlot(i);

				if (item != null && item.getItem() == itemstack.getItem() && (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == item.getItemDamage())
					&& ItemStack.areItemStackTagsEqual(itemstack, item))
				{
					if (canAccessFromSide(inventory, i, side) && canInsertFromSide(inventory, itemstack, i, side))
					{
						int l = item.stackSize + itemstack.stackSize;

						if (l <= itemstack.getMaxStackSize())
						{
							itemstack.stackSize = 0;
							ItemStack itemstack1 = item.copy();
							itemstack1.stackSize = l;
							inventory.setInventorySlotContents(i, itemstack1);
							flag = true;
						}
						else if (item.stackSize < itemstack.getMaxStackSize())
						{
							itemstack.stackSize -= itemstack.getMaxStackSize() - item.stackSize;
							ItemStack itemstack1 = item.copy();
							itemstack1.stackSize = itemstack.getMaxStackSize();
							inventory.setInventorySlotContents(i, itemstack1);
							flag = true;
						}
					}
				}

				if (itemstack.stackSize == 0)
				{
					itemstack = null;
					return flag;
				}
			}
		}

		if (itemstack.stackSize > 0)
		{
			for (int i = 0; i < size; i++)
			{
				ItemStack item = inventory.getStackInSlot(i);

				if (item == null && canAccessFromSide(inventory, i, side) && canInsertFromSide(inventory, itemstack, i, side))
				{
					inventory.setInventorySlotContents(i, itemstack.copy());
					itemstack.stackSize = 0;
					flag = true;
					break;
				}
			}
		}

		return flag;
	}

	public static boolean canAccessFromSide(IInventory inventory, int slot, int side)
	{
		if (inventory instanceof ISidedInventory)
		{
			int[] slots = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);

			for (int i = 0; i < slots.length; i++)
			{
				if (slots[i] == slot)
				{
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public static boolean canExtractFromSide(IInventory inventory, ItemStack itemstack, int slot, int side)
	{
		return !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canExtractItem(slot, itemstack, side);
	}

	public static boolean canInsertFromSide(IInventory inventory, ItemStack itemstack, int slot, int side)
	{
		return !inventory.isItemValidForSlot(slot, itemstack) ? false : !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canInsertItem(slot, itemstack, side);
	}
}