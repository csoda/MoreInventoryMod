package moreinventory.util;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Lists;

public class MIMItemInvList extends MIMItemList implements IWorldDataSave
{
	private final List<ItemStack[]> inv = Lists.newArrayList();
	private String tagName;

	public MIMItemInvList() {}

	public MIMItemInvList(String tag)
	{
		this.tagName = tag;
	}

	public ItemStack[] getInv(ItemStack itemstack)
	{
		int i = getItemIndex(itemstack);

		return i != -1 ? inv.get(i) : new ItemStack[2];
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
			inv.add(new ItemStack[2]);
		}
	}

	public ItemStack updateState(int index)
	{
		if (index != -1)
		{
			ItemStack[] items = inv.get(index);

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
			ItemStack itemstack = list.get(index).copy();

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
		if (tagName != null)
		{
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < list.size(); i++)
			{
				NBTTagCompound data1 = new NBTTagCompound();
				NBTTagCompound data2 = new NBTTagCompound();
				ItemStack contents = list.get(i);

				if (contents != null)
				{
					contents.writeToNBT(data2);
				}

				ItemStack itemstack = inv.get(i)[1];

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
		NBTTagList nbttaglist = nbt.getTagList(tagName, 10);

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound data = nbttaglist.getCompoundTagAt(i);
			list.add(ItemStack.loadItemStackFromNBT(data.getCompoundTag("Item")));
			count.add(data.getInteger("Count"));
			inv.add(new ItemStack[2]);
			updateState(i);
		}
	}
}