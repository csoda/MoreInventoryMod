package moreinventory.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class CSItemList
{

	protected List<ItemStack> list;
	protected List<Integer> count;

	public CSItemList()
	{
		list = new ArrayList();
		count = new ArrayList();
	}

	public int getItemCount(ItemStack itemstack)
	{
		int t = getItemIndex(itemstack);
		return t != -1 ? count.get(t) : 0;
	}

	public void addItem(ItemStack itemstack)
	{
		int size = itemstack.stackSize;
		int t = getItemIndex(itemstack);
		if (t != -1)
		{
			count.set(t, count.get(t) + size);
		}
		else
		{
			list.add(itemstack);
			count.add(size);
		}
	}

	public int getItemIndex(ItemStack itemstack)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (CSUtil.compareStacksWithDamage(list.get(i), itemstack))
			{
				return i;
			}
		}
		return -1;
	}
}
