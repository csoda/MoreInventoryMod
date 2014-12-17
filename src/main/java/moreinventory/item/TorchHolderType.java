package moreinventory.item;

import com.google.common.collect.Maps;
import moreinventory.core.MoreInventoryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class TorchHolderType
{
	public static final Map<String, TorchHolderType> types = Maps.newLinkedHashMap();

	public int capacity;
	public String iconName, emptyIconName;

	public TorchHolderType(int capacity, String iconName, String emptyIconName)
	{
		this.capacity = capacity;
		this.iconName = iconName;
		this.emptyIconName = emptyIconName;
	}

	public static ItemStack createItemStack(String type)
	{
		ItemStack itemstack = new ItemStack(MoreInventoryMod.torchHolder);

		if (!types.containsKey(type))
		{
			return itemstack;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Type", type);
		itemstack.setTagCompound(nbt);

		return itemstack;
	}

	public static int getCapacity(String type)
	{
		if (!types.containsKey(type))
		{
			return 256;
		}

		return types.get(type).capacity;
	}

	public static void initialize()
	{
		types.put("Iron", new TorchHolderType(256, "moreinv:torchholder_iron", "moreinv:emptyholder_iron"));
		types.put("Gold", new TorchHolderType(1024, "moreinv:torchholder_gold", "moreinv:emptyholder_gold"));
		types.put("Diamond", new TorchHolderType(4096, "moreinv:torchholder_diamond", "moreinv:emptyholder_diamond"));
	}
}