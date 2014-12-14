package moreinventory.util;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

public class MIMItemBoxList extends MIMBoxList
{
	private final List<ItemStack> itemList = Lists.newArrayList();

	public MIMItemBoxList() {}

	public MIMItemBoxList(String tag)
	{
		super(tag);
	}

	@Override
	public boolean addBox(int x, int y, int z, int d)
	{
		return addBox(x, y, z, d, null);
	}

	public boolean addBox(int x, int y, int z, int d, ItemStack itemstack)
	{
		if (!isOnBoxList(x, y, z, d))
		{
			super.addBox(x, y, z, d);
			itemList.add(itemstack);

			return true;
		}

		registerItem(x, y, z, d, itemstack);

		return false;
	}

	@Override
	public boolean insBox(int index, int x, int y, int z, int d)
	{
		return insBox(index, x, y, z, d, null);
	}

	public boolean insBox(int index, int x, int y, int z, int d, ItemStack itemstack)
	{
		super.insBox(index, x, y, z, d);
		itemList.add(index, itemstack);

		return true;
	}

	@Override
	public void removeBox(int i)
	{
		super.removeBox(i);
		itemList.remove(i);
	}

	@Override
	public void addAllBox(MIMBoxList csList) {}

	public void addAllBoxList(MIMItemBoxList csList)
	{
		super.addAllBox(csList);
		itemList.addAll(csList.itemList);
	}

	public ItemStack getItem(int i)
	{
		return i < itemList.size() ? itemList.get(i) : null;
	}

	public int getItemDamage(int i)
	{
		ItemStack item = null;

		if (i < itemList.size())
		{
			item = itemList.get(i);
		}

		return item != null ? item.getItemDamage() : 0;
	}

	public void registerItem(int x, int y, int z, int d, ItemStack itemstack)
	{
		boolean flag = false;

		for (int i = 0; i < itemList.size(); i++)
		{
			int[] pos = getBoxPos(i);
			int dim = getDimensionID(i);

			if (x == pos[0] && y == pos[1] && z == pos[2] && dim == d)
			{
				itemList.set(i, itemstack);
				flag = true;
			}
		}

		if (!flag)
		{
			addBox(x, y, z, d, itemstack);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if (tagName != null)
		{
			super.writeToNBT(nbt);
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < itemList.size(); i++)
			{
				ItemStack itemstack = getItem(i);

				if (itemstack != null)
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setInteger("Index", i);
					itemstack.writeToNBT(data);
					list.appendTag(data);
				}
			}

			nbt.setTag(tagName + "Item", list);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		itemList.clear();

		NBTTagList list = (NBTTagList)nbt.getTag(tagName + "Item");

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);

				itemList.add(data.getInteger("Index"), ItemStack.loadItemStackFromNBT(data));
			}
		}
	}
}