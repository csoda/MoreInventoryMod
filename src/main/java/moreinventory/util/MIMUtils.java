package moreinventory.util;

import com.google.common.base.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

public final class MIMUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

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
		return item1 != null && item1.getItem() == item2;
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

			for (int slot1 : slots)
			{
				if (slot1 == slot)
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
		return inventory.isItemValidForSlot(slot, itemstack) && (!(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canInsertItem(slot, itemstack, side));
	}

	public static int getFirstSlot(ItemStack[] items, Item item)
	{
		for (int i = 0; i < items.length; ++i)
		{
			if (items[i] != null && items[i].getItem() == item)
			{
				return i;
			}
		}

		return -1;
	}

	public static int compareWithNull(Object o1, Object o2)
	{
		return (o1 == null ? 1 : 0) - (o2 == null ? 1 : 0);
	}

	public static final Comparator<Block> blockComparator = new Comparator<Block>()
	{
		@Override
		public int compare(Block o1, Block o2)
		{
			int i = compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				UniqueIdentifier unique1 = GameRegistry.findUniqueIdentifierFor(o1);
				UniqueIdentifier unique2 = GameRegistry.findUniqueIdentifierFor(o2);

				i = compareWithNull(unique1, unique2);

				if (i == 0 && unique1 != null && unique2 != null)
				{
					i = (unique1.modId.equals("minecraft") ? 0 : 1) - (unique2.modId.equals("minecraft") ? 0 : 1);

					if (i == 0)
					{
						i = unique1.modId.compareTo(unique2.modId);

						if (i == 0)
						{
							i = unique1.name.compareTo(unique1.name);
						}
					}
				}
			}

			return i;
		}
	};

	public static final Comparator<Item> itemComparator = new Comparator<Item>()
	{
		@Override
		public int compare(Item o1, Item o2)
		{
			int i = compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				UniqueIdentifier unique1 = GameRegistry.findUniqueIdentifierFor(o1);
				UniqueIdentifier unique2 = GameRegistry.findUniqueIdentifierFor(o2);

				i = compareWithNull(unique1, unique2);

				if (i == 0 && unique1 != null && unique2 != null)
				{
					i = (unique1.modId.equals("minecraft") ? 0 : 1) - (unique2.modId.equals("minecraft") ? 0 : 1);

					if (i == 0)
					{
						i = unique1.modId.compareTo(unique2.modId);

						if (i == 0)
						{
							i = unique1.name.compareTo(unique1.name);
						}
					}
				}
			}

			return i;
		}
	};

	public static final Comparator<ItemStack> itemStackComparator = new Comparator<ItemStack>()
	{
		@Override
		public int compare(ItemStack o1, ItemStack o2)
		{
			int i = compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = itemComparator.compare(o1.getItem(), o2.getItem());

				if (i == 0)
				{
					i = Integer.compare(o1.getItemDamage(), o2.getItemDamage());

					if (i == 0)
					{
						i = Integer.compare(o1.stackSize, o2.stackSize);

						if (i == 0)
						{
							NBTTagCompound nbt1 = o1.getTagCompound();
							NBTTagCompound nbt2 = o2.getTagCompound();

							i = compareWithNull(nbt1, nbt2);

							if (i == 0 && nbt1 != null && nbt2 != null)
							{
								i = Byte.compare(nbt1.getId(), nbt2.getId());
							}
						}
					}
				}
			}

			return i;
		}
	};

	public static final Comparator<BlockMeta> blockMetaComparator = new Comparator<BlockMeta>()
	{
		@Override
		public int compare(BlockMeta o1, BlockMeta o2)
		{
			int i = MIMUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = blockComparator.compare(o1.block, o2.block);

				if (i == 0)
				{
					i = Integer.compare(o1.meta, o2.meta);
				}
			}

			return i;
		}
	};

	public static boolean containsIgnoreCase(String s1, String s2)
	{
		if (Strings.isNullOrEmpty(s1) || Strings.isNullOrEmpty(s2))
		{
			return false;
		}

		return Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
	}

	public static boolean blockMetaFilter(BlockMeta entry, String filter)
	{
		if (entry == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		try
		{
			if (containsIgnoreCase(getUniqueName(entry.block), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		ItemStack itemstack = new ItemStack(entry.block, 1, entry.meta);

		if (itemstack.getItem() == null)
		{
			try
			{
				if (containsIgnoreCase(entry.block.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (containsIgnoreCase(entry.block.getLocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}
		else
		{
			try
			{
				if (containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (containsIgnoreCase(itemstack.getDisplayName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}

		return false;
	}
}