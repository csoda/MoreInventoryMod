package moreinventory.util;

import com.google.common.base.Strings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MIMItemBoxList extends MIMBoxList
{
	private final HashMap<int[], ItemStack> itemMap = new HashMap<>();

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
			itemMap.put(new int[]{x, y, z, d}, null);
			flg = true;
		}

		registerItem(x, y, z, d, itemstack);

		return flg;
	}

	@Override
	public void removeBox(int i)
	{
		int[] pos = getBoxPos(i);
		itemMap.remove(pos);
		super.removeBox(i);
	}

	@Override
	public void addAllBox(MIMBoxList csList) {}

	public void putItem (int[] pos, ItemStack itemstack)
	{
		Set entrySet = itemMap.entrySet();
		for (Iterator t = entrySet.iterator(); t.hasNext();)
		{
			Map.Entry<int[],ItemStack> entry = (Map.Entry<int[], ItemStack>) t.next();
			if (Arrays.equals(pos, entry.getKey()))
			{
				entry.setValue(itemstack);
			}
		}
	}

	public ItemStack getItem(int i)
	{
		ItemStack itemstack = null;

		int[] pos = getBoxPos(i);

		Set entrySet = itemMap.entrySet();
		for (Iterator t = entrySet.iterator(); t.hasNext();)
		{
			Map.Entry<int[],ItemStack> entry = (Map.Entry<int[], ItemStack>) t.next();
			if (Arrays.equals(pos, entry.getKey()))
			{
				itemstack = entry.getValue();
			}
		}

		return itemstack;
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
			ItemStack newItem = itemstack != null ? itemstack.copy() : null;
			putItem(new int[]{x, y, z, d}, newItem);
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

			for (int i = 0; i < getListSize(); ++i)
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
			itemMap.clear();

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				itemMap.put(getBoxPos(data.getInteger("Index")), ItemStack.loadItemStackFromNBT(data));
			}
		}
	}
}