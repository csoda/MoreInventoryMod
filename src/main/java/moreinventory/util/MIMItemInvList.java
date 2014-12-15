package moreinventory.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

public class MIMItemInvList extends MIMItemList implements INBTSaveData
{
	private final List<ItemStack[]> inventories = Lists.newArrayList();

	private String tagName;

	public MIMItemInvList() {}

	public MIMItemInvList(String tag)
	{
		this.tagName = tag;
	}

	public ItemStack[] getInventory(ItemStack itemstack)
	{
		int i = getItemIndex(itemstack);

		return i != -1 ? inventories.get(i) : new ItemStack[2];
	}

	@Override
	public void addItem(ItemStack itemstack)
	{
		int size = itemstack.stackSize;
		int i = getItemIndex(itemstack);

		if (i == -1)
		{
			registerItem(itemstack);
			i = getItemIndex(itemstack);
		}

		int j = count.get(i);

		if (j < Integer.MAX_VALUE - size)
		{
			count.set(i, j + size);
		}
		else
		{
			count.set(i, Integer.MAX_VALUE);
		}
	}

	public void registerItem(ItemStack itemstack)
	{
		if (itemstack != null && getItemIndex(itemstack) == -1)
		{
			list.add(itemstack);
			count.add(0);
			inventories.add(new ItemStack[2]);
		}
	}

	public ItemStack updateState(int index)
	{
		if (index != -1)
		{
			ItemStack[] items = inventories.get(index);

			if (items[0] != null)
			{
				if (MIMUtils.compareStacksWithDamage(items[0], list.get(index)))
				{
					addItem(items[0]);

					items[0] = null;
				}
				else
				{
					return items[0];
				}
			}

			if (items[1] == null || items[1].stackSize != items[1].getMaxStackSize())
			{
				if (items[1] != null)
				{
					addItem(items[1]);
				}

				items[1] = loadNewItemStack(index);
			}
		}

		return null;
	}

	public ItemStack loadNewItemStack(int index)
	{
		if (index != -1)
		{
			ItemStack itemstack = ItemStack.copyItemStack(list.get(index));

			if (itemstack != null)
			{
				int size = itemstack.getMaxStackSize();
				int i = count.get(index);

				if (i < size)
				{
					size = i;
				}

				count.set(index, count.get(index) - size);
				itemstack.stackSize = size;

				return itemstack;
			}
		}

		return null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if (!Strings.isNullOrEmpty(tagName))
		{
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < list.size(); i++)
			{
				ItemStack itemstack = list.get(i);
				NBTTagCompound data1 = new NBTTagCompound();
				NBTTagCompound data2 = new NBTTagCompound();

				if (itemstack != null)
				{
					itemstack.writeToNBT(data2);
				}

				itemstack = inventories.get(i)[1];

				data1.setTag("Item", data2);
				data1.setInteger("Count", count.get(i) + (itemstack != null ? itemstack.stackSize : 0));
				nbttaglist.appendTag(data1);
			}

			nbt.setTag(tagName, nbttaglist);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist = (NBTTagList)nbt.getTag(tagName);

		if (nbttaglist != null)
		{
			for (int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound data = nbttaglist.getCompoundTagAt(i);
				list.add(ItemStack.loadItemStackFromNBT(data.getCompoundTag("Item")));
				count.add(data.getInteger("Count"));
				inventories.add(new ItemStack[2]);
				updateState(i);
			}
		}
	}
}