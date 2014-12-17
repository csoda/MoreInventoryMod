package moreinventory.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class MIMBoxList implements INBTSaveData
{
	private final List<Integer> listX = Lists.newArrayList();
	private final List<Integer> listY = Lists.newArrayList();
	private final List<Integer> listZ = Lists.newArrayList();
	private final List<Integer> dimension = Lists.newArrayList();
	protected String tagName;

	public MIMBoxList() {}

	public MIMBoxList(String tag)
	{
		this.tagName = tag;
	}

	public int getListSize()
	{
		return listX.size();
	}

	public int[] getBoxPos(int i)
	{
		return new int[] {listX.get(i), listY.get(i), listZ.get(i)};
	}

	public int getDimensionID(int i)
	{
		return dimension.get(i);
	}

	public TileEntity getTileBeyondDim(int i)
	{
		int[] pos = getBoxPos(i);

		return DimensionManager.getWorld(getDimensionID(i)).getTileEntity(pos[0], pos[1], pos[2]);
	}

	public boolean addBox(int x, int y, int z, int d)
	{
		if (!isOnBoxList(x, y, z, d))
		{
			listX.add(x);
			listY.add(y);
			listZ.add(z);
			dimension.add(d);

			return true;
		}

		return false;
	}

	public void removeBox(int i)
	{
		listX.remove(i);
		listY.remove(i);
		listZ.remove(i);
		dimension.remove(i);
	}

	public void addAllBox(MIMBoxList csList)
	{
		listX.addAll(csList.listX);
		listY.addAll(csList.listY);
		listZ.addAll(csList.listZ);
		dimension.addAll(csList.dimension);
	}

	public boolean isOnBoxList(int x, int y, int z, int d)
	{
		for (int i = 0; i < getListSize(); i++)
		{
			int[] pos = getBoxPos(i);

			if (x == pos[0] && y == pos[1] && z == pos[2] && dimension.get(i) == d)
			{
				return true;
			}
		}

		return false;
	}

	public MIMBoxList getDifference(MIMBoxList list)
	{
		MIMBoxList newList = new MIMBoxList();

		for (int i = 0; i < getListSize(); i++)
		{
			boolean flag = true;
			int[] pos1 = getBoxPos(i);
			int dim1 = getDimensionID(i);

			for (int j = 0; j < list.getListSize(); j++)
			{
				int[] pos2 = list.getBoxPos(j);
				int dim2 = list.getDimensionID(j);

				if (pos1[0] == pos2[0] && pos1[1] == pos2[1] && pos1[2] == pos2[2] && dim1 == dim2)
				{
					flag = false;
					break;
				}
			}

			if (flag)
			{
				newList.addBox(pos1[0], pos1[1], pos1[2], dim1);
			}
		}

		return newList;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if (!Strings.isNullOrEmpty(tagName))
		{
			NBTTagList list = new NBTTagList();

			for (int i = 0; i < getListSize(); i++)
			{
				NBTTagCompound data = new NBTTagCompound();
				int[] pos = getBoxPos(i);
				data.setInteger("X", pos[0]);
				data.setInteger("Y", pos[1]);
				data.setInteger("Z", pos[2]);
				data.setInteger("D", dimension.get(i));
				list.appendTag(data);
			}

			nbt.setTag(tagName, list);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = (NBTTagList)nbt.getTag(tagName);

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				listX.add(data.getInteger("X"));
				listY.add(data.getInteger("Y"));
				listZ.add(data.getInteger("Z"));
				dimension.add(data.getInteger("D"));
			}
		}
	}
}