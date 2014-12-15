package moreinventory.util;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSaveData
{
	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}