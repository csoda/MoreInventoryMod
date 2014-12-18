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
	private final List<int[]> posList = Lists.newArrayList();
	protected String tagName;

	public MIMBoxList() {}

	public MIMBoxList(String tag)
	{
		this.tagName = tag;
	}

	public int getListSize()
	{
		return posList.size();
	}

	public int[] getBoxPos(int i)
	{
		return posList.get(i);
	}

	public int getDimensionID(int i)
	{
		return posList.get(i)[3];
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
			posList.add(new int[] {x, y, z, d});
			return true;
		}

		return false;
	}

	public void removeBox(int i)
	{
		posList.remove(i);
	}

	public void addAllBox(MIMBoxList csList)
	{
		posList.addAll(getDifference(csList).posList);
	}

	public boolean isOnBoxList(int x, int y, int z, int d)
	{
		for (int[] pos : posList)
		{
			if (x == pos[0] && y == pos[1] && z == pos[2] && d == pos[3])
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
				data.setInteger("D", pos[3]);
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
				posList.add(new int[]{data.getInteger("X"), data.getInteger("Y"), data.getInteger("Z"), data.getInteger("D")});
			}
		}
	}
}