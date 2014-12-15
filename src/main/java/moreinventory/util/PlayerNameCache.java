package moreinventory.util;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Map;
import java.util.Map.Entry;

public class PlayerNameCache implements INBTSaveData
{
	private final Map<String, String> nameCache = Maps.newHashMap(); // <UUID,Name>

	public void refreshOwner(EntityPlayer player)
	{
		nameCache.put(player.getUniqueID().toString(), player.getDisplayName());
	}

	public String getName(String uuid)
	{
		String result = "Unknown";

		if (nameCache.containsKey(uuid))
		{
			result = nameCache.get(uuid);
		}

		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = (NBTTagList)nbt.getTag("PlayerNames");

		if (list != null)
		{
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound data = list.getCompoundTagAt(i);
				nameCache.put(data.getString("id"), data.getString("name"));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();

		for (Entry<String, String> owner : nameCache.entrySet())
		{
			NBTTagCompound data = new NBTTagCompound();
			data.setString("id", owner.getKey());
			data.setString("name", owner.getValue());
			list.appendTag(data);
		}

		nbt.setTag("PlayerNames", list);
	}
}