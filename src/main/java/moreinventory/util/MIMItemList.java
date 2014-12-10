package moreinventory.util;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MIMItemList
{
	protected final List<ItemStack> list = Lists.newArrayList();
	protected final List<Integer> count = Lists.newArrayList();

	public int getItemCount(ItemStack itemstack)
	{
		int i = getItemIndex(itemstack);

		return i != -1 ? count.get(i) : 0;
	}

	public void addItem(ItemStack itemstack)
	{
		int size = itemstack.stackSize;
		int i = getItemIndex(itemstack);

		if (i != -1)
		{
			count.set(i, count.get(i) + size);
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
			if (MIMUtils.compareStacksWithDamage(list.get(i), itemstack))
			{
				return i;
			}
		}

		return -1;
	}
}