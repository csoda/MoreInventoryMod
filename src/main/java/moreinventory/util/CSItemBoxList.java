package moreinventory.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CSItemBoxList extends CSBoxList{
	
	protected List<ItemStack> itemList;
	
	public CSItemBoxList(){
		super();
		itemList = new ArrayList();
	}
	
	public CSItemBoxList(World world,String tag){
		this();
		tagName = tag;
	}
	
	@Override
	public boolean addBox(int x,int y,int z, int d){
		return this.addBox(x,y,z,d,null);
	}
	
	public boolean addBox(int x, int y, int z , int d, ItemStack item){
		if(!isOnBoxList(x, y, z, d)){
			super.addBox(x,y,z,d);
            itemList.add(item);
			return true;
		}
		else
		{
			this.registerItem(x, y, z, d, item);
		}
		return false;
	}
	
	@Override
	public boolean insBox(int index, int x, int y, int z, int d){
		return this.insBox(index,x,y,z,d,null);
	}
	
	public boolean insBox(int index, int x, int y, int z , int d, ItemStack item){
		super.insBox(index, x, y, z, d);
            itemList.add(index,item);
		return true;
	}
	
	public void removeBox(int i){
		super.removeBox(i);
            itemList.remove(i);
	}
	
	@Override
	public void addAllBox(CSBoxList csList){}
	
	public void addAllBoxList(CSItemBoxList csList){
		super.addAllBox(csList);
            itemList.addAll(csList.itemList);
	}
	
	public ItemStack getItem(int i){
		return i < itemList.size() ? itemList.get(i) : null;
	}
	
	public int getItemDamage(int i){
            ItemStack item = null;
            if(i < itemList.size()){
                  item = itemList.get(i);
            }
		return item != null ? item.getItemDamage() : 0;
	}
	
	public void registerItem(int x, int y, int z ,int d, ItemStack item){
		int[] pos = new int[3];
		int dimID;
		boolean flg = false;
		int k = getListSize();
		for(int i = 0; i < k; i++){
			pos = getBoxPos(i);
			dimID = getDimensionID(i);
			if(x == pos[0] && y == pos[1] && z == pos[2] && dimID == d){
                        itemList.set(i,item);
				flg = true;
			}
		}
		if(!flg)
		{
			this.addBox(x, y, z, d, item);
		}
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
            if(tagName != null){
                  super.writeToNBT(nbt);
                  NBTTagList nbttaglist = new NBTTagList();
                  int k = this.getListSize();
                   for (int i = 0; i < k; i++)
                   {
                          ItemStack item = itemList.get(i);
                          if(item != null){
                               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                              nbttagcompound1.setInteger("Index",i);
                              item.writeToNBT(nbttagcompound1);
                              nbttaglist.appendTag(nbttagcompound1);
                          }
                   }
              nbt.setTag(tagName + "Item", nbttaglist);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList(tagName + "Item" , 10);
            itemList = new ArrayList<ItemStack>(this.getListSize());
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                  NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
                  int k = nbttagcompound1.getInteger("Index");
                  itemList.add(k,ItemStack.loadItemStackFromNBT(nbttagcompound1));
            }
	}
}
