package moreinventory.util;

import com.google.common.base.Strings;
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
		boolean flg = false;

		if (!isOnBoxList(x, y, z, d))
		{
			super.addBox(x, y, z, d);

			itemList.add(null);
			flg = true;
		}

		registerItem(x, y, z, d, itemstack);

		return flg;
	}

	@Override
	public void removeBox(int i)
	{
		super.removeBox(i);
		itemList.remove(i);
	}

	@Override
	public void addAllBox(MIMBoxList csList) {}

	public ItemStack getItem(int i)
	{
		return i >= 0 && i < itemList.size() ? itemList.get(i) : null;
	}

	public int getItemDamage(int i)
	{
		ItemStack item = getItem(i);

		return item != null ? item.getItemDamage() : 0;
	}

	public void registerItem(int x, int y, int z, int d, ItemStack itemstack)
	{
		if (isOnBoxList(x, y, z, d))
		{
			for (int i = 0; i < getListSize(); i++)
			{
				int[] pos = getBoxPos(i);
				int dim = getDimensionID(i);

				if (x == pos[0] && y == pos[1] && z == pos[2] && dim == d)
				{
					if (itemstack != null)
					{
						itemList.set(i, itemstack.copy());
					}
					else
					{
						itemList.set(i, null);
					}
				}
			}
		}
		else
		{
			addBox(x, y, z, d, itemstack);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if (!Strings.isNullOrEmpty(tagName))
		{
			super.writeToNBT(nbt);
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < itemList.size(); ++i)
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
		NBTTagList list = (NBTTagList)nbt.getTag(tagName + "Item");

		if (list != null)
		{
			itemList.clear();

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				itemList.add(data.getInteger("Index"), ItemStack.loadItemStackFromNBT(data));
			}
		}
	}
}