package moreinventory.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StorageBoxOwnerList implements IWorldDataSave
{
	private final HashMap<String,String> ownerList; //<UUID ,displayedName>

	public StorageBoxOwnerList()
	{
		ownerList = new HashMap<String, String>();
	}

	public void updateOwner(EntityPlayer player)
	{
		String id = player.getUniqueID().toString();
		ownerList.put(id, player.getDisplayName());
	}

	public String getOwnerName(String UUID)
	{
		String result = "Unknown";

		if (ownerList.containsKey(UUID))
		{
			result = ownerList.get(UUID);
		}

		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList("OwnerList", 10);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound data = list.getCompoundTagAt(i);
			ownerList.put(data.getString("id"), data.getString("name"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();
		Set UUIDs = ownerList.entrySet();

		for (Iterator i = UUIDs.iterator(); i.hasNext();)
		{
			Map.Entry owner = (Map.Entry<String, String>)i.next();
			NBTTagCompound data = new NBTTagCompound();
			data.setString("id", (String)owner.getKey());
			data.setString("name", (String)owner.getValue());
			list.appendTag(data);
		}

		nbt.setTag("OwnerList", list);
	}
}
