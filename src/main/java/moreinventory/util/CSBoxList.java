package moreinventory.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

public class CSBoxList implements IWorldDataSave{

	protected List<Integer> listX;
	protected List<Integer> listY;
	protected List<Integer> listZ;
	protected List<Integer> dimension;
	protected String tagName;
	
	public CSBoxList(){
		listX = new ArrayList();
		listY = new ArrayList();
		listZ = new ArrayList();
		dimension = new ArrayList();
	}
	
	public CSBoxList(String tag){
		this();
		tagName = tag;
	}
	
	public int getListSize(){
		return listX.size();
	}
	
	public int[] getBoxPos(int i){
		return new int[] {listX.get(i), listY.get(i), listZ.get(i)};
	}
	
	public int getDimensionID(int i){
		return dimension.get(i);
	}
	
	public TileEntity getTileBeyondDim(int i)
	{
		int[] pos = getBoxPos(i);
		return DimensionManager.getWorld(this.getDimensionID(i)).getTileEntity(pos[0], pos[1], pos[2]);
		
	}
	
	public boolean addBox(int x, int y, int z, int d){
		if(!isOnBoxList(x,y,z,d)){
			listX.add(x);
			listY.add(y);
			listZ.add(z);
			dimension.add(d);
			return true;
		}
		return false;
	}
	
	public boolean insBox(int index, int x, int y, int z, int d){
		if(!isOnBoxList(x,y,z,d)){
			listX.add(index, x);
			listY.add(index, y);
			listZ.add(index, z);
			dimension.add(index, d);
			return true;
		}
		return false;
	}
	
	public void removeBox(int i){
		listX.remove(i);
		listY.remove(i);
		listZ.remove(i);
		dimension.remove(i);
	}
	
	public void addAllBox(CSBoxList csList){
		listX.addAll(csList.listX);
		listY.addAll(csList.listY);
		listZ.addAll(csList.listZ);
		dimension.addAll(csList.dimension);
	}
	
	public boolean isOnBoxList(int x, int y, int z, int d){
		int[] pos = new int[3];
		int k = getListSize();
		for(int i = 0; i<k; i++){
			pos = getBoxPos(i);
			if(x==pos[0]&&y==pos[1]&&z==pos[2]&&dimension.get(i)==d){
				return true;
			}
		}
		return false;
	}
	
	public CSBoxList getDifference(CSBoxList list){
		CSBoxList newList = new CSBoxList();
		for(int i = 0; i < this.getListSize();i++)
		{
			boolean flg = true;
			int[] pos1 = this.getBoxPos(i);
			int dimID1 = this.getDimensionID(i);
			for(int t = 0; t < list.getListSize();t++)
			{
				int[] pos2 = list.getBoxPos(t);
				int dimID2 = list.getDimensionID(t);
				if(pos1[0]==pos2[0]&&pos1[1]==pos2[1]&&pos1[2]==pos2[2]&&dimID1 == dimID2)
				{
					flg = false;
					break;
				}
			}
			if(flg)
			{
				newList.addBox(pos1[0], pos1[1],pos1[2], dimID1);
			}
		}
		return newList;
	}
	
	public void writeToNBT(NBTTagCompound nbt){
		if(tagName != null){
			NBTTagList nbttaglist = new NBTTagList();
			int k = this.getListSize();
	        for (int i = 0; i < k; i++)
	        {
	            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
	            int[] pos = this.getBoxPos(i);
	            nbttagcompound1.setInteger("X", pos[0]);
	            nbttagcompound1.setInteger("Y", pos[1]);
	            nbttagcompound1.setInteger("Z", pos[2]);
	            nbttagcompound1.setInteger("D", dimension.get(i));
	            nbttaglist.appendTag(nbttagcompound1);
	        }
	        nbt.setTag(tagName, nbttaglist);
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		NBTTagList nbttaglist = nbt.getTagList(tagName, 10);
        for (int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.getCompoundTagAt(i);
            listX.add(nbttagcompound1.getInteger("X"));
            listY.add(nbttagcompound1.getInteger("Y"));
            listZ.add(nbttagcompound1.getInteger("Z"));
            dimension.add(nbttagcompound1.getInteger("D"));
        }
	}
}
