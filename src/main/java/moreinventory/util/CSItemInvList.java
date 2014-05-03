package moreinventory.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class CSItemInvList extends CSItemList implements IWorldDataSave{
	
	protected List<ItemStack[]> inv;
      protected String tagName;
	
	public CSItemInvList(){
		super();
		inv = new ArrayList();
	}
	
	public CSItemInvList(String tag){
            this();
            tagName = tag;
	}
	
	
	public ItemStack[] getInv(ItemStack itemstack){
		int t = this.getItemIndex(itemstack);
		return t != -1 ? inv.get(t) : new ItemStack[2];
	}

      @Override
	public void addItem(ItemStack itemstack){
		int size = itemstack.stackSize;
		int t = getItemIndex(itemstack);
		if(t==-1){
			registerItem(itemstack);
                  t = getItemIndex(itemstack);
		}
		int nowCount = count.get(t);
		if(nowCount<Integer.MAX_VALUE - size){
			count.set(t, nowCount + size);
		}
		else
		{
			count.set(t,Integer.MAX_VALUE);
		}
	}
	
	public void registerItem(ItemStack itemstack){
		if(itemstack != null &&getItemIndex(itemstack)==-1){
                  list.add(itemstack);
			count.add(0);
			inv.add(new ItemStack[2]);
		}
	}
	
	public ItemStack updateState(int index){
		if(index != -1){
			ItemStack[] items = inv.get(index);
			if(items[0]!=null){
				if(CSutil.compareStacksWithDamage(items[0], list.get(index))){
					this.addItem(items[0]);
                              items[0] = null;
				}
				else
				{
					return items[0];
				}
			}
			if(items[1]==null || items[1].stackSize != items[1].getMaxStackSize()){
				if(items[1] != null){
					this.addItem(items[1]);
				}
				items[1] = this.loadNewItemStack(index);
			}
		}
		return null;
	}
	
	public ItemStack loadNewItemStack(int index){
		if(index != -1){
			ItemStack itemstack = list.get(index).copy();
			if(itemstack != null){
				int size = itemstack.getMaxStackSize();
				int k = count.get(index);
				if(k < size)
                        {
                              size = k;
                        }
				count.set(index , count.get(index) - size);
				itemstack.stackSize = size;
				return itemstack;
			}
		}
		return null;
	}

	public void writeToNBT(NBTTagCompound nbt){
		if(tagName != null){
                  NBTTagList nbttaglist = new NBTTagList();
                  int k = list.size();
                  for (int i = 0; i < k; i++)
                  {
                        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                        NBTTagCompound itemNBT = new NBTTagCompound();
                        ItemStack contents = list.get(i);
                        if(contents != null){
                              contents.writeToNBT(itemNBT);
                        }
                        ItemStack itemstack = inv.get(i)[1];
                        int t = itemstack != null ? itemstack.stackSize : 0;
                        nbttagcompound1.setTag("Item", itemNBT);
                        nbttagcompound1.setInteger("Count", count.get(i)+t);
                        nbttaglist.appendTag(nbttagcompound1);
                  }
                  nbt.setTag(tagName, nbttaglist);
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt){
        NBTTagList nbttaglist = nbt.getTagList(tagName,10);
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
            list.add(ItemStack.loadItemStackFromNBT(nbttagcompound1.getCompoundTag("Item")));
            count.add(nbttagcompound1.getInteger("Count"));
            inv.add(new ItemStack[2]);
            updateState(i);
        }
	}
}
