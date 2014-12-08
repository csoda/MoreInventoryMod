package moreinventory.util;

import net.minecraft.nbt.NBTTagCompound;

public interface IWorldDataSave
{
	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}